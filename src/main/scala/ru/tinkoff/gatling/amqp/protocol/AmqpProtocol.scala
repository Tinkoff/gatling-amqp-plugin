package ru.tinkoff.gatling.amqp.protocol

import java.util.concurrent.atomic.AtomicReference

import com.rabbitmq.client.ConnectionFactory
import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}
import ru.tinkoff.gatling.amqp.client.{AmqpConnectionPool, TrackerPool}
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

object AmqpProtocol {
  val amqpProtocolKey: ProtocolKey[AmqpProtocol, AmqpComponents] = new ProtocolKey[AmqpProtocol, AmqpComponents] {
    override def protocolClass: Class[Protocol] = classOf[AmqpProtocol].asInstanceOf[Class[Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): AmqpProtocol =
      throw new IllegalStateException("Can't provide a default value for AmqpProtocol")

    private val trackerPoolRef    = new AtomicReference[TrackerPool]()
    private val connectionPoolRef = new AtomicReference[AmqpConnectionPool]()

    private def getOrCreateConnectionPool(protocol: AmqpProtocol) = {
      if (connectionPoolRef.get() == null) {
        connectionPoolRef.lazySet(
          new AmqpConnectionPool(protocol.connectionFactory, protocol.consumersThreadCount)
        )
      }
      connectionPoolRef.get()
    }

    private def getOrCreateTrackerPool(components: CoreComponents, pool: AmqpConnectionPool) = {
      if (trackerPoolRef.get() == null) {
        trackerPoolRef.lazySet(
          new TrackerPool(pool, components.actorSystem, components.statsEngine, components.clock, components.configuration)
        )
      }
      trackerPoolRef.get()
    }

    override def newComponents(coreComponents: CoreComponents): AmqpProtocol => AmqpComponents =
      amqpProtocol => {
        val pool = getOrCreateConnectionPool(amqpProtocol)
        coreComponents.actorSystem.registerOnTermination(pool.close())
        val trackerPool = getOrCreateTrackerPool(coreComponents, pool)
        AmqpComponents(amqpProtocol, pool, trackerPool)
      }
  }
}

case class AmqpProtocol(
    connectionFactory: ConnectionFactory,
    deliveryMode: Int,
    replyTimeout: Option[Long],
    consumersThreadCount: Int,
    messageMatcher: AmqpMessageMatcher,
    responseTransformer: Option[AmqpProtocolMessage => AmqpProtocolMessage]
) extends Protocol {
  type Components = AmqpComponents
}
