package com.rallyhealth.kupo.service

import com.rallyhealth.kupo.data.dao.UserTokenEntryKey
import com.rallyhealth.kupo.data.store.ExclusionaryWordStore.{wordStore => database}
import com.rallyhealth.kupo.data.store.{SuggestionInfo, UserTokenStore}
import com.rallyhealth.kupo.slack.SlackClient
import com.rallyhealth.kupo.slack.eventcallback.{ChannelCreatedBody, ChannelEvent, MessageEvent}
import com.rallyhealth.kupo.slack.interactionpayload.{BlockAction, PayloadAction}
import play.api.Logging
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

/**
 * If a channel message includes a problematic phrase, return a private ephemeral response to gently prod
 * messenger to use a different phrase.
 */
class MessageService(
  serviceConfig: ServiceConfigImpl,
  slackClient: SlackClient,
  userTokenStore: UserTokenStore
)(implicit ec: ExecutionContext) extends Logging with DelayedSlackInterjector {

  /**
   * Automatically join new channels.
   * Currently OFF. TODO: This should be configurable.
   */
  def channelEventCallback(event: ChannelEvent, teamId: String): Future[Unit] = {
    if (MessageMuncher.isEventChannelCreated(event))
      event.channel match {
        // Join new public channels automatically
        case channelCreated: ChannelCreatedBody =>
          //slackClient.joinChannel(channelCreated.id, oauthTokenFor(teamId))
          //  .map(_ => ())
          Future.successful(())
      } else Future.successful(())
  }

  def interactionCallback(action: BlockAction): Future[Unit] = {
    action.team.foreach { team =>
      import MessageBuilder.ActionIds._
      action.actions.headOption match {
        case Some(PayloadAction(Dismiss, _, _, _)) | Some(PayloadAction(Close, _, _, _)) =>
          slackClient.deleteSourceMessage(action.response_url, oauthTokenFor(team.id))
        case Some(payloadAction) if WasThisHelpful.asSet.contains(payloadAction.action_id) =>
          // Add Redis
          slackClient.deleteSourceMessage(action.response_url, oauthTokenFor(team.id))
        case Some(PayloadAction(LearnMore, _, action_ts, Some(key))) =>
          val message = findLearnMore(key).getOrElse {
            logger.warn(s"Missing 'Learn More' message for key=$key.")
            "Something went wrong; couldn't find 'Learn More' message. Sorry..."
          }
          slackClient.sendBlockEphemeral(
            blocks = MessageBuilder.buildLearnMore(message).asJsArray,
            channelId = action.channel.id,
            token = oauthTokenFor(team.id),
            user = action.user.id,
            threadId = action_ts,
            fallbackText = message
          )
        case Some(PayloadAction(Edit, _, action_ts, Some(value))) =>
          val userTokenEntryKey = UserTokenEntryKey(action.user.id, team.id, serviceConfig.kupoAuth.clientId)
          userTokenStore.get(userTokenEntryKey)
            .map { token =>
              logger.warn(s"Yay token: $token")
              // Correct the original message
              Json.parse(value).validate[EditInfo] match {
                case JsSuccess(info, _) =>
                  slackClient.updateMessage(
                    text = info.message,
                    ts = info.ts,
                    token = token,
                    channelId = action.channel.id
                  )
                  slackClient.deleteSourceMessage(
                    responseUrl = action.response_url,
                    oauthTokenFor(team.id)
                  )
                case JsError(errors) =>
                  logger.error(s"Could not parse CorrectInfo=$errors")
              }
            }.getOrElse {
            slackClient
              .sendBlockEphemeral(
                blocks = MessageBuilder.buildAuthorize(oauthSignInUrlFor(team.id)).asJsArray,
                channelId = action.channel.id,
                token = oauthTokenFor(team.id),
                user = action.user.id,
                threadId = action_ts,
                fallbackText = "I need your permission!"
              )
          }

        case _ => ()
      }
    }
    Future.successful(())
  }

  def someEventCallback(event: MessageEvent, teamId: String): Future[Unit] = {
    event match {
      // Exclude messages from other bots
      case botMessage if MessageMuncher.isBotMessage(botMessage) =>
        logger.trace("eventMatch=bot")
        Future.successful(())
      // Check if direct message to bot is exclusionary
      case dm if MessageMuncher.isDirectMessage(dm) =>
        dm.user.foreach { user =>
          findSuggestion(dm.text, user) match {
            case Some(suggestion) =>
              slackClient.sendBlockMessage(
                blocks = MessageBuilder.buildSuggestion(suggestion, dm.ts, oauthTokenFor(teamId)).asJsArray,
                channelId = dm.channel,
                threadId = dm.thread_ts,
                token = oauthTokenFor(teamId),
                fallbackText = suggestion.suggestion
              )
            case None =>
              slackClient.sendTextMessage(
                message = MessageBuilder.MainPlainText.directMessagePasses,
                channelId = dm.channel,
                threadId = dm.thread_ts,
                token = oauthTokenFor(teamId),
              )
          }
        }
        Future.successful(())
      // Check if channel message is exclusive
      case channelMessage =>
        channelMessage.user.map { userId =>
          findSuggestion(channelMessage.text, userId)
            .map { suggestion =>
              slackClient.sendBlockEphemeral(
                blocks = MessageBuilder
                  .buildSuggestion(suggestion, channelMessage.ts, oauthTokenFor(teamId)).asJsArray,
                channelId = channelMessage.channel,
                token = oauthTokenFor(teamId),
                user = userId,
                threadId = channelMessage.ts,
                fallbackText = suggestion.suggestion
              )
            }
          Future.successful(())
        }.getOrElse(Future.successful(()))
    }
  }

  private def findLearnMore(key: String): Option[String] = database.get(key).map(_.learnMore.asString)

  /**
   * Return response of first match we find
   */
  private def findSuggestion(phrase: String, user: String): Option[SuggestionInfo] = {
    val caseInsensitivePhrase = stripLeadingAtMentions(phrase).toLowerCase()
    if (caseInsensitivePhrase.nonEmpty) {
      database
        .find { case (key, _) => caseInsensitivePhrase.contains(key) }
        .map { case (key, suggestionBody) =>
          import suggestionBody._
          val suggestion = {
            if (suggestions.length == 1) suggestions.head
            else suggestions(new Random().nextInt(suggestions.length))
          }
          val newMessage = {
            val lowerCasedMessage = caseInsensitivePhrase.replace(key, suggestion)
            val originalSplitPhrase = phrase.split(" ")
            lowerCasedMessage.split(" ")
              .map { word =>
                originalSplitPhrase
                  .find(original => original.equalsIgnoreCase(word))
                  .getOrElse(word)
              }
          }.mkString(" ")
          SuggestionInfo(key, suggestion, user, newMessage)
        }
    } else None
  }

  /**
   * Remove leading @messaging from a string. These look like `<@4LPH4NUM3R1C>`
   *
   * @param input String possibly starting with a @mention
   * @return Input string with it removed if found
   */
  private def stripLeadingAtMentions(input: String): String = {
    val regex = "^\\<@\\w+\\>\\s*"
    input.replaceAll(regex, "")
  }

  private def oauthTokenFor(teamId: String): String = {
    serviceConfig.kupoAuth.oauthTokens.get(teamId) match {
      case Some(token) => token
      case None => throw new RuntimeException(s"Unknown workspace. No token found for teamId=$teamId")
    }
  }

  private def oauthSignInUrlFor(teamId: String): String = {
    serviceConfig.kupoAuth.signInUrls.get(teamId) match {
      case Some(signInUrl) => signInUrl.toString
      case None => throw new RuntimeException(s"Unknown workspace. No token found for teamId=$teamId")
    }
  }
}
