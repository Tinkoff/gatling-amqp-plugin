package ru.tinkoff.gatling.amqp.examples

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import ru.tinkoff.gatling.amqp.Predef._
import ru.tinkoff.gatling.amqp.examples.Utils.idFeeder
import ru.tinkoff.gatling.amqp.protocol.AmqpProtocolBuilder

import scala.concurrent.duration._

class RequestReplyExample extends Simulation{
  val amqpConf: AmqpProtocolBuilder = amqp
    .connectionFactory(
      rabbitmq
        .host("test_mq")
        .port(5672)
        .username("testUser")
        .password("testPass")
        .vhost("/test")
    )
    .replyTimeout(60000)
    .consumerThreadsCount(8)
    .matchByMessageId
    .usePersistentDeliveryMode

  val scn: ScenarioBuilder = scenario("AMQP test")
    .feed(idFeeder)
    .exec(
      amqp("Request Reply exchange test").requestReply
        .directExchange("test_queue_in", "test_key")
        .replyExchange("test_queue_out")
        .textMessage("Hello message - ${id}")
        .messageId("${id}")
        .priority(0)
        .property("testheader", "testvalue")
        .check(
          bodyString.exists,
          bodyString.is("Message processed")
        )
    )

  setUp(
    scn.inject(
      rampUsersPerSec(1) to 5 during (60 seconds),
      constantUsersPerSec(5) during (5 minutes))
  ).protocols(amqpConf)
    .maxDuration(10 minutes)
}
