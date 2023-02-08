package ru.tinkoff.gatling.amqp.examples

import io.gatling.core.Predef._
import ru.tinkoff.gatling.amqp.Predef._

class AmqpGatlingRunnerTest extends Simulation {

  setUp(
    scenario("Test Scenario")
      .exec(
        amqp("Test request").publish
          .queueExchange("test_queue")
          .textMessage("test message")
          .priority(0)
          .messageId("1"),
      )
      .inject(atOnceUsers(1)),
  ).protocols(
    amqp
      .connectionFactory(
        rabbitmq
          .host("localhost")
          .port(5672)
          .username("rabbitmq")
          .password("rabbitmq")
          .vhost("/"),
      )
      .replyTimeout(60000)
      .consumerThreadsCount(8)
      .usePersistentDeliveryMode,
  ).maxDuration(10)
}
