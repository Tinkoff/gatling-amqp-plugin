package ru.tinkoff.gatling.amqp.action

import io.gatling.commons.stats.KO
import io.gatling.commons.util.Clock
import io.gatling.commons.validation.{SuccessWrapper, Validation}
import io.gatling.core.CoreComponents
import io.gatling.core.action.Action
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import ru.tinkoff.gatling.amqp.client.AmqpMessageTracker
import ru.tinkoff.gatling.amqp.protocol.AmqpComponents
import ru.tinkoff.gatling.amqp.request._

class RequestReply(
    attributes: AmqpAttributes,
    replyDestination: AmqpExchange,
    components: AmqpComponents,
    coreComponents: CoreComponents,
    val next: Action,
    throttler: Option[Throttler]
) extends AmqpAction(attributes, components, throttler, coreComponents) {

  private val replyTimeout = components.protocol.replyTimeout.getOrElse(0L)

  override val name: String = genName("amqpRequestReply")

  override val statsEngine: StatsEngine = coreComponents.statsEngine

  override def clock: Clock = coreComponents.clock

  private def resolveTracker(replyQueue: String, replyKey: String): Validation[AmqpMessageTracker] = {
    if (replyQueue.nonEmpty)
      components.trackerPool
        .tracker(
          replyQueue,
          components.protocol.messageMatcher,
          components.protocol.responseTransformer
        )
        .success
    else
      components.trackerPool
        .tracker(
          replyKey,
          components.protocol.messageMatcher,
          components.protocol.responseTransformer
        )
        .success

  }

  private val client = components.client

  override protected def publishAndLogMessage(requestNameString: String, msg: AmqpProtocolMessage, session: Session,
  ): Unit = {
    (for {
      (pubExchange, pubRoutingKey) <- resolveDestination(attributes.destination, session)
      (replyQueue, replyKey)       <- resolveDestination(replyDestination, session)
      tracker                      <- resolveTracker(replyQueue, replyKey)
      id                           <- components.protocol.messageMatcher.requestMatchId(msg).success
      startTime                    <- clock.nowMillis.success
    } yield
      client.basicPublish(pubExchange, pubRoutingKey, msg)(
        _ => {
          if (logger.underlying.isDebugEnabled) {
            logMessage(s"Message sent user=${session.userId} AMQPMessageID=${msg.messageId}", msg)
          }
          tracker.track(id, clock.nowMillis, replyTimeout, attributes.checks, session, next, requestNameString)
        },
        e =>
          executeNext(session, startTime, clock.nowMillis, KO, next, requestNameString, Some("500"), Some(e.getMessage))
      )).onFailure { m =>
      coreComponents.statsEngine.logCrash(session.scenario, session.groups, requestNameString, m)
      executeNext(session, clock.nowMillis, clock.nowMillis, KO, next, requestNameString, Some("ERROR"), Some(m))
    }
  }

}
