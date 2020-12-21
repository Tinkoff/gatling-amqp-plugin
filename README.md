# Gatling AMQP Plugin [![Build Status](https://travis-ci.com/TinkoffCreditSystems/gatling-amqp-plugin.svg?branch=master)](https://travis-ci.com/TinkoffCreditSystems/gatling-amqp-plugin) [![Maven Central](https://img.shields.io/maven-central/v/ru.tinkoff/gatling-amqp-plugin_2.12.svg?color=success)](https://search.maven.org/search?q=ru.tinkoff.gatling-amqp-plugin)

Plugin for support performance testing with AMQP in Gatling(3.4.x)

# Usage

## Getting Started
Plugin is currently available for Scala 2.12.

You may add plugin as dependency in project with your tests. Write this to your build.sbt: 
``` scala
libraryDependencies += "ru.tinkoff" %% "gatling-amqp-plugin" % <version> % Test
``` 

## Example Scenarios

* Example scenario for [publishing](https://github.com/TinkoffCreditSystems/gatling-amqp-plugin/blob/master/src/test/scala/ru/tinkoff/gatling/amqp/examples/PublishExample.scala)
* Example scenario for [Publish And Reply](https://github.com/TinkoffCreditSystems/gatling-amqp-plugin/blob/master/src/test/scala/ru/tinkoff/gatling/amqp/examples/RequestReplyExample.scala)
* Example scenario for [Publish and Reply on different message-brokers](https://github.com/TinkoffCreditSystems/gatling-amqp-plugin/blob/master/src/test/scala/ru/tinkoff/gatling/amqp/examples/RequestReplyTwoBrokerExample.scala)
