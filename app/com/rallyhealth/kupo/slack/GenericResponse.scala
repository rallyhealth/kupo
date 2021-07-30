package com.rallyhealth.kupo.slack

import play.api.libs.json.{Format, Json}

case class GenericResponse(
  ok: Boolean,
  error: Option[String],
)

object GenericResponse {

  implicit val format: Format[GenericResponse] = Json.format[GenericResponse]

}
