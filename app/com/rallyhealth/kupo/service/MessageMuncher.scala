package com.rallyhealth.kupo.service

import com.rallyhealth.kupo.slack.eventcallback.{MessageEvent, SlackEvent}

object MessageMuncher {

  def isEventChannelCreated(event: SlackEvent): Boolean = {
    event.`type` == "channel_created"
  }

  def isBangCommand(event: MessageEvent): Boolean = {
    event.text.startsWith("!")
  }

  /**
    * NOTE: Slack does not consider slackbot to be a bot.
    * @param event The incoming event
    * @return True if a 3rd party bot sent this message
    */
  def isBotMessage(event: MessageEvent): Boolean = {
    event.subtype.contains("bot_message") || event.bot_id.isDefined
  }

  def isDirectMessage(event: MessageEvent): Boolean = {
    event.channel_type.contains("im")
  }

  def isAtMentioned(event: MessageEvent): Boolean = {
    event.`type` == "app_mention"
  }

  def isKeywordMentioned(event: MessageEvent): Boolean = ???

}
