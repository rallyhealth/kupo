package com.rallyhealth.kupo.slack

import akka.stream.Materializer
import com.softwaremill.macwire._
import play.api.libs.ws.StandaloneWSClient

import scala.concurrent.ExecutionContext

// $COVERAGE-OFF$

@Module
class SlackClientModule(wsClient: StandaloneWSClient)(implicit val ec: ExecutionContext, mat: Materializer) {

  lazy val slackClient: SlackClient = wire[SlackClient]

}

// $COVERAGE-ON$
