package ru.tinkoff.gatling.amqp.examples

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import ru.tinkoff.gatling.amqp.Predef._
import ru.tinkoff.gatling.amqp.examples.Utils.idFeeder
import ru.tinkoff.gatling.amqp.protocol.AmqpProtocolBuilder

import scala.concurrent.duration._

/**
  * Execute this test.
  * - start docker-compose for the `docker-compose.yaml` - docker-compose -f docker-compose.yml up
  * -- open http://localhost:15672 (gatling publishes messages here), user: guest, password: guest
  * -- open http://localhost:15673 (consumer writes messages here), user: guest, password: guest
  * - run `SimpleRabbitMQClient` - this class implements a consumer for queue readQueue which reads from it and writes to _writeQueue_
  * - run RequestReplyGatlingRunner from IDE - it will write to readQueue and read messages from writeQueue
  */
class RequestReplyTwoBrokerExample extends Simulation {

  val amqpConf: AmqpProtocolBuilder = amqp
    .connectionFactory(
      rabbitmq
        .host("localhost")
        .port(5672)
        .username("guest")
        .password("guest")
        .vhost("/"),
      rabbitmq
        .host("localhost")
        .port(5673)
        .username("guest")
        .password("guest")
        .vhost("/")
    )
    .replyTimeout(60000)
    .consumerThreadsCount(8)
    .matchByMessageId
    .usePersistentDeliveryMode

  val scn: ScenarioBuilder = scenario("Request Reply AMQP test")
    .feed(idFeeder)
    .exec(
      amqp("Request Reply exchange test").requestReply
        .queueExchange("readQueue")
        .replyExchange("writeQueue")
        .textMessage("""{"msg": "Hello message - ${id}"}""")
        .messageId("${id}")
        .priority(0)
        .contentType("application/json")
        .headers("test" -> "performance", "extra-test" -> "34-${id}")
        .check(
          bodyString.exists,
          bodyString.is("Message processed")
        )
    )

  setUp(
    scn.inject(
      rampUsersPerSec(1) to 5 during (60 seconds),
      constantUsersPerSec(5) during (2 minutes))
  ).protocols(amqpConf)
    .maxDuration(10 minutes)
}
