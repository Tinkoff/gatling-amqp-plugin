package ru.tinkoff.gatling.amqp.action

import io.gatling.commons.stats.OK
import io.gatling.commons.util.Clock
import io.gatling.commons.validation.{Validation, _}
import io.gatling.core.action.Action
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import ru.tinkoff.gatling.amqp.protocol.AmqpComponents
import ru.tinkoff.gatling.amqp.request.{AmqpAttributes, AmqpProtocolMessage}

class Publish(
    attributes: AmqpAttributes,
    components: AmqpComponents,
    val statsEngine: StatsEngine,
    val clock: Clock,
    val next: Action,
    throttler: Option[Throttler]
) extends AmqpAction(attributes, components, throttler) {
  override val name: String = genName("amqpPublish")

  override protected def aroundPublish(
      requestName: String,
      session: Session,
      message: AmqpProtocolMessage
  ): Validation[Around] =
    Around(
      before = {
        if (logger.underlying.isDebugEnabled) {
          logMessage(s"Message sent user=${session.userId} AMQPMessageID=${message.messageId}", message)
        }

        val now = clock.nowMillis

        statsEngine.logResponse(session.scenario, session.groups, requestName, now, now, OK, None, None)

        next ! session
      },
      after = ()
    ).success
}
