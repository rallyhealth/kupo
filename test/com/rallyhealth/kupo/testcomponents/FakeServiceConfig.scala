package com.rallyhealth.kupo.testcomponents

import com.rallyhealth.kupo.service.{ServiceConfig, SlackAppHmacService}
import com.rallyhealth.kupo.service.ServiceConfigImpl.AuthBundle
import org.bouncycastle.crypto.params.KeyParameter

class FakeServiceConfig extends ServiceConfig {

  override val kupoAuth: AuthBundle = {
    val keyParameter = new KeyParameter("signingSecret".getBytes(SlackAppHmacService.defaultCharset))
    AuthBundle(
      clientId = "clientId", clientSecret = "secret", signingSecret = keyParameter,
      oauthTokens = Map.empty, scopes = Seq.empty, userScopes = Seq.empty
    )
  }
}
