package ru.tinkoff.gatling.amqp.action

import io.gatling.commons.util.Clock
import io.gatling.commons.validation.Validation
import io.gatling.core.action.Action
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import ru.tinkoff.gatling.amqp.protocol.AmqpComponents
import ru.tinkoff.gatling.amqp.request.{AmqpAttributes, AmqpProtocolMessage, _}

class RequestReply(
    attributes: AmqpAttributes,
    replyDestination: AmqpExchange,
    setAmqpReplyTo: Boolean,
    trackerDestination: Option[AmqpExchange],
    components: AmqpComponents,
    val statsEngine: StatsEngine,
    val clock: Clock,
    val next: Action,
    throttler: Throttler,
    throttled: Boolean
) extends AmqpAction(attributes, components, throttler, throttled) {

  private val replyTimeout = components.protocol.replyTimeout.getOrElse(0L)

  override def name: String = genName("amqpRequestReply")

  override protected def aroundPublish(requestName: String,
                                       session: Session,
                                       message: AmqpProtocolMessage): Validation[Around] = {
    resolveDestination(replyDestination, session).map { qName =>
      val tracker = components.trackerPool.tracker(qName,
                                                   components.protocol.consumersThreadCount,
                                                   components.protocol.messageMatcher,
                                                   components.protocol.responseTransformer)
      val id = components.protocol.messageMatcher.requestMatchId(message)
      Around(
        before = {

          if (logger.underlying.isDebugEnabled) {
            logMessage(s"Message sent matchId=$id", message)
          }
          tracker.track(id, clock.nowMillis, replyTimeout, attributes.checks, session, next, requestName)
        },
        after = {}
      )
    }

  }

  def resolveDestination(dest: AmqpExchange, session: Session): Validation[String] =
    dest match {
      case AmqpDirectExchange(name, _, _) => name(session)
      case AmqpQueueExchange(name, _)     => name(session)
    }

}
