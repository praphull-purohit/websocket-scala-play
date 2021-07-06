package service.models

import play.api.libs.json.{Json, OFormat}

case class IncomingRequest(`type`: String, text: String)

object IncomingRequest {
  implicit val format: OFormat[IncomingRequest] = Json.format[IncomingRequest]
}