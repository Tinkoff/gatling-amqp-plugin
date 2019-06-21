import Dependencies._

enablePlugins(GatlingPlugin)

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "ru.tinkoff",
      scalaVersion := "2.12.8",
      version := "1.0.1"
    )),
    name := "gatling-amqp-plugin",
    libraryDependencies ++= gatling,
    libraryDependencies ++= Seq( rabbitmq, commonsPool),
    scalacOptions ++= Seq(
      "-encoding", "utf8", // Option and arguments on same line
      "-deprecation",
      "-unchecked",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps"
    ),
    homepage := Some(url("https://github.com/TinkoffCreditSystems/gatling-amqp-plugin")),
    scmInfo := Some(ScmInfo(url("https://github.com/TinkoffCreditSystems/gatling-amqp-plugin"), "git@github.com:TinkoffCreditSystems/gatling-amqp-plugin.git")),
    developers := List(Developer("red-bashmak", "Vyacheslav Kalyokin", "v.kalyokin@tinkoff.ru", url("https://github.com/red-bashmak"))),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    publishMavenStyle := true,
    // Add sonatype repository settings
    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    )
  )
