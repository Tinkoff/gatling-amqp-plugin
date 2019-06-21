package ru.tinkoff.gatling.amqp.protocol

import com.rabbitmq.client.ConnectionFactory

case object AmqpProtocolBuilderBase {
  def connectionFactory(cf: ConnectionFactory) = AmqpProtocolBuilder(cf)
}
