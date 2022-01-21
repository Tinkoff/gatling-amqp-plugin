package ru.tinkoff.gatling.amqp.client

import akka.actor.ActorSystem
import com.rabbitmq.client.Delivery
import io.gatling.commons.util.Clock
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.stats.StatsEngine
import io.gatling.core.util.NameGen
import ru.tinkoff.gatling.amqp.action.AmqpLogging
import ru.tinkoff.gatling.amqp.client.AmqpMessageTrackerActor.MessageConsumed
import ru.tinkoff.gatling.amqp.protocol.AmqpMessageMatcher
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

import java.util.concurrent.ConcurrentHashMap

class TrackerPool(
    pool: AmqpConnectionPool,
    system: ActorSystem,
    statsEngine: StatsEngine,
    clock: Clock,
    configuration: GatlingConfiguration,
) extends AmqpLogging with NameGen {

  private val trackers = new ConcurrentHashMap[String, AmqpMessageTracker]

  def tracker(
      sourceQueue: String,
      listenerThreadCount: Int,
      messageMatcher: AmqpMessageMatcher,
      responseTransformer: Option[AmqpProtocolMessage => AmqpProtocolMessage],
  ): AmqpMessageTracker =
    trackers.computeIfAbsent(
      sourceQueue,
      _ => {
        val actor =
          system.actorOf(AmqpMessageTrackerActor.props(statsEngine, clock), genName("amqpTrackerActor"))

        for (_ <- 1 to listenerThreadCount) {
          val consumerChannel = pool.createConsumerChannel
          consumerChannel.basicConsume(
            sourceQueue,
            true,
            (_: String, message: Delivery) => {
              val receivedTimestamp = clock.nowMillis
              val amqpMessage       = AmqpProtocolMessage(message.getProperties, message.getBody)
              val replyId           = messageMatcher.responseMatchId(amqpMessage)
              logMessage(
                s"Message received AmqpMessageID=#{message.getProperties.getMessageId} matchId=$replyId",
                amqpMessage,
              )
              actor ! MessageConsumed(
                replyId,
                receivedTimestamp,
                responseTransformer.map(_(amqpMessage)).getOrElse(amqpMessage),
              )
            },
            (_: String) => (),
          )
        }

        new AmqpMessageTracker(actor)
      },
    )
}
