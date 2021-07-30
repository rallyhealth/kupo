package com.rallyhealth.kupo.data.store

import com.rallyhealth.kupo.data.dao.{UserTokenEntry, UserTokenEntryKey}
import com.redis.RedisClientPool

class UserTokenStore(redis: RedisClientPool) {

  def get(key: UserTokenEntryKey): Option[String] = redis.withClient(client => client.get(key.asString))

  def set(entry: UserTokenEntry): Boolean = redis
    .withClient(client => client.set(entry.key.asString, entry.token))

}
