package ru.tinkoff.gatling.amqp.examples

import io.gatling.core.Predef._
import ru.tinkoff.gatling.amqp.javaapi.AmqpDsl._

class JavaTest extends Simulation {

  setUp(
    scenario("Test Scenario")
      .exec(
        amqp("Test publish request").publish
          .queueExchange("test_queue")
          .textMessage("test message")
          .priority(0)
          .messageId("1")
          .asScala(),
      )
      .exec(
        amqp("Test queue exchange").requestReply
          .queueExchange("test_queue")
          .replyExchange("test_queue")
          .textMessage("test message")
          .priority(0)
          .messageId("1")
          .expiration("10")
          .asScala(),
      )
      .exec(
        amqp("Test topic exchange").requestReply
          .topicExchange("test_exchange", "routingKey")
          .replyExchange("test_queue")
          .noReplyTo()
          .textMessage("test message")
          .priority(0)
          .messageId("1")
          .asScala(),
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
          .vhost("/")
          .build(),
      )
      .replyTimeout(60000)
      .consumerThreadsCount(8)
      .usePersistentDeliveryMode()
      .protocol(),
  ).maxDuration(60)
}
