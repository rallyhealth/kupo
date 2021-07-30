package com.rallyhealth.kupo.slack

import play.api.libs.json.{Format, Json}

case class AuthTestResponse(
  ok: Boolean,
  error: Option[String],
  url: Option[String],
  team: Option[String],
  user: Option[String],
  team_id: Option[String],
  user_id: Option[String],
)

object AuthTestResponse {

  implicit val format: Format[AuthTestResponse] = Json.format[AuthTestResponse]

}
