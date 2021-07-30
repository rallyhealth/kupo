import sbt._

object Dependencies {

  private val macwireVersion = "2.3.3"

  object Ext {
    val bcprov = "org.bouncycastle" % "bcprov-jdk15on" % "1.68"
    val enumeratumPlay = "com.beachape" %% "enumeratum-play" % "1.6.1"
    val macwire = "com.softwaremill.macwire" %% "util" % macwireVersion
    val macwireMacros =  "com.softwaremill.macwire" %% "macros" % macwireVersion % "provided"
    val redisClient = "net.debasishg" %% "redisclient" % "3.30"
  }

  object TestOnly {
    val mockito = "org.mockito" % "mockito-all" % "1.10.19" % "test"
    val scalaTestPlusPlay = "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % "test"
  }

}
