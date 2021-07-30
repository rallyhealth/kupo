package com.rallyhealth.kupo.slack.eventcallback

import enumeratum.EnumEntry.{LowerCamelcase, Lowercase, Snakecase}
import enumeratum.{EnumEntry, PlayEnum}

// from https://api.slack.com/events
sealed trait EventType extends EnumEntry

object EventType extends PlayEnum[EventType] with Lowercase with Snakecase {

  override def values: IndexedSeq[EventType] = findValues
  case object UrlVerification extends EventType
  case object Message extends EventType

}
