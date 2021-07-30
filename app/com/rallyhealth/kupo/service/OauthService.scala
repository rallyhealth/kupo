package com.rallyhealth.kupo.service

import com.rallyhealth.kupo.data.dao.{UserTokenEntry, UserTokenEntryKey}
import com.rallyhealth.kupo.data.store.UserTokenStore
import com.rallyhealth.kupo.slack.{OAuthAccessInfo, OAuthV2AccessInfo, SlackClient}
import play.api.Logging

import scala.concurrent.{ExecutionContext, Future}

class OauthService(
  slackClient: SlackClient,
  serviceConfig: ServiceConfigImpl,
  userTokenStore: UserTokenStore
)(implicit ec: ExecutionContext) extends Logging {

  def exchangeCodeForToken(code: String): Future[OAuthAccessInfo] = {
    val (clientId, secret) = (serviceConfig.kupoAuth.clientId, serviceConfig.kupoAuth.clientSecret)

    val futureToken = slackClient.exchangeCodeForTokenV2(
      code = code,
      clientId = clientId,
      clientSecret = secret
    )
    futureToken.foreach(info => storeToken(info, clientId))
    futureToken
  }

  private def storeToken(
    authAccessInfo: OAuthV2AccessInfo,
    clientId: String
  ): Boolean = {
    authAccessInfo.authed_user match {
      case Some(authedUser) =>
        val isSuccess = userTokenStore.set(
          UserTokenEntry(
            UserTokenEntryKey(username = authedUser.id, teamId = authAccessInfo.team.id, clientId = clientId),
            token = authedUser.access_token
          )
        )
        val identifier = s"User=${authedUser.id}, TeamId=${authAccessInfo.team.id}, ClientId=$clientId"
        if (isSuccess) logger.debug(s"$identifier token successfully stored in Redis.")
        else logger.warn(s"$identifier token failed storage in Redis.")
        isSuccess
      case None =>
        logger.warn(s"No authed_user returned for botUserId=${authAccessInfo.bot_user_id}")
        false
    }
  }
}
