package service

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.Inject
import service.websockets.WebSocketManager

class WebSocketResponseService @Inject()(actorSystem: ActorSystem) {

  lazy val webSocketManager: ActorRef =
    actorSystem.actorOf(WebSocketManager.props, "ws-manager")

  def respond(id: String, data: String): Unit = {
    webSocketManager ! WebSocketManager.Notify(id, data)
  }

}
