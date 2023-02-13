package ru.tinkoff.gatling.amqp.examples

import com.rabbitmq.client.BuiltinExchangeType
import io.gatling.core.Predef._
import ru.tinkoff.gatling.amqp.javaapi.AmqpDsl._
import ru.tinkoff.gatling.amqp.javaapi.protocol._
import io.gatling.javaapi.core.CoreDsl.{bodyBytes, bodyString, jmesPath, jsonPath, substring, xpath}

import java.util
import scala.jdk.javaapi.CollectionConverters.asJava

class AmqpGatlingTest extends Simulation {

  val testQueue = new AmqpQueue("test_queue",true,false,false, java.util.Map.of())
  val testExchange = new AmqpExchange("test_exchange", BuiltinExchangeType.TOPIC,true,false, java.util.Map.of())

  setUp(
    scenario("Test Scenario")
      .exec{s => s.set("bytes", "RR topic test message".getBytes)}
      .exec(
        amqp("Test publish queue exchange").publish
          .queueExchange("test_queue")
          .textMessage("Publish queue test message")
          .priority(0)
          .messageId("1")
          .correlationId("1")
          .contentEncoding("text/plain")
          .contentType("text")
          .asScala(),
      )
      .exec(
        amqp("Test publish topic exchange").publish
          .topicExchange("test_exchange","routingKey")
          .textMessage("Publish topic test message")
          .priority(0)
          .messageId("2")
          .userId("rabbitmq")
          .asScala(),
      )
      .exec(
        amqp("Test publish direct exchange").publish
          .directExchange("test_exchange", "routingKey")
          .textMessage("Publish direct test message")
          .priority(0)
          .messageId("3")
          .appId("test_app")
          .replyTo("test_queue")
          .asScala(),
      )
      .exec(
        amqp("Test request reply queue exchange").requestReply
          .queueExchange("test_queue")
          .replyExchange("test_queue")
          .textMessage("""{"message":"RR queue test message"}""")
          .contentEncoding("application/json")
          .contentType("json")
          .priority(0)
          .messageId("4")
          .check(
            jsonPath("$.message").is("RR queue test message"),
            jmesPath("message").is("RR queue test message")
          )
          .asScala(),
      )
      .exec(
        amqp("Test request reply topic exchange").requestReply
          .topicExchange("test_exchange", "routingKey")
          .replyExchange("test_queue")
          .bytesMessage("#{bytes}")
          .contentEncoding("application/x-binary")
          .priority(0)
          .messageId("5")
          .check(
            bodyBytes.is("RR topic test message".getBytes)
          )
          .asScala(),
      )
      .exec(
        amqp("Test request reply direct exchange").requestReply
          .directExchange("test_exchange", "routingKey")
          .replyExchange("test_queue")
          .textMessage("RR direct test message")
          .priority(0)
          .messageId("6")
          .check(
            simpleCheck(msg => msg.messageId.matches("6")),
            bodyString.is("RR direct test message"),
            substring("RR").exists()
          )
          .asScala(),
      )
      .exec(
        amqp("Test xpath check").requestReply
          .directExchange("test_exchange", "routingKey")
          .replyExchange("test_queue")
          .textMessage(
            """
              |<Request>
              |    <message>xpath check</message>
              |</Request>
              |""".stripMargin)
          .priority(0)
          .messageId("7")
          .check(
            xpath("""//Request/message""").is("xpath check")
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
      .replyTimeout(2000)
      .consumerThreadsCount(1)
      .usePersistentDeliveryMode()
      .protocol(),
  )
    .maxDuration(20)
    .assertions(
      global.failedRequests.percent.is(0.0)
    )
}
