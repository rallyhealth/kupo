package com.rallyhealth.kupo.slack

import com.rallyhealth.kupo.slack.SlackClient._
import play.api.Logging
import play.api.http.ContentTypes.{FORM, JSON}
import play.api.http.HeaderNames.{AUTHORIZATION, CONTENT_TYPE}
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.libs.ws.{EmptyBody, StandaloneWSClient}

import java.util.Base64
import scala.concurrent.{ExecutionContext, Future}

class SlackClient(ws: StandaloneWSClient)(implicit ec: ExecutionContext) extends Logging {

  val baseUrl: String = "https://slack.com/api/"

  def exchangeCodeForTokenV2(code: String, clientId: String, clientSecret: String): Future[OAuthV2AccessInfo] = {
    val encodedClient = Base64.getEncoder.encodeToString(s"$clientId:$clientSecret".getBytes)
    ws.url(baseUrl + "oauth.v2.access")
      .addQueryStringParameters(("code", code))
      .addHttpHeaders(Headers.ContentTypeUrlEncoded, Headers.basicAuth(encodedClient))
      .get()
      .map{ response =>
          Json.parse(response.body.getBytes).asOpt[OAuthV2AccessInfo] match {
            case Some(token) => token
            case _ => throw new IllegalStateException(s"Unexpected response returned from Slack: ${response.body}")
          }
      }
  }

  /**
   * Ostensibly this method is for verifying auth, but it has a useful side effect
   * that you get your bot's user_id in the response. This is handy for filtering
   * channel messages that are @'ed to you.
   *
   * @return A Future containing the AuthTestResponse
   */
  def authTest(token: String): Future[AuthTestResponse] = {
    ws.url(baseUrl + "auth.test")
      .addHttpHeaders(Headers.ContentTypeJson, Headers.bearerAuth(token))
      .post(EmptyBody)
      .mapTo[AuthTestResponse]
  }

  /**
   * Send a simple message (with no fancy formatting or embeds) to a channel
   */
  def sendTextMessage(
    message: String,
    channelId: String,
    threadId: Option[String],
    token: String
  ): Future[GenericResponse] = {
    val requestBody: JsValue = Json.obj("channel" -> channelId, "thread_ts" -> threadId, "text" -> message)
    postJsonResponse(baseUrl + "chat.postMessage", requestBody, token)
  }

  /**
   * See https://api.slack.com/methods/chat.postEphemeral
   * Sends a complex channel message but private to user who triggered event.
   * "Ephemeral" in that it is not included in chat history (disappears with each Slack session)
   * and can be deleted by the user, even within Rally com.rallyhealth.kupo.slack.
   */
  def sendBlockEphemeral(
    blocks: JsArray,
    channelId: String,
    threadId: String,
    token: String,
    fallbackText: String,
    user: String
  ): Future[GenericResponse] = {
    val requestBody: JsValue = Json.obj(
      "channel" -> channelId,
      "ts" -> threadId,
      "blocks" -> blocks,
      "text" -> fallbackText,
      "user" -> user
    )
    logger.debug(s"EphemeralBlock=$requestBody")
    postJsonResponse(baseUrl + "chat.postEphemeral", requestBody, token)
  }

  /**
   * See https://api.slack.com/methods/chat.postMessage#arg_blocks
   * Sends a complex message, compared to [[sendTextMessage()]]].
   */
  def sendBlockMessage(
    blocks: JsArray,
    channelId: String,
    threadId: Option[String],
    token: String,
    fallbackText: String
  ): Future[GenericResponse] = {
    val requestBody: JsValue = Json.obj(
      "channel" -> channelId,
      "thread_ts" -> threadId,
      "blocks" -> blocks,
      "text" -> fallbackText
    )
    postJsonResponse(baseUrl + "chat.postMessage", requestBody, token)
  }

  def updateMessage(text: String, ts: String, channelId: String, token: String): Unit = {
    val requestBody: JsValue = Json.obj("text" -> text, "ts" -> ts, "channel" -> channelId, "as_user" -> "true")
    logger.debug(s"updateMessage=$requestBody")
    postJsonResponse(baseUrl + "chat.update", requestBody, token)
  }

  /**
   * See https://api.slack.com/interactivity/handling#deleting_message_response
   * In response to an interaction with a message, delete original message.
   */
  def deleteSourceMessage(responseUrl: String, token: String): Future[GenericResponse] = {
    val requestBody: JsValue = Json.obj("delete_original" -> "true")
    postJsonResponse(responseUrl, requestBody, token)
  }

  private def postJsonResponse(url: String, body: JsValue, token: String): Future[GenericResponse] = {
    ws.url(url)
      .addHttpHeaders(Headers.ContentTypeJsonUtf8, Headers.bearerAuth(token))
      .post(body)
      .mapTo[GenericResponse]
  }
}

object SlackClient {
  object Urls {
    val base: String = "https://slack.com/api/"
    val oauth: String = base + "oauth.access"

  }
  object Headers {
    val ContentTypeJson: (String, String) = (CONTENT_TYPE, JSON)
    val ContentTypeJsonUtf8: (String, String) = (ContentTypeJson._1, ContentTypeJson._2 + ";charset=utf8")
    val ContentTypeUrlEncoded: (String, String) = (CONTENT_TYPE, FORM)
    def basicAuth(code: String): (String, String) = (AUTHORIZATION, s"Basic $code")
    def bearerAuth(token: String): (String, String) = (AUTHORIZATION, s"Bearer $token")
  }

}
