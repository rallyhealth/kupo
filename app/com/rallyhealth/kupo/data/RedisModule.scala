package com.rallyhealth.kupo.data

import com.rallyhealth.kupo.data.store.{UserTokenStore, WordCountStore}
import com.redis.RedisClientPool
import com.softwaremill.macwire.{Module, wire}
import play.api.Configuration

@Module
class RedisModule(
  configuration: Configuration
) {

  // todo: configure this
  lazy val redisHost: String = configuration.get[String]("redis.host")
  lazy val redisPort: Int = configuration.get[Int]("redis.port")
  lazy val clientPool: RedisClientPool = new RedisClientPool(redisHost, redisPort)
  lazy val wordCountStore: WordCountStore = wire[WordCountStore]
  lazy val userTokenStore: UserTokenStore = wire[UserTokenStore]

}
