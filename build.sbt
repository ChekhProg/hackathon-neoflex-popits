lazy val `domain-common` = ProjectBuilder.common("domain")
lazy val `kafka-common` = ProjectBuilder.common("kafka")

val AkkaVersion = "2.6.15"

lazy val `candles` =
  ProjectBuilder
    .service("candles")
    .settings(
      libraryDependencies ++= Dependencies.`akka-streams`,
      libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      libraryDependencies += "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
      libraryDependencies += "com.typesafe.akka" %% "akka-stream-typed" % AkkaVersion,
      libraryDependencies += "net.debasishg" %% "redisclient" % "3.30",
      libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.11",
      libraryDependencies ++= Seq("com.typesafe.play" % "play-json_2.11" % "2.4.2")
    )
    .dependsOn(
      `domain-common`,
      `kafka-common`,
    )
