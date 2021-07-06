package service.models

import play.api.libs.json.{Json, OFormat}

import java.util.Date

case class OutgoingResponse(id: String, data: String, eventTime: Long)

object OutgoingResponse {
  implicit val formats: OFormat[OutgoingResponse] = Json.format[OutgoingResponse]

  def apply(id: String, data: String): OutgoingResponse =
    OutgoingResponse(id, data, new Date().getTime)
}
