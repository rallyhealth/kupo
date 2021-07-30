package com.rallyhealth.kupo.controller

import com.rallyhealth.kupo.service.SlackAppHmacService
import com.softwaremill.macwire.{Module, wire}
import play.api.mvc.ControllerComponents
import scala.concurrent.ExecutionContext

@Module
class ActionModule(
  slackRequestVerifier: SlackAppHmacService,
  controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext)  {

  lazy val eventVerificationAction: SlackEventVerificationAction = wire[SlackEventVerificationAction]
  lazy val requestAuthAction: KupoRequestAuthAction = wire[KupoRequestAuthAction]
}
