package service.websockets

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import org.slf4j.LoggerFactory
import service.models.{IncomingRequest, OutgoingResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class WebSocketActor(id: String, caller: ActorRef, manager: ActorRef)
  extends Actor {
  private val log = LoggerFactory.getLogger("ws-actor")

  import WebSocketActor._

  override def preStart(): Unit = {
    super.preStart()
    manager ! WebSocketManager.Register(id, context.self)
    //Maximum total time for WebSocket to be alive: 30 sec
    context.system.scheduler.scheduleOnce(30.seconds, self, Die("No authorization provided!"))
  }

  override def receive: Receive = unauthorized

  private def deathHandler: Receive = {
    case Die(reason) =>
      log.info(s"Died due to reason: $reason")
      self ! PoisonPill
  }

  def unauthorized: Receive = deathHandler.orElse {
    case IncomingRequest(requestType, token) if requestType.equals("Authorization") =>
      if (token.equals("Yo!")) {
        log.info(s"Authorized request for $id")
        context.become(authorized)
        //Time for WebSocket to be alive after authorization: 15 sec
        context.system.scheduler.scheduleOnce(15.seconds, self, Die("Timeout post authorization"))
      } else {
        log.error(s"Invalid token sent for $id")
        caller ! OutgoingResponse(id, "Invalid token!")
        self ! Die("Invalid token!")
      }
    case IncomingRequest(_, _) =>
      log.warn(s"Awaiting authorization for $id. Ignored the current message")
      caller ! OutgoingResponse(id, "Awaiting authorization")

  }

  def authorized: Receive = deathHandler.orElse {
    case InternalMessage(contents) => caller ! OutgoingResponse(id, contents)
    case IncomingRequest(requestType, text) =>
      val logMessage = s"Received request of type $requestType with text: $text"
      log.debug(logMessage)
      caller ! OutgoingResponse(id, logMessage)
    case other => log.debug(s"Received unsupported message $other")
  }

  override def postStop(): Unit = {
    super.postStop()
    //Any resource cleanup when WebSocket terminates
  }
}

object WebSocketActor {
  def props(id: String, caller: ActorRef, manager: ActorRef): Props =
    Props(new WebSocketActor(id, caller, manager))

  case class InternalMessage(contents: String)

  case class Authorization(token: String)

  private case class Die(reason: String)
}