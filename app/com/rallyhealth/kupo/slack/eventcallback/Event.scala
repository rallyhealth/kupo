package com.rallyhealth.kupo.slack.eventcallback

import play.api.libs.json.{JsValue, _}

sealed trait SlackEvent {
  def `type`: String
}


/**
  * So far, anything other than a [[ChannelEvent]]
  * @param bot_id Present in, for example, a DM from the bot itself. Empty if from human user.
 *  @param text the message itself
 *  @param user Slack-generated unique user ID
 *  @param ts timestamp, e.g., 1610045060.004100
 *  @param team Slack-generated unique workspace(?) ID
 *  @param thread_ts Used as the id for a thread if we're in one
 *  @param channel Slack-generated unique channel ID
 *  @param channel_type "im" == direct message
 *  Also "blocks". Do we care?
 *
  */
case class MessageEvent(
  override val `type`: String, // always "message"?
  bot_id: Option[String],
  text: String,
  channel: String,
  channel_type: Option[String],
  event_ts: String,
  ts: String,
  thread_ts: Option[String],
  subtype: Option[String],
  user: Option[String],
  team: Option[String]
) extends SlackEvent

case class MessageDeletedEvent(
  override val `type`: String,
  deleted_ts: String,
  channel: String,
  event_ts: String,
  ts: Option[String],
  channel_type: String
) extends SlackEvent

case class ChannelEvent(
  override val `type`: String,
  team: Option[String],
  channel: ChannelBody
) extends SlackEvent

sealed trait ChannelBody

case class ChannelCreatedBody(
  id: String,
  is_channel: Option[Boolean],
  name: String,
  name_normalized: Option[String],
  creator: String,
  is_shared: Option[Boolean],
  is_org_shared: Option[Boolean]
) extends ChannelBody

object MessageDeletedEvent {
  implicit val format: Reads[MessageDeletedEvent] = Json.reads[MessageDeletedEvent]
}

object MessageEvent {
  implicit val format: Format[MessageEvent] = Json.format[MessageEvent]
}

object ChannelCreatedBody {
  implicit val format: Format[ChannelCreatedBody] = Json.format[ChannelCreatedBody]
}

object ChannelBody {
  implicit val reads: Reads[ChannelBody] = Json.reads[ChannelCreatedBody].widen[ChannelBody]
}

object ChannelEvent {
  implicit val reads: Reads[ChannelEvent] = Json.reads[ChannelEvent]
}

object SlackEvent {

  implicit val reads: Reads[SlackEvent] = new Reads[SlackEvent] {
    override def reads(
      json: JsValue
    ): JsResult[SlackEvent] = {
      (json \ "subtype").toOption match {
        case Some(JsString("message_deleted")) => Json.fromJson[MessageDeletedEvent](json)
        case _ => Json.fromJson[MessageEvent](json).orElse(Json.fromJson[ChannelEvent](json))
      }
    }
  }
}
