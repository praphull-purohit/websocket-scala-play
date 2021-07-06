package service.websockets

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import service.models.OutgoingResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class WebSocketActor(id: String, caller: ActorRef, manager: ActorRef)
  extends Actor with ActorLogging {

  import WebSocketActor._

  override def preStart(): Unit = {
    super.preStart()
    context.system.scheduler.scheduleOnce(10.millis, manager,
      WebSocketManager.Register(id, context.self))
    context.system.scheduler.scheduleOnce(1.second, self,
      InternalMessage("Initiated"))
    //Maximum time for WebSocket to be alive: 15 sec
    context.system.scheduler.scheduleOnce(15.seconds, self, "die")
  }

  override def receive: Receive = {
    case "ping" =>
      log.info(s"Received ping")
      caller ! OutgoingResponse(id, "pong")
    case "die" =>
      log.info(s"Received die")
      self ! PoisonPill
    case InternalMessage(contents) => caller ! OutgoingResponse(id, contents)
    case text: String => caller ! OutgoingResponse(id, s"Received text: $text")
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
}