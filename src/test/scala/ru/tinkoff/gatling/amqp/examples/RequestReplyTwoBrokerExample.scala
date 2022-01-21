package ru.tinkoff.gatling.amqp.examples

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import jodd.util.ThreadUtil
import ru.tinkoff.gatling.amqp.Predef._
import ru.tinkoff.gatling.amqp.examples.Utils.idFeeder
import ru.tinkoff.gatling.amqp.examples.utils.SimpleRabbitMQClient
import ru.tinkoff.gatling.amqp.protocol.AmqpProtocolBuilder

import scala.Console.println
import scala.concurrent.duration._

/** Execute this test.
  *   - start docker-compose for the `docker-compose.yaml` - docker-compose -f docker-compose.yml up
  * -- open http://localhost:15672 (gatling publishes messages here), user: guest, password: guest
  * -- open http://localhost:15673 (consumer writes messages here), user: guest, password: guest
  *   - run RequestReplyGatlingRunner from IDE - it will
  * -- start the messageConsumer SimpleRabbitMQClient
  * -- gatling publish messages to readQueue, simpleClient reads them
  * -- gatling receives messages from writeQueue, simple client has them written
  */
class RequestReplyTwoBrokerExample extends Simulation {

  before {
    // For this test-example we define a consumer in your setup this should not be required, because
    // you already have a rabbitmq-consumer.
    SimpleRabbitMQClient.setup()
    SimpleRabbitMQClient.readAndWrite()
  }

  after {
    SimpleRabbitMQClient.tearDown()
  }

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
        .vhost("/"),
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
