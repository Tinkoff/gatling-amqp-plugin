package ru.tinkoff.gatling.amqp.examples

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.rabbitmq.client.ConnectionFactory
import io.gatling.core.config.GatlingConfiguration
import ru.tinkoff.gatling.amqp.Predef._
import io.gatling.core.CoreComponents
import ru.tinkoff.gatling.amqp.protocol._
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

class AmqpProtocolTest extends AnyFlatSpec with Matchers {
  val gatlingConfig: GatlingConfiguration = GatlingConfiguration.loadForTest()

  it should "Connection factory test" in {
    val amqpConf: ConnectionFactory =
      rabbitmq(gatlingConfig)
        .host("localhost")
        .port(5672)
        .username("guest")
        .password("guest")
        .vhost("/")
        .build

    val expectedConnectionFactory = new ConnectionFactory()
    expectedConnectionFactory.setHost("localhost")
    expectedConnectionFactory.setPort(5672)
    expectedConnectionFactory.setUsername("guest")
    expectedConnectionFactory.setPassword("guest")
    expectedConnectionFactory.setVirtualHost("/")

    amqpConf.getHost shouldBe "localhost"
    amqpConf.getPort shouldBe 5672
    amqpConf.getUsername shouldBe "guest"
    amqpConf.getPassword shouldBe "guest"
    amqpConf.getVirtualHost shouldBe "/"
  }

  it should "Protocol builder test with persistent delivery mode " in {

    def transformFunction(mess: AmqpProtocolMessage): AmqpProtocolMessage = {
      mess.messageId(s"${mess.messageId}.")
    }

    val amqpProtocolBuilder: AmqpProtocol = amqp(gatlingConfig)
      .connectionFactory(
        rabbitmq(gatlingConfig)
          .host("localhost")
          .port(5672)
          .username("guest")
          .password("guest")
          .vhost("/"),
        rabbitmq(gatlingConfig)
          .host("localhost")
          .port(5673)
          .username("guest")
          .password("guest")
          .vhost("/"),
      )
      .declare(
        AmqpQueue(
          name = "test_queue",
          durable = true,
          exclusive = false,
          autoDelete = false,
          arguments = Map.empty[String, Any],
        ),
      )
      .replyTimeout(60000)
      .consumerThreadsCount(8)
      .matchByMessageId
      .usePersistentDeliveryMode
      .responseTransform(transformFunction)
      .build

    amqpProtocolBuilder.connectionFactory.getHost shouldBe "localhost"
    amqpProtocolBuilder.connectionFactory.getPort shouldBe 5672
    amqpProtocolBuilder.connectionFactory.getUsername shouldBe "guest"
    amqpProtocolBuilder.connectionFactory.getPassword shouldBe "guest"
    amqpProtocolBuilder.connectionFactory.getVirtualHost shouldBe "/"
    amqpProtocolBuilder.replyConnectionFactory.getHost shouldBe "localhost"
    amqpProtocolBuilder.replyConnectionFactory.getPort shouldBe 5673
    amqpProtocolBuilder.replyConnectionFactory.getUsername shouldBe "guest"
    amqpProtocolBuilder.replyConnectionFactory.getPassword shouldBe "guest"
    amqpProtocolBuilder.replyConnectionFactory.getVirtualHost shouldBe "/"
    amqpProtocolBuilder.deliveryMode shouldBe Persistent()
    amqpProtocolBuilder.replyTimeout shouldBe Some(60000)
    amqpProtocolBuilder.consumersThreadCount shouldBe 8
    amqpProtocolBuilder.messageMatcher shouldBe MessageIdMessageMatcher
    amqpProtocolBuilder.responseTransformer shouldBe transformFunction(_: AmqpProtocolMessage)
    amqpProtocolBuilder.initActions shouldBe List(
      QueueDeclare(
        AmqpQueue(
          name = "test_queue",
          durable = true,
          exclusive = false,
          autoDelete = false,
          arguments = Map.empty[String, Any],
        ),
      ),
    )
  }

}
