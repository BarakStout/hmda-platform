package hmda.auth

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model.headers.{ Authorization, OAuth2BearerToken }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.Materializer
import com.typesafe.config.{ Config, ConfigFactory }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import hmda.api.http.model.ErrorResponse
import org.keycloak.adapters.KeycloakDeploymentBuilder
import org.keycloak.representations.adapters.config.AdapterConfig
import org.slf4j.Logger

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
// $COVERAGE-OFF$
class OAuth2Authorization(logger: Logger, tokenVerifier: TokenVerifier) {

  val config      = ConfigFactory.load()
  val clientId    = config.getString("keycloak.client.id")
  val runtimeMode = config.getString("hmda.runtime.mode")

  def authorizeTokenWithLeiOrRole(lei: String, role: String): Directive1[VerifiedToken] =
    handleRejections(authRejectionHandler) & (authorizeTokenWithLeiReject(lei) | authorizeTokenWithRoleReject(role))

  def authorizeTokenWithRoleReject(role: String): Directive1[VerifiedToken] =
    authorizeToken flatMap {
      case t if t.roles.contains(role) =>
        provide(t)
      case _ =>
        if (runtimeMode == "dev" || runtimeMode == "docker-compose") {
          provide(VerifiedToken())
        } else {
          reject(AuthorizationFailedRejection).toDirective[Tuple1[VerifiedToken]]
        }
    }

  protected def authRejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle({
        case AuthorizationFailedRejection =>
          complete(
            (StatusCodes.Forbidden, ErrorResponse(StatusCodes.Forbidden.intValue, "Authorization Token could not be verified", Path("")))
          )
      })
      .result()

  protected def authorizeTokenWithLeiReject(lei: String): Directive1[VerifiedToken] =
    authorizeToken flatMap {
      case t if t.lei.nonEmpty =>
        withLocalModeBypass {
          val leiList = t.lei.split(',')
          if (leiList.contains(lei.trim())) {
            provide(t)
          } else {
            logger.info(s"Providing reject for ${lei.trim()}")
            reject(AuthorizationFailedRejection).toDirective[Tuple1[VerifiedToken]]
          }
        }

      case _ =>
        withLocalModeBypass {
          logger.info("Rejecting request in authorizeTokenWithLei")
          reject(AuthorizationFailedRejection).toDirective[Tuple1[VerifiedToken]]
        }
    }

  protected def withLocalModeBypass(thunk: => Directive1[VerifiedToken]): Directive1[VerifiedToken] =
    if (runtimeMode == "dev" || runtimeMode == "docker-compose") {
      provide(VerifiedToken())
    } else { thunk }

  def authorizeTokenWithRole(role: String): Directive1[VerifiedToken] =
    handleRejections(authRejectionHandler) & authorizeTokenWithRoleReject(role)

  def authorizeTokenWithLei(lei: String): Directive1[VerifiedToken] =
    authorizeToken flatMap {
      case t if t.lei.nonEmpty =>
        if (runtimeMode == "dev" || runtimeMode == "docker-compose") {
          provide(t)
        } else {
          val leiList = t.lei.split(',')
          if (leiList.contains(lei.trim())) {
            provide(t)
          } else {
            logger.info(s"Providing reject for ${lei.trim()}")
            complete(
              (StatusCodes.Forbidden, ErrorResponse(StatusCodes.Forbidden.intValue, "Authorization Token could not be verified", Path("")))
            ).toDirective[Tuple1[VerifiedToken]]
          }
        }

      case _ =>
        if (runtimeMode == "dev" || runtimeMode == "docker-compose") {
          provide(VerifiedToken())
        } else {
          logger.info("Rejecting request in authorizeTokenWithLei")
          complete(
            (StatusCodes.Forbidden, ErrorResponse(StatusCodes.Forbidden.intValue, "Authorization Token could not be verified", Path("")))
          ).toDirective[Tuple1[VerifiedToken]]
        }
    }

  def authorizeToken: Directive1[VerifiedToken] =
    bearerToken.flatMap {
      case Some(token) =>
        onComplete(tokenVerifier.verifyToken(token)).flatMap {
          _.map { t =>
            val lei: String = if (t.getOtherClaims.containsKey("lei")) {
              t.getOtherClaims.get("lei").toString
            } else ""

            provide(
              VerifiedToken(
                token,
                t.getId,
                t.getName,
                t.getPreferredUsername,
                t.getEmail,
                t.getResourceAccess().get(clientId).getRoles.asScala.toSeq,
                lei
              )
            )
          }.recover {
            case ex: Throwable =>
              logger.error("Authorization Token could not be verified {}", ex)
              complete(
                (
                  StatusCodes.Forbidden,
                  ErrorResponse(StatusCodes.Forbidden.intValue, "Authorization Token could not be verified", Path(""))
                )
              ).toDirective[Tuple1[VerifiedToken]]
          }.get
        }
      case None =>
        if (runtimeMode == "dev"  || runtimeMode == "docker-compose") {
          provide(VerifiedToken())
        } else {
          val r: Route = (extractRequest { req =>
            import scala.compat.java8.OptionConverters._
            logger.error("No bearer token, authz header [{}]" + req.getHeader("authorization").asScala)
            complete(
              (StatusCodes.Forbidden, ErrorResponse(StatusCodes.Forbidden.intValue, "Authorization Token could not be verified", Path("")))
            )
          })
          StandardRoute(r).toDirective[Tuple1[VerifiedToken]]
        }
    }

  private def bearerToken: Directive1[Option[String]] =
    for {
      authBearerHeader <- optionalHeaderValueByType(classOf[Authorization])
        .map(extractBearerToken)
      xAuthCookie <- optionalCookie("X-Authorization-Token").map(_.map(_.value))
    } yield authBearerHeader.orElse(xAuthCookie)

  private def extractBearerToken(authHeader: Option[Authorization]): Option[String] =
    authHeader.collect {
      case Authorization(OAuth2BearerToken(token)) => token
    }

}

object OAuth2Authorization {
  def apply(log: Logger, config: Config)(implicit system: ActorSystem[_], mat: Materializer, ec: ExecutionContext): OAuth2Authorization = {
    val authUrl       = config.getString("keycloak.auth.server.url")
    val keycloakRealm = config.getString("keycloak.realm")
    val apiClientId   = config.getString("keycloak.client.id")
    val adapterConfig = new AdapterConfig()
    adapterConfig.setRealm(keycloakRealm)
    adapterConfig.setAuthServerUrl(authUrl)
    adapterConfig.setResource(apiClientId)
    val keycloakDeployment = KeycloakDeploymentBuilder.build(adapterConfig)
    OAuth2Authorization(log, new KeycloakTokenVerifier(keycloakDeployment))
  }

  def apply(logger: Logger, tokenVerifier: TokenVerifier): OAuth2Authorization =
    new OAuth2Authorization(logger, tokenVerifier)
}
// This is just a Guardian for starting up the API
// $COVERAGE-OFF$
