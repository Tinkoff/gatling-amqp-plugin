import Dependencies._

coverageEnabled := true

lazy val root = (project in file("."))
  .enablePlugins(GitVersioning, GatlingPlugin)
  .settings(
    name         := "gatling-amqp-plugin",
    scalaVersion := "2.13.10",
    libraryDependencies ++= gatling ++ gatlingCore,
    libraryDependencies ++= Seq(rabbitmq, commonsPool, fastUUID),
    libraryDependencies ++= scalaTest,
    scalacOptions ++= Seq(
      "-encoding",
      "utf8", // Option and arguments on same line
      "-deprecation",
      "-unchecked",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps",
    ),
  )
