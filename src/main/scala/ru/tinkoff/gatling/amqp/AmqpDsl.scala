package ru.tinkoff.gatling.amqp

import com.rabbitmq.client.{BuiltinExchangeType, ConnectionFactory}
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression
import ru.tinkoff.gatling.amqp.checks.AmqpCheckSupport
import ru.tinkoff.gatling.amqp.protocol._
import ru.tinkoff.gatling.amqp.request.{AmqpDslBuilderBase, PublishDslBuilder, RequestReplyDslBuilder}

trait AmqpDsl extends AmqpCheckSupport {
  def amqp(implicit configuration: GatlingConfiguration): AmqpProtocolBuilderBase.type = AmqpProtocolBuilderBase

  def amqp(requestName: Expression[String]): AmqpDslBuilderBase = AmqpDslBuilderBase(requestName)

  def rabbitmq(implicit configuration: GatlingConfiguration): RabbitMQConnectionFactoryBuilderBase.type =
    RabbitMQConnectionFactoryBuilderBase

  def rabbitReceiver(implicit configuration: GatlingConfiguration): RabbitMQConnectionFactoryBuilderBase.type =
    RabbitMQConnectionFactoryBuilderBase

  def queue(
      name: String,
      durable: Boolean = true,
      exclusive: Boolean = false,
      autoDelete: Boolean = true,
      arguments: Map[String, Any] = Map.empty
  ): AmqpQueue =
    AmqpQueue(name, durable, exclusive, autoDelete, arguments)

  def exchange(
      name: String,
      exchangeType: BuiltinExchangeType,
      durable: Boolean = true,
      autoDelete: Boolean = true,
      arguments: Map[String, Any] = Map.empty
  ): AmqpExchange =
    AmqpExchange(name, exchangeType, durable, autoDelete, arguments)

  implicit def amqpProtocolBuilder2amqpProtocol(builder: AmqpProtocolBuilder): AmqpProtocol = builder.build

  implicit def amqpPublishDslBuilder2ActionBuilder(builder: PublishDslBuilder): ActionBuilder = builder.build()

  implicit def amqpRequestReplyDslBuilder2ActionBuilder(builder: RequestReplyDslBuilder): ActionBuilder =
    builder.build()

  implicit def rabbitMQ2ConnectionFactory(builder: RabbitMQConnectionFactoryBuilder): ConnectionFactory = builder.build
}
