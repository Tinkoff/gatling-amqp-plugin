package ru.tinkoff.gatling.amqp.client

import io.gatling.core.session.Session
import ru.tinkoff.gatling.amqp.action.Around
import ru.tinkoff.gatling.amqp.protocol.AmqpComponents
import ru.tinkoff.gatling.amqp.request._

class AmqpPublisher(destination: AmqpExchange, components: AmqpComponents) extends WithAmqpChannel {
  def publish(message: AmqpProtocolMessage, around: Around, session: Session): Unit = {

    destination match {
      case AmqpDirectExchange(name, routingKey, _) =>
        for {
          exName <- name(session)
          exKey  <- routingKey(session)
        } withChannel(channel => around(channel.basicPublish(exName, exKey, message.amqpProperties, message.payload)))

      case AmqpQueueExchange(name, _)              =>
        name(session).foreach(qName =>
          withChannel(channel => around(channel.basicPublish("", qName, message.amqpProperties, message.payload)))
        )

      case AmqpTopicExchange(name, routingKey, _)  =>
        for {
          exName <- name(session)
          exKey  <- routingKey(session)
        } withChannel(channel => around(channel.basicPublish(exName, exKey, message.amqpProperties, message.payload)))
    }
  }

  override protected val pool: AmqpConnectionPool = components.connectionPublishPool
}
