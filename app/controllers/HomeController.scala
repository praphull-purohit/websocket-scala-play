package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import play.api.libs.json.{JsObject, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import service.WebSocketResponseService
import service.models.{IncomingRequest, OutgoingResponse}
import service.websockets.WebSocketActor

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
    MessageFlowTransformer.jsonMessageFlowTransformer[IncomingRequest, OutgoingResponse]

  def startListening(id: String) = WebSocket.accept[IncomingRequest, OutgoingResponse] { _ =>
    ActorFlow.actorRef { out =>
      WebSocketActor.props(id, out, webSocketResponseService.webSocketManager)
    }
    /*request.headers.get("Authorization") match {
      case Some(BearerToken(token)) =>
        if (token.equals("Yo!")) {
          Future.successful(Right(ActorFlow.actorRef { out =>
            WebSocketActor.props(id, out, webSocketResponseService.webSocketManager)
          }))
        } else {
          Future.successful(Left(Unauthorized(failure(1003, "Invalid token"))))
        }
      case Some(_) =>
        Future.successful(Left(Forbidden(failure(1002, "Invalid auth header"))))
      case None => Future.successful(Left(Forbidden(failure(1001, "Missing auth header"))))
    }*/
  }

  def respond(id: String) = Action(parse.json) { request =>
    (request.body \ "data").asOpt[String] match {
      case Some(data) =>
        webSocketResponseService.respond(id, data)
        Ok
      case None => BadRequest(failure(2001, "Missing data field"))
    }
  }

  def login() = Action { implicit request =>
    val url = routes.HomeController.startListening(":id").webSocketURL()
    Ok(views.html.login(url))
  }

  def profile() = Action {
    Ok(views.html.profile())
  }
}

//noinspection TypeAnnotation
object HomeController {
  val BearerToken = "^Bearer (.+)$".r
}