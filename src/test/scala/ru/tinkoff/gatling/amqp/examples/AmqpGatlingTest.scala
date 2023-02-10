package ru.tinkoff.gatling.amqp.examples

import com.rabbitmq.client.BuiltinExchangeType
import io.gatling.core.Predef._
import ru.tinkoff.gatling.amqp.javaapi.AmqpDsl._
import ru.tinkoff.gatling.amqp.javaapi.protocol._

class AmqpGatlingTest extends Simulation {

  val testQueue = new AmqpQueue("test_queue",true,false,false, java.util.Map.of())
  val testExchange = new AmqpExchange("test_exchange", BuiltinExchangeType.TOPIC,true,false, java.util.Map.of())


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
          .asScala(),
      )
      .exec(
        amqp("Test topic exchange").requestReply
          .topicExchange("test_exchange", "routingKey")
          .replyExchange("test_queue")
          .textMessage("Topic test message")
          .priority(0)
          .messageId("2")
          .asScala(),
      )
      .exec(
        amqp("Test direct exchange").requestReply
          .directExchange("test_exchange", "routingKey")
          .replyExchange("test_queue")
          .textMessage("Direct test message")
          .priority(0)
          .messageId("3")
          .replyTo("test_queue")
          .appId("appId")
          .check(
            simpleCheck(msg => msg.messageId.matches("3"))
          )
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
      .declare(testExchange)
      .declare(testQueue)
      .bindQueue(testQueue, testExchange,"routingKey", java.util.Map.of())
      .replyTimeout(60000)
      .consumerThreadsCount(8)
      .usePersistentDeliveryMode()
      .protocol(),
  ).maxDuration(20)
}
