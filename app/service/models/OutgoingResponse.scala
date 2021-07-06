package service.models

import play.api.libs.json.{Json, OFormat}

case class OutgoingResponse(id: String, data: String)

object OutgoingResponse {
  implicit val formats: OFormat[OutgoingResponse] = Json.format[OutgoingResponse]
}
