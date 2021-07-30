package com.rallyhealth.kupo.slack

import play.api.libs.json.{Format, JsObject, JsResult, JsValue, Json, OFormat}

import scala.util.Try

case class WebhookAccessInfo(url: String, channel: String, configuration_url: String)

object WebhookAccessInfo {

  implicit val format: OFormat[WebhookAccessInfo] = Json.format[WebhookAccessInfo]

}

case class BotAccessInfo(bot_user_id: String, bot_access_token: String)

object BotAccessInfo {

  implicit val format: OFormat[BotAccessInfo] = Json.format[BotAccessInfo]

}

case class AuthedUserAccessInfo(
  id: String,
  scope: String,
  access_token: String,
  token_type: String
)

object AuthedUserAccessInfo {
  implicit val format: OFormat[AuthedUserAccessInfo] = Json.format[AuthedUserAccessInfo]
}

sealed trait OAuthAccessInfo

case class OAuthV1AccessInfo(
  access_token: String,
  scope: String,
  team_name: String,
  team_id: String,
  incoming_webhook: Option[WebhookAccessInfo],
  authed_user: Option[AuthedUserAccessInfo],
  bot: Option[BotAccessInfo]
) extends OAuthAccessInfo

object OAuthV1AccessInfo {
  implicit val format: OFormat[OAuthV1AccessInfo] = Json.format[OAuthV1AccessInfo]
}

case class OAuthV2Team(
  name: String,
  id: String
)

object OAuthV2Team {
  implicit val format: Format[OAuthV2Team] = Json.format[OAuthV2Team]
}

/**
  *
  * @param scope A comma-delimited list of approved scopes, but returned as type String
  */
case class OAuthV2AccessInfo(
  access_token: String,
  scope: String,
  team: OAuthV2Team,
  authed_user: Option[AuthedUserAccessInfo],
  bot_user_id: String,
  token_type: String
) extends OAuthAccessInfo

object OAuthV2AccessInfo {
  implicit val format: Format[OAuthV2AccessInfo] = Json.format[OAuthV2AccessInfo]
}

object OAuthAccessInfo {
  implicit val format: Format[OAuthAccessInfo] = Json.format[OAuthAccessInfo]

}
