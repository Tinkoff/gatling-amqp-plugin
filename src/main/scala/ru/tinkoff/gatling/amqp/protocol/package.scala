package ru.tinkoff.gatling.amqp

import java.util.UUID

import com.eatthepath.uuid.FastUUID
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

package object protocol {
  trait AmqpMessageMatcher {
    def prepareRequest(msg: AmqpProtocolMessage): Unit
    def requestMatchId(msg: AmqpProtocolMessage): String
    def responseMatchId(msg: AmqpProtocolMessage): String
  }

  object MessageIdMessageMatcher extends AmqpMessageMatcher {
    override def prepareRequest(msg: AmqpProtocolMessage): Unit    = {}
    override def requestMatchId(msg: AmqpProtocolMessage): String  = msg.messageId
    override def responseMatchId(msg: AmqpProtocolMessage): String = msg.messageId
  }

  object CorrelationIdMessageMatcher extends AmqpMessageMatcher {
    override def prepareRequest(msg: AmqpProtocolMessage): Unit    = msg.correlationId(FastUUID.toString(UUID.randomUUID))
    override def requestMatchId(msg: AmqpProtocolMessage): String  = msg.correlationId
    override def responseMatchId(msg: AmqpProtocolMessage): String = msg.correlationId
  }

  case class AmqpProtocolMessageMatcher(extractId: AmqpProtocolMessage => String) extends AmqpMessageMatcher {
    override def prepareRequest(msg: AmqpProtocolMessage): Unit = {}

    override def requestMatchId(msg: AmqpProtocolMessage): String = extractId(msg)

    override def responseMatchId(msg: AmqpProtocolMessage): String = extractId(msg)
  }

  case object RabbitMQConnectionFactoryBuilderBase {
    def host(host: String): RabbitMQConnectionFactoryBuilder = RabbitMQConnectionFactoryBuilder(host = Some(host))

    /**
      * Builder with default connection factory settings
      * */
    def default: RabbitMQConnectionFactoryBuilder = RabbitMQConnectionFactoryBuilder()
  }
}
