package ru.tinkoff.gatling.amqp.request

import com.rabbitmq.client.AMQP

case class AmqpProtocolMessage(
    amqpProperties: AMQP.BasicProperties,
    payload: Array[Byte],
    responseCode: Option[String] = None
) {
  def correlationId(newValue: String): AmqpProtocolMessage =
    this.copy(amqpProperties = amqpProperties.builder.correlationId(newValue).build)

  def correlationId: String = amqpProperties.getCorrelationId

  def messageId(newValue: String): AmqpProtocolMessage =
    this.copy(amqpProperties = amqpProperties.builder.messageId(newValue).build)

  def messageId: String = amqpProperties.getMessageId
}
