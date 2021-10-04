package ru.tinkoff.gatling.amqp.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session
import ru.tinkoff.gatling.amqp.client.{AMQPClient, TrackerPool}

case class AmqpComponents(
    protocol: AmqpProtocol,
    trackerPool: TrackerPool,
    client: AMQPClient
) extends ProtocolComponents {
  override def onStart: Session => Session = Session.Identity

  override def onExit: Session => Unit = ProtocolComponents.NoopOnExit

}
