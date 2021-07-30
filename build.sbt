import Dependencies.{Ext, TestOnly}

val macwireVersion = "2.3.3"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
  .settings(
    name := """kupo""",
    version := "1.0-SNAPSHOT",
    organization := "com.rallyhealth",
    scalaVersion := "2.13.3",
    libraryDependencies ++= Seq(
      guice, ws,
      Ext.enumeratumPlay,
      Ext.macwire,
      Ext.macwireMacros,
      Ext.bcprov,
      Ext.redisClient,
      TestOnly.scalaTestPlusPlay,
      TestOnly.mockito
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.rallyhealth.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.rallyhealth.binders._"
