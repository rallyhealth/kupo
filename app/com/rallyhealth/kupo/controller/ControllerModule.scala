package com.rallyhealth.kupo.controller

import com.rallyhealth.kupo.service.ServiceModule
import com.softwaremill.macwire.{Module, wire}
import play.api.mvc.ControllerComponents

import scala.concurrent.ExecutionContext

@Module
class ControllerModule(
  controllerComponents: ControllerComponents,
  actionModule: ActionModule,
  serviceModule: ServiceModule
)(implicit ec: ExecutionContext) {

  lazy val slackController: SlackController = wire[SlackControllerImpl]
  lazy val kupoController: KupoController = wire[KupoControllerImpl]
}
