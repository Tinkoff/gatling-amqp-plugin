package ru.tinkoff.gatling.amqp

import com.eatthepath.uuid.FastUUID
import com.rabbitmq.client.BuiltinExchangeType
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

import java.util.UUID

package object protocol {
  sealed trait DeliveryMode {
    val mode: Int
  }

  final case class Persistent(mode: Int = 2)    extends DeliveryMode
  final case class NonPersistent(mode: Int = 1) extends DeliveryMode

  trait AmqpMessageMatcher {
    def prepareRequest(msg: AmqpProtocolMessage): AmqpProtocolMessage = msg
    def requestMatchId(msg: AmqpProtocolMessage): String
    def responseMatchId(msg: AmqpProtocolMessage): String
  }

  object MessageIdMessageMatcher extends AmqpMessageMatcher {
    override def requestMatchId(msg: AmqpProtocolMessage): String  = msg.messageId
    override def responseMatchId(msg: AmqpProtocolMessage): String = msg.messageId
  }

  object CorrelationIdMessageMatcher extends AmqpMessageMatcher {
    override def prepareRequest(msg: AmqpProtocolMessage): AmqpProtocolMessage =
      msg.correlationId(FastUUID.toString(UUID.randomUUID))
    override def requestMatchId(msg: AmqpProtocolMessage): String              = msg.correlationId
    override def responseMatchId(msg: AmqpProtocolMessage): String             = msg.correlationId
  }

  case class AmqpProtocolMessageMatcher(extractId: AmqpProtocolMessage => String) extends AmqpMessageMatcher {
    override def requestMatchId(msg: AmqpProtocolMessage): String  = extractId(msg)
    override def responseMatchId(msg: AmqpProtocolMessage): String = extractId(msg)
  }

  case object RabbitMQConnectionFactoryBuilderBase {
    def host(host: String): RabbitMQConnectionFactoryBuilder = RabbitMQConnectionFactoryBuilder(host = Some(host))

    /** Builder with default connection factory settings
      */
    def default: RabbitMQConnectionFactoryBuilder = RabbitMQConnectionFactoryBuilder()
  }

  sealed trait AmqpChannelInitAction

  final case class QueueDeclare(q: AmqpQueue)       extends AmqpChannelInitAction
  final case class ExchangeDeclare(e: AmqpExchange) extends AmqpChannelInitAction
  final case class BindQueue(queueName: String, exchangeName: String, routingKey: String, args: Map[String, Any])
      extends AmqpChannelInitAction

  type AmqpChannelInitActions = List[AmqpChannelInitAction]

  final case class AmqpExchange(
      name: String,
      exchangeType: BuiltinExchangeType,
      durable: Boolean,
      autoDelete: Boolean,
      arguments: Map[String, Any],
  )

  final case class AmqpQueue(
      name: String,
      durable: Boolean,
      exclusive: Boolean,
      autoDelete: Boolean,
      arguments: Map[String, Any],
  )
}
