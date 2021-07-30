package com.rallyhealth.kupo.controllers

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer, SystemMaterializer}
import org.scalatest.{BeforeAndAfterAll, Suite}
import akka.stream.testkit.NoMaterializer

trait OneMaterializerPerSuite extends BeforeAndAfterAll { this: Suite =>

  protected implicit val actorSystem: ActorSystem = ActorSystem.apply()

  override protected def afterAll(): Unit = {
    actorSystem.terminate()
    super.afterAll()
  }
}
