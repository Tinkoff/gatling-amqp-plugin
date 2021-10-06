import Dependencies._

enablePlugins(GatlingPlugin)
enablePlugins(GitVersioning)

lazy val root = (project in file("."))
  .settings(
    name := "gatling-amqp-plugin",
    libraryDependencies ++= gatling ++ gatlingCore,
    libraryDependencies ++= Seq(rabbitmq, commonsPool, alpakkaAmqp, akkaStream),
    scalacOptions ++= Seq(
      "-encoding", "utf8", // Option and arguments on same line
      "-deprecation",
      "-unchecked",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps"
    )
  )
