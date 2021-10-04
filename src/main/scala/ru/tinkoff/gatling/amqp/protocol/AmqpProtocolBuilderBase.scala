package ru.tinkoff.gatling.amqp.protocol

import com.rabbitmq.client.ConnectionFactory

case object AmqpProtocolBuilderBase {
  def connectionFactory(cf: ConnectionFactory): AmqpProtocolBuilder = AmqpProtocolBuilder(cf, cf)
  def connectionFactory(
      requestConnectionFactory: ConnectionFactory,
      replyConnectionFactory: ConnectionFactory
  ): AmqpProtocolBuilder =
    AmqpProtocolBuilder(requestConnectionFactory, replyConnectionFactory)
}
