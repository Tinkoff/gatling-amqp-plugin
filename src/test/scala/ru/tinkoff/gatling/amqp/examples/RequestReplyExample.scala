package ru.tinkoff.gatling.amqp.examples

import com.rabbitmq.client.BuiltinExchangeType
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import ru.tinkoff.gatling.amqp.Predef._
import ru.tinkoff.gatling.amqp.examples.Utils.idFeeder
import ru.tinkoff.gatling.amqp.protocol.AmqpProtocolBuilder

import scala.concurrent.duration._

class RequestReplyExample extends Simulation {
  private val topic    = exchange("test_queue_in", BuiltinExchangeType.TOPIC)
  private val innerQ   = queue("test_queue_inner_in")
  private val outQueue = queue("test_queue_out")

  val amqpConf: AmqpProtocolBuilder = amqp
    .connectionFactory(
      rabbitmq
        .host("localhost")
        .port(5672)
        .username("guest")
        .password("guest")
        .vhost("/"),
    )
    .replyTimeout(60000)
    .consumerThreadsCount(8)
    .matchByMessageId
    .usePersistentDeliveryMode
    .declare(topic)
    .declare(innerQ)
    .declare(outQueue)
    .bindQueue(innerQ, topic, "we")

  val scn: ScenarioBuilder = scenario("AMQP test")
    .feed(idFeeder)
    .exec(
      amqp("Request Reply exchange test").requestReply
        .topicExchange("test_queue_in", "we")
        .replyExchange("test_queue_out")
        .textMessage("""{"msg": "Hello message - #{id}"}""")
        .messageId("#{id}")
        .priority(0)
        .contentType("application/json")
        .headers("test" -> "performance", "extra-test" -> "34-#{id}")
        .check(
          bodyString.exists,
          bodyString.is("Message processed"),
        ),
    )

  setUp(
    scn.inject(rampUsersPerSec(1) to 5 during (60 seconds), constantUsersPerSec(5) during (2 minutes)),
  ).protocols(amqpConf)
    .maxDuration(10 minutes)
}
