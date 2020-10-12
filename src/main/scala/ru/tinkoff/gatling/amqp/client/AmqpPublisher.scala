package ru.tinkoff.gatling.amqp.client

import io.gatling.core.session.Session
import javax.jms.DeliveryMode
import ru.tinkoff.gatling.amqp.action.Around
import ru.tinkoff.gatling.amqp.protocol.AmqpComponents
import ru.tinkoff.gatling.amqp.request.{AmqpDirectExchange, AmqpTopicExchange, AmqpExchange, AmqpProtocolMessage, AmqpQueueExchange}

import scala.collection.JavaConverters._

class AmqpPublisher(destination: AmqpExchange, components: AmqpComponents) extends WithAmqpChannel {
  def publish(message: AmqpProtocolMessage, around: Around, session: Session): Unit = {

    val protocolDurable = components.protocol.deliveryMode == DeliveryMode.PERSISTENT

    destination match {
      case AmqpDirectExchange(name, routingKey, durable) =>
        for {
          exName <- name(session)
          exKey  <- routingKey(session)
        } withChannel { channel =>
          channel.exchangeDeclare(exName, "direct", durable || protocolDurable)
          around(channel.basicPublish(exName, exKey, message.amqpProperties, message.payload))
        }

      case AmqpQueueExchange(name, durable) =>
        name(session).foreach(qName =>
          withChannel { channel =>
            channel.queueDeclare(qName, durable || protocolDurable, false, false, Map.empty[String, Object].asJava)
            around(channel.basicPublish("", qName, message.amqpProperties, message.payload))
        })

      case AmqpTopicExchange(name, routingKey, durable) =>
        for {
          exName <- name(session)
          exKey  <- routingKey(session)
        } withChannel { channel =>
          channel.exchangeDeclare(exName, "topic", durable || protocolDurable)
          around(channel.basicPublish(exName, exKey, message.amqpProperties, message.payload))
        }
    }
  }

  override protected val pool: AmqpConnectionPool = components.connectionPool
}
