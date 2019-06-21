package ru.tinkoff.gatling.amqp.examples

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import ru.tinkoff.gatling.amqp.Predef._
import ru.tinkoff.gatling.amqp.protocol.AmqpProtocolBuilder

import Utils._
import scala.concurrent.duration._

class PublishExample extends Simulation {

  val amqpConf: AmqpProtocolBuilder = amqp
    .connectionFactory(
      rabbitmq
        .host("test_mq")
        .port(5672)
        .username("testUser")
        .password("testPass")
        .vhost("/test")
    )
    .usePersistentDeliveryMode

  val scn: ScenarioBuilder = scenario("AMQP test")
    .feed(idFeeder)
    .exec(
      amqp("publish to exchange").publish
        .directExchange("test_queue", "test_key")
        .textMessage("Hello message - ${id}")
        .messageId("${id}")
        .priority(0)
        .headers(Map("testheader" -> "testvalue"))
    )

  setUp(
    scn.inject(
      rampUsersPerSec(1) to 5 during (60 seconds),
      constantUsersPerSec(5) during (5 minutes))
  ).protocols(amqpConf)
    .maxDuration(10 minutes)

}
