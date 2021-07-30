package com.rallyhealth.kupo.slack.interactionpayload

import play.api.libs.json.{Json, Reads}

/**
  * Interaction Payloads
  * See https://api.slack.com/interactivity/handling#payloads
  */

case class BlockAction(
  `type`: String,
  team: Option[PayloadTeam],
  user: PayloadUser,
  actions: Seq[PayloadAction],
  trigger_id: String,
  channel: PayloadChannel,
  response_url: String
)

object BlockAction {
  implicit val reads: Reads[BlockAction] = Json.reads[BlockAction]
}
