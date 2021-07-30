package com.rallyhealth.kupo.data.store

import com.rallyhealth.kupo.data.dao.WordEntryKey

import java.time.{Instant, Period}
import com.redis.RedisClientPool
import com.redis.serialization.Format._

class WordCountStore(redis: RedisClientPool) {

  /**
   * Fetch all instances word has been triggered in a channel.
   */
  def get(
    key: WordEntryKey,
    since: Instant = Instant.now().minus(Period.ofMonths(1))
  ): List[Instant] = redis
    .withClient(
      _.lrange[String](key.asString, 0, -1)
        .map(list => list.flatten.map(str => Instant.parse(str))
          .filter(_.isAfter(since)))
    )
    .getOrElse(List.empty)

  /**
   * Increment count of word instance with timestamp. If successful, return new word instance count. Else, return None.
   */
  def increment(key: WordEntryKey): Option[Long] = redis
    .withClient(client => client.lpush(key.asString, Instant.now()))
}
