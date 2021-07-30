package com.rallyhealth.kupo.service

import java.util.concurrent.atomic.AtomicLong

import com.rallyhealth.kupo.slack.{GenericResponse, SlackClient}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

trait DelayedSlackInterjector {

  /**
    * How long to wait between interjections.
    */
  def restPeriod: FiniteDuration = 24.hours

  /**
    * How likely the interjection is
    * @return
    */
  def probability: Double = 0.3

  private lazy val lastInterjection = new AtomicLong(System.currentTimeMillis() - restPeriod.toMillis)

  def interject(slackClient: SlackClient, message: String, channelId: String, threadId: Option[String], token: String)(implicit ec: ExecutionContext): Future[Option[GenericResponse]] = {
    val nowMs = System.currentTimeMillis()
    val itsTime = (nowMs - lastInterjection.get()).millis > restPeriod

    if (itsTime) {
      lastInterjection.set(nowMs)
      if (scala.util.Random.nextDouble() <= probability) {
        slackClient.sendTextMessage(message, channelId, threadId, token).map(response => Some(response))
      } else {
        Future.successful(None)
      }
    } else {
      Future.successful(None)
    }
  }
}
