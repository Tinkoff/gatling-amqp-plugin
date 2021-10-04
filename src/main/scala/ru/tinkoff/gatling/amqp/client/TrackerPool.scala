package ru.tinkoff.gatling.amqp.client

import akka.actor.ActorSystem
import io.gatling.commons.util.Clock
import io.gatling.core.stats.StatsEngine
import io.gatling.core.util.NameGen
import ru.tinkoff.gatling.amqp.action.AmqpLogging
import ru.tinkoff.gatling.amqp.client.AmqpMessageTrackerActor.MessageConsumed
import ru.tinkoff.gatling.amqp.protocol.AmqpMessageMatcher
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

import java.util.concurrent.ConcurrentHashMap

final class TrackerPool(
    client: AMQPClient,
    system: ActorSystem,
    statsEngine: StatsEngine,
    clock: Clock,
) extends AmqpLogging with NameGen {

  private val trackers = new ConcurrentHashMap[String, AmqpMessageTracker]

  def tracker(
      sourceQueue: String,
      messageMatcher: AmqpMessageMatcher,
      responseTransformer: Option[AmqpProtocolMessage => AmqpProtocolMessage]
  ): AmqpMessageTracker =
    trackers.computeIfAbsent(
      sourceQueue,
      _ => {
        val actor =
          system.actorOf(AmqpMessageTrackerActor.props(statsEngine, clock), genName("amqpTrackerActor"))

        client.consumeWith(sourceQueue)(
          incomeMsg => {
            val receivedTimestamp = clock.nowMillis
            val replyId           = messageMatcher.responseMatchId(incomeMsg)
            logMessage(
              s"Message received AmqpMessageID=${incomeMsg.amqpProperties.getMessageId} matchId=$replyId",
              incomeMsg
            )
            actor ! MessageConsumed(
              replyId,
              receivedTimestamp,
              responseTransformer.map(_(incomeMsg)).getOrElse(incomeMsg)
            )
          },
          e => logger.error(e.getMessage, e)
        )

        new AmqpMessageTracker(actor)
      }
    )
}
