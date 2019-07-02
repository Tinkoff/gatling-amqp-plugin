ThisBuild / organization := "ru.tinkoff"
ThisBuild / scalaVersion := "2.12.8"

ThisBuild / publishMavenStyle := true

ThisBuild / publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/TinkoffCreditSystems/gatling-amqp-plugin"),
    "git@github.com:TinkoffCreditSystems/gatling-amqp-plugin.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "red-bashmak",
    name  = "Vyacheslav Kalyokin",
    email = "v.kalyokin@tinkoff.ru",
    url   = url("https://github.com/red-bashmak")
  )
)

ThisBuild / description := "Plugin for support performance testing with AMQP in Gatling(3.x.x)."
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/TinkoffCreditSystems/gatling-amqp-plugin"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
