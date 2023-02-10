package ru.tinkoff.gatling.amqp.examples

import io.gatling.core.Predef._
import ru.tinkoff.gatling.amqp.javaapi.AmqpDsl._

import java.util.Date

class JavaTest extends Simulation {
  val date = new Date()

  setUp(
    scenario("Test Scenario")
      .exec(
        amqp("Test publish request").publish
          .queueExchange("test_queue")
          .textMessage("Publish test message")
          .priority(0)
          .messageId("1")
          .asScala(),
      )
      .exec(
        amqp("Test queue exchange").requestReply
          .queueExchange("test_queue")
          .replyExchange("test_queue")
          .textMessage("Queue test message")
          .priority(0)
          .messageId("1")
          .expiration("60000")
          .contentEncoding("text/plain")
          .check(
          )
          .asScala(),
      )
      .exec(
        amqp("Test topic exchange").requestReply
          .topicExchange("test_exchange", "routingKey")
          .replyExchange("test_queue")
          .textMessage("Topic test message")
          .priority(0)
          .messageId("1")
          .replyTo("test_queue")
          .appId("appId")
          .asScala(),
      )
      .exec(
        amqp("Test direct exchange").requestReply
          .directExchange("test_exchange", "routingKey")
          .replyExchange("test_queue")
          .textMessage("Direct test message")
          .priority(0)
          .messageId("1")
          .replyTo("test_queue")
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
  ).maxDuration(20)
}
