package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import controllers.HomeController.BearerToken
import play.api.libs.json.{JsObject, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import service.WebSocketResponseService
import service.models.OutgoingResponse
import service.websockets.WebSocketActor

import scala.concurrent.Future

//noinspection TypeAnnotation
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               webSocketResponseService: WebSocketResponseService)
                              (implicit system: ActorSystem, mat: Materializer)
  extends BaseController {
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def test() = Action(parse.json) { request =>
    (request.body \ "name").asOpt[String] match {
      case Some(name) => Ok(Json.obj("name" -> name))
      case None => BadRequest("Invalid")
    }
  }

  private def failure(code: Int, error: String): JsObject =
    Json.obj("code" -> code, "message" -> error)

  private implicit val messageFlowTransformer =
    MessageFlowTransformer.jsonMessageFlowTransformer[String, OutgoingResponse]

  def startListening(id: String) = WebSocket.acceptOrResult[String, OutgoingResponse] { request =>
    request.headers.get("Authorization") match {
      case Some(BearerToken(token)) =>
        if (token.equals("Yo!")) {
          Future.successful(Right(ActorFlow.actorRef { out =>
            WebSocketActor.props(id, out, webSocketResponseService.webSocketManager)
          }))
        } else {
          Future.successful(Left(Forbidden(failure(1003, "Invalid token"))))
        }
      case Some(_) =>
        Future.successful(Left(Forbidden(failure(1002, "Invalid auth header"))))
      case None => Future.successful(Left(Forbidden(failure(1001, "Missing auth header"))))
    }
  }

  def respond(id: String) = Action(parse.json) { request =>
    (request.body \ "data").asOpt[String] match {
      case Some(data) =>
        webSocketResponseService.respond(id, data)
        Ok
      case None => BadRequest(failure(2001, "Missing data field"))
    }
  }
}

//noinspection TypeAnnotation
object HomeController {
  val BearerToken = "^Bearer (.+)$".r
}