package ru.tinkoff.gatling.amqp.action

import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.Clock
import io.gatling.core.CoreComponents
import io.gatling.core.action.Action
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import ru.tinkoff.gatling.amqp.protocol.AmqpComponents
import ru.tinkoff.gatling.amqp.request.{AmqpAttributes, AmqpProtocolMessage}

final class Publish(
    attributes: AmqpAttributes,
    components: AmqpComponents,
    coreComponents: CoreComponents,
    throttler: Option[Throttler],
    val next: Action
) extends AmqpAction(attributes, components, throttler, coreComponents) {
  override val name: String = genName("amqpPublish")
  private val client        = components.client

  override val statsEngine: StatsEngine = coreComponents.statsEngine

  override val clock: Clock = coreComponents.clock

  override protected def publishAndLogMessage(requestNameString: String, msg: AmqpProtocolMessage, session: Session,
  ): Unit = {
    val startTime = clock.nowMillis
    (for {
      (exchange, routingKey) <- resolveDestination(attributes.destination, session)
    } yield
      client.basicPublish(exchange, routingKey, msg)(
        _ => {
          if (logger.underlying.isDebugEnabled) {
            logMessage(s"Message sent user=${session.userId} AMQPMessageID=${msg.messageId}", msg)
          }
          executeNext(session, startTime, clock.nowMillis, OK, next, requestNameString, None, None)
        },
        e => {
          val (message, code) = handleException(e)
          executeNext(session, startTime, clock.nowMillis, KO, next, requestNameString, Option(code), Option(message))
        }
      )).onFailure { m =>
      coreComponents.statsEngine.logCrash(session.scenario, session.groups, requestNameString, m)
      executeNext(session, clock.nowMillis, clock.nowMillis, KO, next, requestNameString, Some("ERROR"), Some(m))
    }

  }
}
