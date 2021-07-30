package com.rallyhealth.kupo.controllers

import com.rallyhealth.kupo.testcomponents.FakeServiceConfig
import com.rallyhealth.kupo.controller.SlackControllerImpl
import com.rallyhealth.kupo.service.{OauthService, ServiceConfig}
import com.rallyhealth.kupo.slack.{OAuthV2AccessInfo, OAuthV2Team, SlackClient}
import com.softwaremill.macwire.wire
import org.bouncycastle.crypto.params.KeyParameter
import org.mockito.Mockito.{verify, when}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.ControllerComponents
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class SlackControllerTest extends FunSpec
  with Matchers
  with MockitoSugar
  with OneMaterializerPerSuite
  with BeforeAndAfter {

  private implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  private var oauthService: OauthService = _
  private var slackClient: SlackClient = _
  private var serviceConfig: ServiceConfig = _
  private var controller: SlackControllerImpl = _

  before {
    oauthService = mock[OauthService]
    slackClient = mock[SlackClient]
    serviceConfig = new FakeServiceConfig
    val cc: ControllerComponents = Helpers.stubControllerComponents()
    controller = wire[SlackControllerImpl]
  }

  describe("oauthCallback") {
    it("should return 200 after successfully authorizing an app") {
      val code = "code"
      val accessInfo = OAuthV2AccessInfo(
        access_token = "token",
        scope = "scope",
        team = OAuthV2Team("name", "id"),
        authed_user = None,
        bot_user_id = "botid",
        token_type = "token_type"
      )
      when(oauthService.exchangeCodeForToken(code)).thenReturn(Future.successful(accessInfo))
      when(
        slackClient.exchangeCodeForTokenV2(
          code = code,
          clientId = serviceConfig.kupoAuth.clientId,
          clientSecret = serviceConfig.kupoAuth.clientSecret
        )
      ).thenReturn(Future.successful(accessInfo))
      val result = call(controller.oauthCallback(code), FakeRequest())
      status(result) shouldBe OK
      verify(oauthService).exchangeCodeForToken(code)
    }
  }
}
