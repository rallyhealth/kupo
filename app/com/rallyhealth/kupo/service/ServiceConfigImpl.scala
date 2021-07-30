package com.rallyhealth.kupo.service

import com.rallyhealth.kupo.service.ServiceConfigImpl.AuthBundle
import org.bouncycastle.crypto.params.KeyParameter
import play.api.Configuration

trait ServiceConfig {
   val kupoAuth: AuthBundle
 }

class ServiceConfigImpl(
  config: Configuration
) extends ServiceConfig {

  override val kupoAuth: AuthBundle = AuthBundle(
    clientId = config.get[String]("slack.client.id"),
    clientSecret = config.get[String]("slack.client.secret"),
    signingSecret = new KeyParameter(config.get[String]("slack.signing.secret").getBytes(SlackAppHmacService.defaultCharset)),
    oauthTokens = {
      val token = config.get[String]("slack.oauth.token")
      val workspace = config.get[String]("slack.workspace")
      Map(workspace -> token)
    },
    scopes = Seq("app_mentions:read", "channels:history", "channels:read", "chat:write", "commands", "groups:history", "groups:read"),
    userScopes = Seq("chat:write")
  )

}

object ServiceConfigImpl {

  case class SignInUrl(
    workspace: String,
    clientId: String,
    scopes: Seq[String],
    userScopes: Seq[String]
  ) {
    private def buildScpeStr(paramName: String, scpes: Seq[String]) = {
      if (scpes.nonEmpty) {
        s"&$paramName=${scpes.mkString(",")}"
      } else ""
    }
    private val scopeStr = buildScpeStr("scope", scopes)
    private val userScopeStr = buildScpeStr("user_scope", userScopes)
    override val toString: String = s"https://slack.com/oauth/v2/authorize?client_id=$clientId$scopeStr$userScopeStr"
  }

  /**
    * Standard Slack app credentials.
    *
    * @param signingSecret also called the HMAC secret
    * @param oauthTokens   client ids and client secrets are per-app, but oauth tokens are per-workspace
    */
  case class AuthBundle(
    clientId: String,
    clientSecret: String,
    signingSecret: KeyParameter,
    oauthTokens: Map[String, String] = Map.empty,
    scopes: Seq[String] = Seq.empty,
    userScopes: Seq[String] = Seq.empty
  ) {
    val signInUrls: Map[String, SignInUrl] = oauthTokens.keys.map { workspace =>
      (workspace, SignInUrl(
        workspace = workspace,
        clientId = clientId,
        scopes = scopes,
        userScopes = userScopes
      ))
    }.toMap
  }

}
