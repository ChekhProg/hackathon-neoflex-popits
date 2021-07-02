lazy val `domain-common` = ProjectBuilder.common("domain")
lazy val `kafka-common` = ProjectBuilder.common("kafka")

val AkkaVersion = "2.6.15"

lazy val `candles` =
  ProjectBuilder
    .service("candles")
    .settings(
      libraryDependencies ++= Dependencies.`akka-streams`,
      libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion
    )
    .dependsOn(
      `domain-common`,
      `kafka-common`,
    )
