package com.rallyhealth.kupo.controller

import com.rallyhealth.kupo.service.OauthService
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}

import scala.concurrent.ExecutionContext

trait SlackController extends Logging {
  def oauthCallback(code: String): EssentialAction
}

class SlackControllerImpl(
  cc: ControllerComponents,
  oauthService: OauthService
)(implicit ec: ExecutionContext) extends AbstractController(cc) with SlackController {

  override def oauthCallback(code: String): EssentialAction = Action.async { _ =>
    logger.info(s"Authorizing: code=$code")
    oauthService.exchangeCodeForToken(code).map { accessInfo =>
      val response = s"""
        |<html><body>
        |<p>Kupo was successfully installed!</p>
        |<p>Store this info somewhere safe. <strong>It will not be displayed again.</strong></p>
        |<pre>
        |${Json.prettyPrint(Json.toJson(accessInfo))}
        |</pre>
        |</body></html>
      """.stripMargin

      Ok(response).as(HTML)
    }
  }

}
