package ru.tinkoff.gatling.amqp.examples

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import ru.tinkoff.gatling.amqp.Predef._
import ru.tinkoff.gatling.amqp.examples.Utils.idFeeder
import ru.tinkoff.gatling.amqp.protocol.AmqpProtocolBuilder

import scala.concurrent.duration._

class RequestReplyTwoBrokerExample extends Simulation{
  val amqpConf: AmqpProtocolBuilder = amqp
    .connectionFactory(
      rabbitmq
        .host("localhost")
        .port(5672)
        .username("guest")
        .password("guest")
        .vhost("/")
    )
    .replyTimeout(60000)
    .consumerThreadsCount(8)
    .matchByMessageId
    .usePersistentDeliveryMode

  val scn: ScenarioBuilder = scenario("AMQP test")
    .feed(idFeeder)
    .exec(
      amqp("Request Reply exchange test").requestReply
        .queueExchange("readQueue")
        .replyExchange("readQueue")
        .textMessage("""{"msg": "Hello message - ${id}"}""")
        .messageId("${id}")
        .priority(0)
        .contentType("application/json")
        .headers("test"-> "performance", "extra-test" -> "34-${id}")
        .check(
          bodyString.exists,
          bodyString.is("Message processed")
        )
    )

  setUp(
    scn.inject(
      rampUsersPerSec(1) to 5 during (60 seconds),
      constantUsersPerSec(5) during (2 minutes))
  ).protocols(amqpConf)
    .maxDuration(10 minutes)
}
