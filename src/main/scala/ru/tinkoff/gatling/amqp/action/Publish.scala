package ru.tinkoff.gatling.amqp.action

import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.Clock
import io.gatling.core.action.Action
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.stats.StatsEngine
import ru.tinkoff.gatling.amqp.client.AmqpPublisher
import ru.tinkoff.gatling.amqp.protocol.AmqpComponents
import ru.tinkoff.gatling.amqp.request.{AmqpAttributes, AmqpProtocolMessage}

class Publish(
    attributes: AmqpAttributes,
    components: AmqpComponents,
    val statsEngine: StatsEngine,
    val clock: Clock,
    val next: Action,
    throttler: Option[Throttler],
) extends AmqpAction(attributes, components, throttler) {
  override val name: String = genName("amqpPublish")

  override val requestName: Expression[String] = attributes.requestName

  override protected def publishAndLogMessage(
      requestNameString: String,
      msg: AmqpProtocolMessage,
      session: Session,
      publisher: AmqpPublisher,
  ): Unit = {
    val now = clock.nowMillis
    try {
      publisher.publish(msg, session)
      if (logger.underlying.isDebugEnabled) {
        logMessage(s"Message sent user=${session.userId} AMQPMessageID=${msg.messageId}", msg)
      }
      statsEngine.logResponse(session.scenario, session.groups, requestNameString, now, clock.nowMillis, OK, None, None)
    } catch {
      case e: Throwable =>
        logger.error(e.getMessage, e)
        statsEngine.logResponse(
          session.scenario,
          session.groups,
          requestNameString,
          now,
          clock.nowMillis,
          KO,
          Some("500"),
          Some(e.getMessage),
        )
    }
    next ! session
  }
}
