package ru.tinkoff.gatling.amqp.action

import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.Clock
import io.gatling.commons.validation.Validation
import io.gatling.core.action.Action
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import ru.tinkoff.gatling.amqp.client.AmqpPublisher
import ru.tinkoff.gatling.amqp.protocol.AmqpComponents
import ru.tinkoff.gatling.amqp.request.{AmqpAttributes, AmqpProtocolMessage, _}

class RequestReply(
    attributes: AmqpAttributes,
    replyDestination: AmqpExchange,
    components: AmqpComponents,
    val statsEngine: StatsEngine,
    val clock: Clock,
    val next: Action,
    throttler: Option[Throttler],
) extends AmqpAction(attributes, components, throttler) {

  private val replyTimeout = components.protocol.replyTimeout.getOrElse(0L)

  override val name: String = genName("amqpRequestReply")

  def resolveDestination(dest: AmqpExchange, session: Session): Validation[String] =
    dest match {
      case AmqpDirectExchange(name, _, _) => name(session)
      case AmqpQueueExchange(name, _)     => name(session)
      case AmqpTopicExchange(name, _, _)  => name(session)
    }

  override protected def publishAndLogMessage(
      requestNameString: String,
      msg: AmqpProtocolMessage,
      session: Session,
      publisher: AmqpPublisher,
  ): Unit =
    resolveDestination(replyDestination, session).map { qName =>
      val tracker = components.trackerPool.tracker(
        qName,
        components.protocol.consumersThreadCount,
        components.protocol.messageMatcher,
        components.protocol.responseTransformer,
      )
      val id      = components.protocol.messageMatcher.requestMatchId(msg)
      val now     = clock.nowMillis
      try {
        publisher.publish(msg, session)
        if (logger.underlying.isDebugEnabled) {
          logMessage(s"Message sent user=${session.userId} AMQPMessageID=${msg.messageId}", msg)
        }
        tracker.track(id, clock.nowMillis, replyTimeout, attributes.checks, session, next, requestNameString)
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
    }
}
