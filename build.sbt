import Dependencies._

lazy val root = (project in file("."))
  .enablePlugins(GitVersioning)
  .settings(
    name         := "gatling-amqp-plugin",
    scalaVersion := "2.13.8",
    libraryDependencies ++= gatling ++ gatlingCore,
    libraryDependencies ++= Seq(rabbitmq, commonsPool, fastUUID),
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
