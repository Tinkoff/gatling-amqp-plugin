package ru.tinkoff.gatling.amqp.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session
import ru.tinkoff.gatling.amqp.client.{AmqpConnectionPool, TrackerPool}

case class AmqpComponents(protocol: AmqpProtocol, connectionPool: AmqpConnectionPool, trackerPool: TrackerPool)
    extends ProtocolComponents {
  override def onStart: Session => Session = Session.Identity

  override def onExit: Session => Unit = ProtocolComponents.NoopOnExit

}
