package com.rallyhealth.kupo.controller

import com.rallyhealth.kupo.service.SlackAppHmacService
import play.api.Logging
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


/**
  * Authenticates incoming com.rallyhealth.kupo.slack app requests.
  * @see [[https://api.slack.com/docs/verifying-requests-from-slack]]
  */
abstract class SlackRequestAuthAction(
  slackRequestVerifier: SlackAppHmacService,
  controllerComponents: ControllerComponents
)(implicit override val executionContext: ExecutionContext) extends ActionBuilder[Request, AnyContent] with ActionRefiner[Request, Request] with Logging {

  override protected def refine[A](request: Request[A]): Future[Either[Result, Request[A]]] = Future.successful {
    for {
      timestamp <- request.headers.get(SlackHeaders.Timestamp).toRight(BadRequest(s"Header is missing: ${SlackHeaders.Timestamp}"))
      requestSignature <- request.headers.get(SlackHeaders.Signature).toRight(BadRequest(s"Header is missing: ${SlackHeaders.Signature}"))
      body <- request.body match {
        case AnyContentAsRaw(body) => body.asBytes().map(_.utf8String).toRight(BadRequest("Empty body"))
        case body: RawBuffer => body.asBytes().map(_.utf8String).toRight(BadRequest("Empty body"))
        case other => Left(BadRequest(s"Invalid body: $other"))
      }
      msg = s"v0:$timestamp:$body"
        computedSignature = "v0=" + slackRequestVerifier.getHmac(msg)
      result <- {
        logger.info("Request body: " + body)
        logger.info("RequestParams: " + request.rawQueryString)
        if (computedSignature == requestSignature) Right(request) else Left(Unauthorized)
      }
    } yield result
  }

  override def parser: BodyParser[AnyContent] = controllerComponents.actionBuilder.parser
}

/**
 * Verifies requests made by Kupo
 */
class KupoRequestAuthAction(
  slackRequestVerifier: SlackAppHmacService,
  controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext) extends SlackRequestAuthAction(slackRequestVerifier, controllerComponents)

object SlackHeaders {
  final val Timestamp = "X-Slack-Request-Timestamp"
  final val Signature = "X-Slack-Signature"
}
