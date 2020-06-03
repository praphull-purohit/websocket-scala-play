package controllers

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def test() = Action(parse.json) { request =>
    (request.body \ "name").asOpt[String] match {
      case Some(name) => Ok(Json.obj("name" -> name))
      case None => BadRequest("Invalid")
    }
  }
}
