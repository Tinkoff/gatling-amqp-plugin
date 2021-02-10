package ru.tinkoff.gatling.amqp.protocol

import com.rabbitmq.client.{Channel, ConnectionFactory}
import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}
import ru.tinkoff.gatling.amqp.client.{AmqpConnectionPool, TrackerPool}
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

import java.util.concurrent.atomic.AtomicReference
import scala.collection.JavaConverters._

object AmqpProtocol {
  val amqpProtocolKey: ProtocolKey[AmqpProtocol, AmqpComponents] = new ProtocolKey[AmqpProtocol, AmqpComponents] {
    override def protocolClass: Class[Protocol] = classOf[AmqpProtocol].asInstanceOf[Class[Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): AmqpProtocol =
      throw new IllegalStateException("Can't provide a default value for AmqpProtocol")

    private val trackerPoolRef           = new AtomicReference[TrackerPool]()
    private val connectionPublishPoolRef = new AtomicReference[AmqpConnectionPool]()
    private val connectionReplyPoolRef   = new AtomicReference[AmqpConnectionPool]()

    private def getOrCreateConnectionPublishPool(protocol: AmqpProtocol) = {
      if (connectionPublishPoolRef.get() == null) {
        connectionPublishPoolRef.lazySet(
          new AmqpConnectionPool(protocol.connectionFactory, protocol.consumersThreadCount)
        )
      }
      connectionPublishPoolRef.get()
    }

    private def getOrCreateConnectionReplyPool(protocol: AmqpProtocol) = {
      if (connectionReplyPoolRef.get() == null) {
        connectionReplyPoolRef.lazySet(
          new AmqpConnectionPool(protocol.replyConnectionFactory, protocol.consumersThreadCount)
        )
      }
      connectionReplyPoolRef.get()
    }

    private def getOrCreateTrackerPool(components: CoreComponents, pool: AmqpConnectionPool) = {
      if (trackerPoolRef.get() == null) {
        trackerPoolRef.lazySet(
          new TrackerPool(pool, components.actorSystem, components.statsEngine, components.clock, components.configuration)
        )
      }
      trackerPoolRef.get()
    }

    private def toJavaMap(map: Map[String, Any]): java.util.Map[String, Object] =
      map.asJava.asInstanceOf[java.util.Map[String, Object]]

    private def runInitAction(c: Channel): PartialFunction[AmqpChannelInitAction, Unit] = {
      case ExchangeDeclare(e) =>
        c.exchangeDeclare(e.name, e.exchangeType, e.durable, e.autoDelete, toJavaMap(e.arguments))
      case QueueDeclare(q) =>
        c.queueDeclare(q.name, q.durable, q.exclusive, q.autoDelete, toJavaMap(q.arguments))
      case BindQueue(q, e, rk, args) =>
        c.queueBind(q, e, rk, toJavaMap(args))

    }

    override def newComponents(coreComponents: CoreComponents): AmqpProtocol => AmqpComponents =
      amqpProtocol => {
        val requestPool = getOrCreateConnectionPublishPool(amqpProtocol)
        coreComponents.actorSystem.registerOnTermination(requestPool.close())
        val replyPool = getOrCreateConnectionReplyPool(amqpProtocol)
        coreComponents.actorSystem.registerOnTermination(replyPool.close())

        amqpProtocol.initActions.foreach(runInitAction(requestPool.channel))
        val trackerPool = getOrCreateTrackerPool(coreComponents, replyPool)

        AmqpComponents(amqpProtocol, requestPool, replyPool, trackerPool)
      }
  }
}

case class AmqpProtocol(
    connectionFactory: ConnectionFactory,
    replyConnectionFactory: ConnectionFactory,
    deliveryMode: Int,
    replyTimeout: Option[Long],
    consumersThreadCount: Int,
    messageMatcher: AmqpMessageMatcher,
    responseTransformer: Option[AmqpProtocolMessage => AmqpProtocolMessage],
    initActions: AmqpChannelInitActions
) extends Protocol {
  type Components = AmqpComponents
}
