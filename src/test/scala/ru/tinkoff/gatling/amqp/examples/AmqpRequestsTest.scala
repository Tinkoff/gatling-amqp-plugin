package ru.tinkoff.gatling.amqp.examples

import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.{Session, StaticValueExpression}
import io.gatling.core.session.el._
import io.gatling.netty.util.Transports
import io.netty.channel._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.gatling.amqp.Predef._
import ru.tinkoff.gatling.amqp.action.{PublishBuilder, RequestReplyBuilder}
import ru.tinkoff.gatling.amqp.request._

import java.util.Date

class AmqpRequestsTest extends AnyFlatSpec with Matchers {
  val gatlingConfig: GatlingConfiguration = GatlingConfiguration.loadForTest()

  it should "Publish request test with topic exchange" in {
    val amqpRequest: ActionBuilder = amqp(StaticValueExpression[String]("Test request"))
      .publish(gatlingConfig)
      .topicExchange(StaticValueExpression[String]("test_queue"), StaticValueExpression[String]("routingKey"))
      .textMessage(StaticValueExpression[String]("Test message"))
      .priority(StaticValueExpression[Int](0))
      .amqpType(StaticValueExpression[String]("amqpType"))
      .build()

    val expectedRequest = PublishBuilder(
      AmqpAttributes(
        requestName = StaticValueExpression[String]("Test request"),
        destination = AmqpTopicExchange(
          name = StaticValueExpression[String]("test_queue"),
          routingKey = StaticValueExpression[String]("routingKey"),
        ),
        selector = None,
        message = TextAmqpMessage(
          text = StaticValueExpression[String]("Test message"),
          charset = gatlingConfig.core.charset,
        ),
        messageProperties = AmqpMessageProperties(
          priority = Some(StaticValueExpression[Int](0)),
          `type` = Some(StaticValueExpression[String]("amqpType")),
        ),
      ),
      gatlingConfig,
    )

    amqpRequest shouldBe expectedRequest
  }

  it should "Publish request test with direct exchange" in {
    val amqpRequest: ActionBuilder = amqp(StaticValueExpression[String]("Test request"))
      .publish(gatlingConfig)
      .directExchange(StaticValueExpression[String]("test_queue"), StaticValueExpression[String]("routingKey"))
      .textMessage(StaticValueExpression[String]("Test message"))
      .priority(StaticValueExpression[Int](0))
      .amqpType(StaticValueExpression[String]("amqpType"))
      .build()

    val expectedRequest = PublishBuilder(
      AmqpAttributes(
        requestName = StaticValueExpression[String]("Test request"),
        destination = AmqpDirectExchange(
          name = StaticValueExpression[String]("test_queue"),
          routingKey = StaticValueExpression[String]("routingKey"),
        ),
        selector = None,
        message = TextAmqpMessage(
          text = StaticValueExpression[String]("Test message"),
          charset = gatlingConfig.core.charset,
        ),
        messageProperties = AmqpMessageProperties(
          priority = Some(StaticValueExpression[Int](0)),
          `type` = Some(StaticValueExpression[String]("amqpType")),
        ),
      ),
      gatlingConfig,
    )

    amqpRequest shouldBe expectedRequest
  }

  it should "Publish request test with queue exchange" in {
    val amqpRequest: ActionBuilder = amqp(StaticValueExpression[String]("Test request"))
      .publish(gatlingConfig)
      .queueExchange(StaticValueExpression[String]("test_queue"))
      .textMessage(StaticValueExpression[String]("Test message"))
      .priority(StaticValueExpression[Int](0))
      .amqpType(StaticValueExpression[String]("amqpType"))
      .build()

    val expectedRequest = PublishBuilder(
      AmqpAttributes(
        requestName = StaticValueExpression[String]("Test request"),
        destination = AmqpQueueExchange(
          name = StaticValueExpression[String]("test_queue"),
        ),
        selector = None,
        message = TextAmqpMessage(
          text = StaticValueExpression[String]("Test message"),
          charset = gatlingConfig.core.charset,
        ),
        messageProperties = AmqpMessageProperties(
          priority = Some(StaticValueExpression[Int](0)),
          `type` = Some(StaticValueExpression[String]("amqpType")),
        ),
      ),
      gatlingConfig,
    )

    amqpRequest shouldBe expectedRequest
  }

  it should "Amqp properties to basic properties conversion" in {

    val eventLoopGroup: EventLoopGroup =
      Transports.newEventLoopGroup(gatlingConfig.netty.useNativeTransport, gatlingConfig.netty.useIoUring, 0, "gatling")
    val session                        = Session("Test session", 1L, eventLoopGroup.next()).copy(attributes = Map("testHeader" -> "myHeader"))

    val messageProperties = AmqpMessageProperties(
      priority = Some(StaticValueExpression[Int](0)),
      `type` = Some(StaticValueExpression[String]("amqpType")),
      headers = Map(
        "header" -> "testHeader".el[String],
      ),
    )

    AmqpMessageProperties.toBasicProperties(messageProperties, session)
  }

  it should "RequestReply request test" in {
    val date = new Date()

    val amqpRequest: ActionBuilder = amqp(StaticValueExpression[String]("Test request"))
      .requestReply(gatlingConfig)
      .queueExchange(
        StaticValueExpression[String]("test_queue"),
      )
      .noReplyTo
      .replyExchange(StaticValueExpression[String]("test_queue_out"))
      .textMessage(StaticValueExpression[String]("Test message"))
      .messageId(StaticValueExpression[String]("messageId"))
      .priority(StaticValueExpression[Int](0))
      .contentType(StaticValueExpression[String]("contentType"))
      .contentEncoding(StaticValueExpression[String]("contentEncoding"))
      .correlationId(StaticValueExpression[String]("correlationId"))
      .expiration(StaticValueExpression[String]("expiration"))
      .timestamp(StaticValueExpression[Date](date))
      .amqpType(StaticValueExpression[String]("amqpType"))
      .build()

    val expectedRequest = RequestReplyBuilder(
      AmqpAttributes(
        requestName = StaticValueExpression[String]("Test request"),
        destination = AmqpQueueExchange(
          name = StaticValueExpression[String]("test_queue"),
        ),
        selector = None,
        message = TextAmqpMessage(
          text = StaticValueExpression[String]("Test message"),
          charset = gatlingConfig.core.charset,
        ),
        messageProperties = AmqpMessageProperties(
          contentType = Some(StaticValueExpression[String]("contentType")),
          priority = Some(StaticValueExpression[Int](0)),
          `type` = Some(StaticValueExpression[String]("amqpType")),
          contentEncoding = Some(StaticValueExpression[String]("contentEncoding")),
          correlationId = Some(StaticValueExpression[String]("correlationId")),
          expiration = Some(StaticValueExpression[String]("expiration")),
          timestamp = Some(StaticValueExpression[Date](date)),
          messageId = Some(StaticValueExpression[String]("messageId")),
        ),
      ),
      AmqpQueueExchange(name = StaticValueExpression[String]("test_queue_out")),
      setReplyTo = false,
      gatlingConfig,
    )

    amqpRequest shouldBe expectedRequest
  }

}
