package hmda.api.http.admin

import akka.actor.{ActorSystem, Scheduler}
import akka.event.LoggingAdapter
import akka.stream.ActorMaterializer
import akka.util.Timeout
import akka.http.scaladsl.server.Directives._
import akka.actor.typed.scaladsl.adapter._
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Route
import hmda.model.institution.Institution
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import hmda.api.http.directives.HmdaTimeDirectives
import hmda.persistence.institution.InstitutionPersistence._
import hmda.api.http.codec.institution.InstitutionCodec._
import hmda.persistence.institution.InstitutionPersistence

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait InstitutionAdminHttpApi extends HmdaTimeDirectives {

  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  val log: LoggingAdapter
  implicit val ec: ExecutionContext
  implicit val timeout: Timeout
  val sharding: ClusterSharding

  val institutionWritePath =
    path("institutions") {
      entity(as[Institution]) { institution =>
        //TODO: make this check better, it blows up the response
        require(
          institution.LEI.isDefined && institution.LEI.getOrElse("") != "")
        val typedSystem = system.toTyped
        implicit val scheduler: Scheduler = typedSystem.scheduler

        val institutionPersistence = sharding.entityRefFor(
          InstitutionPersistence.ShardingTypeName,
          s"${InstitutionPersistence.name}-${institution.LEI.getOrElse("")}")

        timedPost { uri =>
          val fCreated: Future[InstitutionCreated] = institutionPersistence ? (
              ref => CreateInstitution(institution, ref))

          onComplete(fCreated) {
            case Success(InstitutionCreated(i)) =>
              complete(ToResponseMarshallable(StatusCodes.Created -> i))
            case Failure(error) => complete(error.getLocalizedMessage)
          }
        } ~
          timedPut { uri =>
            val fModified: Future[InstitutionEvent] = institutionPersistence ? (
                ref => ModifyInstitution(institution, ref)
            )

            onComplete(fModified) {
              case Success(InstitutionModified(i)) =>
                complete(ToResponseMarshallable(StatusCodes.Accepted -> i))
              case Success(InstitutionNotExists) =>
                complete(
                  ToResponseMarshallable(HttpResponse(StatusCodes.NotFound)))
              case Failure(error) => complete(error.getLocalizedMessage)
            }
          } ~
          timedDelete { uri =>
            val fDeleted: Future[InstitutionEvent] = institutionPersistence ? (
                ref => DeleteInstitution(institution.LEI.getOrElse(""), ref)
            )

            onComplete(fDeleted) {
              case Success(InstitutionDeleted(lei)) =>
                complete(ToResponseMarshallable(StatusCodes.Accepted -> lei))
              case Success(InstitutionNotExists) =>
                complete(ToResponseMarshallable(StatusCodes.NotFound))
              case Failure(error) => complete(error.getLocalizedMessage)
            }
          }

      }
    }

  val institutionReadPath =
    path("institutions" / Segment) { lei =>
      val typedSystem = system.toTyped
      implicit val scheduler: Scheduler = typedSystem.scheduler

      val institutionPersistence =
        sharding.entityRefFor(InstitutionPersistence.ShardingTypeName,
                              s"${InstitutionPersistence.name}-$lei")

      timedGet { uri =>
        val fInstitution
          : Future[Option[Institution]] = institutionPersistence ? (
            ref => Get(ref)
        )

        onComplete(fInstitution) {
          case Success(Some(i)) =>
            complete(ToResponseMarshallable(i))
          case Success(None) =>
            complete(ToResponseMarshallable(HttpResponse(StatusCodes.NotFound)))
          case Failure(error) =>
            complete(error.getLocalizedMessage)
        }
      }
    }

  def institutionAdminRoutes: Route = encodeResponse {
    institutionWritePath ~ institutionReadPath
  }

}
