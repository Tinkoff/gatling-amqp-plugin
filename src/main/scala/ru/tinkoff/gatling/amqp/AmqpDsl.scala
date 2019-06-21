package ru.tinkoff.gatling.amqp

import com.rabbitmq.client.ConnectionFactory
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression
import ru.tinkoff.gatling.amqp.checks.AmqpCheckSupport
import ru.tinkoff.gatling.amqp.protocol._
import ru.tinkoff.gatling.amqp.request.{AmqpDslBuilderBase, PublishDslBuilder, RequestReplyDslBuilder}

trait AmqpDsl extends AmqpCheckSupport {
  def amqp(implicit configuration: GatlingConfiguration): AmqpProtocolBuilderBase.type = AmqpProtocolBuilderBase

  def amqp(requestName: Expression[String]) = AmqpDslBuilderBase(requestName)

  def rabbitmq(implicit configuration: GatlingConfiguration): RabbitMQConnectionFactoryBuilderBase.type =
    RabbitMQConnectionFactoryBuilderBase

  implicit def amqpProtocolBuilder2amqpProtocol(builder: AmqpProtocolBuilder): AmqpProtocol = builder.build

  implicit def amqpPublishDslBuilder2ActionBuilder(builder: PublishDslBuilder): ActionBuilder = builder.build()

  implicit def amqpRequestReplyDslBuilder2ActionBuilder(builder: RequestReplyDslBuilder): ActionBuilder = builder.build()

  implicit def rabbitMQ2ConnectionFactory(builder: RabbitMQConnectionFactoryBuilder): ConnectionFactory = builder.build
}
