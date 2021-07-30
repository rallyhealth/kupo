package com.rallyhealth.kupo.service

import akka.stream.Materializer
import com.rallyhealth.kupo.data.RedisModule
import com.rallyhealth.kupo.slack.SlackClient
import play.api.libs.ws.StandaloneWSClient
import com.softwaremill.macwire.{Module, wire}
import play.api.Configuration

import scala.concurrent.ExecutionContext

@Module
class ServiceModule(
  slackClient: SlackClient,
  redisModule: RedisModule,
  wsClient: StandaloneWSClient,
  configuration: Configuration
)(implicit ec: ExecutionContext, mat: Materializer) {

  lazy val serviceConfig: ServiceConfigImpl = wire[ServiceConfigImpl]

  lazy val oauthService: OauthService = wire[OauthService]
  lazy val slackRequestHmacService: SlackAppHmacService = wire[SlackAppHmacService]

  lazy val messageService: MessageService = wire[MessageService]
}
