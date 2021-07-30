package com.rallyhealth.kupo.service

import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.util.encoders.Hex

import java.nio.charset.StandardCharsets

class SlackAppHmacService(serviceConfig: ServiceConfigImpl) {

  import SlackAppHmacService._

  /**
   * Creates a HMAC SHA256 hash from the message:
   * - Uses secret based on the app configuration.
   * - Takes the hex digest of the hash and converts to string.
   */
  def getHmac(message: String): String = {
    val hmacSecret = serviceConfig.kupoAuth.signingSecret
    val in = message.getBytes(defaultCharset)
    val hmac = new HMac(new SHA256Digest)
    hmac.init(hmacSecret)
    hmac.update(in, 0, in.length)

    val out = new Array[Byte](hmac.getMacSize)
    hmac.doFinal(out, 0)
    Hex.encode(out).map(_.toChar).mkString
  }

}

object SlackAppHmacService {
  final val defaultCharset = StandardCharsets.UTF_8
}
