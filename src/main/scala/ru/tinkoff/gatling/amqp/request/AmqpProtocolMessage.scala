package ru.tinkoff.gatling.amqp.request

import java.lang.reflect.Field

import com.rabbitmq.client.AMQP
import com.softwaremill.quicklens._

case class AmqpProtocolMessage(amqpProperties: AMQP.BasicProperties,
                               payload: Array[Byte],
                               responseCode: Option[String] = None) {

  def mergeProperties(props: Map[String, Any]): AmqpProtocolMessage = {
    this
      .modify(_.amqpProperties)
      .using(
        amqpProps =>
          amqpProps.getClass.getDeclaredFields.foldLeft(amqpProps)(
            (p, field) => forceModify(p, field, props.getOrElse(field.getName, null))
        ))
  }

  private def forceModify[T, V](obj: T, field: Field, fieldValue: V): T = {
    if (field.canAccess(obj)) {
      field.set(obj, fieldValue)
    } else {
      field.setAccessible(true)
      field.set(obj, fieldValue)
      field.setAccessible(false)
    }
    obj
  }

  def correlationId(newValue: String): AmqpProtocolMessage =
    this.copy(amqpProperties = amqpProperties.builder.correlationId(newValue).build)

  def correlationId: String = amqpProperties.getCorrelationId

  def messageId(newValue: String): AmqpProtocolMessage =
    this.copy(amqpProperties = amqpProperties.builder.messageId(newValue).build)

  def messageId: String = amqpProperties.getMessageId
}
