package com.rallyhealth.kupo.controller

import com.rallyhealth.kupo.controller.SlackEventVerificationAction.UrlVerification
import play.api.http.ContentTypes
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Results.{BadRequest, Ok}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Will immediately respond to the event verification challenge if appropriate.
  * Otherwise, will let the request through.
  *
  * See https://api.slack.com/events/url_verification.
  */
class SlackEventVerificationAction(
  controllerComponents: ControllerComponents
)(
  implicit
  override val executionContext: ExecutionContext
) extends ActionBuilder[Request, AnyContent] with ActionFilter[Request] {

  override protected def filter[A](request: Request[A]): Future[Option[Result]] = {
    Future.successful {
      request.body match {
        case UrlVerification(result) => Some(result)
        case AnyContentAsJson(UrlVerification(result)) => Some(result)
        case _ => None
      }
    }
  }

  override def parser: BodyParser[AnyContent] = controllerComponents.actionBuilder.parser

}

object SlackEventVerificationAction {

  /**
    * Case destructor object: if the input is a json object with {"type":"url_verification"}, will apply
    * [[urlVerification()]] to it and return the [[Result]].
    */
  object UrlVerification {
    def unapply(json: JsValue): Option[Result] = {
      (json \ "type").asOpt[String] match {
        case Some("url_verification") => Some(urlVerification(json))
        case _ => None
      }
    }
  }

  /**
    * If the object contains {"challenge": "..."}, respond with that same body.
    * Otherwise respond with 400 Bad Request.
    */
  private def urlVerification(json: JsValue): Result = {
    (json \ "challenge").asOpt[String] match {
      case Some(challenge) => Ok(Json.obj("challenge" -> challenge)).as(ContentTypes.JSON)
      case None => BadRequest("Unable to verify challenge in request body.")
    }
  }

}
