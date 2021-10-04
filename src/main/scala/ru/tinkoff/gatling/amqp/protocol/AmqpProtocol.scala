package ru.tinkoff.gatling.amqp.protocol

import com.rabbitmq.client.ConnectionFactory
import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}
import ru.tinkoff.gatling.amqp.client.{AMQPClient, TrackerPool}
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

object AmqpProtocol {
  val amqpProtocolKey: ProtocolKey[AmqpProtocol, AmqpComponents] = new ProtocolKey[AmqpProtocol, AmqpComponents] {
    override def protocolClass: Class[Protocol] = classOf[AmqpProtocol].asInstanceOf[Class[Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): AmqpProtocol =
      throw new IllegalStateException("Can't provide a default value for AmqpProtocol")

    private val trackerPoolRef = new AtomicReference[TrackerPool]()

    private def getOrCreateTrackerPool(components: CoreComponents, client: AMQPClient) = {
      if (trackerPoolRef.get() == null) {
        trackerPoolRef.lazySet(
          new TrackerPool(
            client,
            components.actorSystem,
            components.statsEngine,
            components.clock
          )
        )
      }
      trackerPoolRef.get()
    }

    private def runInitAction(client: AMQPClient): PartialFunction[AmqpChannelInitAction, Unit] = {
      case ExchangeDeclare(e) =>
        client.exchangeDeclare(e)(_ => (), _ => ())
      case QueueDeclare(q) =>
        client.queueDeclare(q)(_ => (), _ => ())
      case b: BindQueue =>
        client.queueBind(b)(_ => (), _ => ())

    }

    override def newComponents(coreComponents: CoreComponents): AmqpProtocol => AmqpComponents =
      amqpProtocol => {
        val blockingPool = Executors.newFixedThreadPool(100)

        val client = AMQPClient.async(amqpProtocol.connectionFactory,
                                      amqpProtocol.replyConnectionFactory,
                                      blockingPool,
                                      amqpProtocol.consumersThreadCount)

        amqpProtocol.initActions.foreach(runInitAction(client))
        val trackerPool = getOrCreateTrackerPool(coreComponents, client)

        coreComponents.actorSystem.registerOnTermination(client.close())

        AmqpComponents(amqpProtocol, trackerPool, client)
      }
  }
}

case class AmqpProtocol(
    connectionFactory: ConnectionFactory,
    replyConnectionFactory: ConnectionFactory,
    deliveryMode: DeliveryMode,
    replyTimeout: Option[Long],
    consumersThreadCount: Int,
    messageMatcher: AmqpMessageMatcher,
    responseTransformer: Option[AmqpProtocolMessage => AmqpProtocolMessage],
    initActions: AmqpChannelInitActions
) extends Protocol {
  type Components = AmqpComponents
}
