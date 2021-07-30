package com.rallyhealth.kupo.slack.interactionpayload

import play.api.libs.json.{Json, Reads}

case class PayloadUser(
  id: String,
  username: String,
  team_id: String
)

object PayloadUser {
  implicit val reads: Reads[PayloadUser] = Json.reads[PayloadUser]
}

case class PayloadTeam(
  id: String
)

object PayloadTeam {
  implicit val reads: Reads[PayloadTeam] = Json.reads[PayloadTeam]
}

case class PayloadAction(
  action_id: String,
  `type`: String,
  action_ts: String,
  value: Option[String]
)

object PayloadAction {
  implicit val reads: Reads[PayloadAction] = Json.reads[PayloadAction]
}

case class PayloadChannel(
  id: String,
  name: String
)

object PayloadChannel {
  implicit val reads: Reads[PayloadChannel] = Json.reads[PayloadChannel]
}

/**
  * @param user user's unique Slack id, internally generated
  * @param ts timestamp
  */
case class PayloadMessage(
  bot_id: Option[String],
  `type`: String,
  text: String,
  user: String,
  ts: String
)
