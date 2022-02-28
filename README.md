# Gatling AMQP Plugin 

![Build](https://github.com/TinkoffCreditSystems/gatling-amqp-plugin/workflows/Build/badge.svg) [![Maven Central](https://img.shields.io/maven-central/v/ru.tinkoff/gatling-amqp-plugin_2.13.svg?color=success)](https://search.maven.org/search?q=ru.tinkoff.gatling-amqp-plugin) [![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

Plugin for support performance testing with AMQP in Gatling(3.7.x)

# Usage

## Getting Started
Plugin is currently available for Scala 2.13.

You may add plugin as dependency in project with your tests. Write this to your build.sbt: 

``` scala
libraryDependencies += "ru.tinkoff" %% "gatling-amqp-plugin" % <version> % Test
``` 

## Example Scenarios

* Example scenario for [publishing](https://github.com/TinkoffCreditSystems/gatling-amqp-plugin/blob/master/src/test/scala/ru/tinkoff/gatling/amqp/examples/PublishExample.scala)
* Example scenario for [Publish And Reply](https://github.com/TinkoffCreditSystems/gatling-amqp-plugin/blob/master/src/test/scala/ru/tinkoff/gatling/amqp/examples/RequestReplyExample.scala)
* Example scenario for [Publish and Reply on different message-brokers](https://github.com/TinkoffCreditSystems/gatling-amqp-plugin/blob/master/src/test/scala/ru/tinkoff/gatling/amqp/examples/RequestReplyTwoBrokerExample.scala)
