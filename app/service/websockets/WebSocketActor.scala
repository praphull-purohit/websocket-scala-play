package service.websockets

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import service.models.OutgoingResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class WebSocketActor(id: String, caller: ActorRef, manager: ActorRef) extends Actor {

  import WebSocketActor._

  override def preStart(): Unit = {
    super.preStart()
    context.system.scheduler.scheduleOnce(10.millis, manager,
      WebSocketManager.Register(id, context.self))
  }

  override def receive: Receive = {
    case "ping" => caller ! OutgoingResponse(id, "pong")
    case "die" => self ! PoisonPill
    case InternalMessage(contents) => caller ! OutgoingResponse(id, contents)
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