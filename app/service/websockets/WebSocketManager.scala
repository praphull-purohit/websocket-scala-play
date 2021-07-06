package service.websockets

import akka.actor.{Actor, ActorRef, Props}
import org.slf4j.LoggerFactory

class WebSocketManager extends Actor {
  private val log = LoggerFactory.getLogger("ws-manager")

  import WebSocketManager._

  override def receive: Receive = update(Map.empty)

  def update(activeListeners: Map[String, ActorRef]): Receive = {
    case Register(id, actor) =>
      log.info(s"Registered $id")
      context.become(update(activeListeners + (id -> actor)))
      context.watchWith(actor, DeRegister(id))
    case DeRegister(id) =>
      log.info(s"De-registered $id")
      context.become(update(activeListeners - id))
    case Notify(id, message) =>
      log.info(s"Notifying $id with message $message")
      activeListeners.get(id) match {
        case Some(actor) => actor ! WebSocketActor.InternalMessage(message)
        case None => log.warn(s"Ignoring message notification received for id $id, since no such WebSocket actor is registered")
      }
  }
}

object WebSocketManager {
  case class Register(id: String, actor: ActorRef)

  case class Notify(id: String, message: String)

  private case class DeRegister(id: String)

  def props: Props = Props(new WebSocketManager)
}