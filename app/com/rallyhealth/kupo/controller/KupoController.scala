package com.rallyhealth.kupo.controller

import com.rallyhealth.kupo.service.MessageService
import com.rallyhealth.kupo.slack.eventcallback.{ChannelEvent, MessageDeletedEvent, MessageEvent, SlackEvent}
import com.rallyhealth.kupo.slack.interactionpayload.BlockAction
import play.api.Logging
import play.api.http.ContentTypes
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction, Result}
import play.core.parsers.FormUrlEncodedParser

import scala.concurrent.{ExecutionContext, Future}

trait KupoController {
  def eventOrVerification(): EssentialAction
  def interaction(): EssentialAction
}

class KupoControllerImpl(
  cc: ControllerComponents,
  authAction: KupoRequestAuthAction,
  eventAction: SlackEventVerificationAction,
  service: MessageService
)(implicit ec: ExecutionContext) extends AbstractController(cc) with KupoController with Logging {

  def eventOrVerification(): EssentialAction = (authAction andThen eventAction).async(parse.raw) { request =>
    val json: JsValue = Json.parse(request.body.asBytes().map(_.utf8String).get) // @TODO clean this up unecessary re-stringifying
    logger.info("eventOrVerification")
    logger.info(s"botHook Response=$json")
    val typ: Option[String] = (json \ "type").asOpt[String]
    val response = typ match {
      case None => BadRequest
      case Some("event_callback") => responseCallback(json)
      // SlackEventVerificationAction doesn't seem to work (?)
      case Some("url_verification") =>
        (json \ "challenge").asOpt[String]
          .map(challenge => Ok(Json.obj("challenge" -> challenge)).as(ContentTypes.JSON))
          .getOrElse(Ok)
      case Some(_) => Ok
    }
    Future.successful(response)
  }

  def userOauthVerification(): EssentialAction = (authAction andThen eventAction).async(parse.raw) { request =>
    logger.info("userOauthVerification")
    val json: JsValue = Json.parse(request.body.asBytes().map(_.utf8String).get) // @TODO clean this up unecessary re-stringifying
    logger.info(s"botHook Response=$json")
    ???
  }

  /**
    * For handling interactions. See https://api.slack.com/interactivity
    */
  def interaction(): EssentialAction = (authAction andThen eventAction).async(parse.raw) { request =>
    logger.info("interaction")
    FormUrlEncodedParser.parse(request.body.asBytes().get.decodeString("UTF-8")).get("payload")
      .map { payload =>
        val arr = payload.flatMap(_.getBytes()).toArray
        val json = Json.parse(arr)
        logger.info(s"botHook Response=$json")
        json.validate[BlockAction] match {
          case JsSuccess(action, _) => service.interactionCallback(action)
          case e: JsError =>
            logger.error(s"Couldn't parse interaction into BlockAction, got error=$e")
            ()
        }
      }
    Future.successful(Ok)
  }

  /**
    * This is our main response handler. Channel messages and DMs to Kupo will all get routed here.
    */
  private def responseCallback(response: JsValue): Result = {
    val event = (response \ "event").get
    logger.info(s"SlackEvent=$event")
    event.validate[SlackEvent] match {
      case JsSuccess(slackEvent, _) =>
        slackEvent match {
          case mde: MessageDeletedEvent => () // No need to do anything
          case me: MessageEvent =>
            me.team.map(teamId => service.someEventCallback(me, teamId))
          case ce: ChannelEvent =>
            val teamId = (response \ "team_id").as[String]
            service.channelEventCallback(ce, teamId)
        }
      case e: JsError => logger.error(s"Couldn't parse event into SlackEvent, got error=$e")
    }
    // Respond immediately
    Ok
  }
}
