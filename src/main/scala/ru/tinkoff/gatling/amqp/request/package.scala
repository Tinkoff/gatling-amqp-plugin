package ru.tinkoff.gatling.amqp

import com.rabbitmq.client.MessageProperties
import io.gatling.commons.validation.Validation
import io.gatling.core.session.{Expression, Session}

import java.nio.charset.Charset

package object request {

  sealed trait AmqpExchange

  case class AmqpTopicExchange(name: Expression[String], routingKey: Expression[String], durable: Boolean = false)
      extends AmqpExchange

  case class AmqpDirectExchange(name: Expression[String], routingKey: Expression[String], durable: Boolean = false)
      extends AmqpExchange

  case class AmqpQueueExchange(name: Expression[String], durable: Boolean = false) extends AmqpExchange

  sealed trait AmqpMessage {
    private[amqp] def amqpProtocolMessage(session: Session): Validation[AmqpProtocolMessage]
  }

  case class TextAmqpMessage(text: Expression[String], charset: Charset) extends AmqpMessage {
    override private[amqp] def amqpProtocolMessage(session: Session) =
      text(session).map(str => AmqpProtocolMessage(MessageProperties.MINIMAL_BASIC, str.getBytes(charset)))
  }
  case class BytesAmqpMessage(bytes: Expression[Array[Byte]]) extends AmqpMessage {
    override private[amqp] def amqpProtocolMessage(session: Session) =
      bytes(session).map(AmqpProtocolMessage(MessageProperties.MINIMAL_BASIC, _))
  }

}
