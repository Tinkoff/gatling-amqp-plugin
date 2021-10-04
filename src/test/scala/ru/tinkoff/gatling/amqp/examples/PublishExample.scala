package ru.tinkoff.gatling.amqp.examples

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import ru.tinkoff.gatling.amqp.Predef._
import ru.tinkoff.gatling.amqp.examples.Utils._
import ru.tinkoff.gatling.amqp.protocol.AmqpProtocolBuilder

import scala.concurrent.duration._

class PublishExample extends Simulation {

  val amqpConf: AmqpProtocolBuilder = amqp
    .connectionFactory(
      rabbitmq
        .host("localhost")
        .port(5672)
        .username("guest")
        .password("guest")
        .vhost("my_vhost")
    )
    .usePersistentDeliveryMode
    .declare(queue("test_q_in"))

  def scn(i: Int): ScenarioBuilder = scenario(s"AMQP test $i")
    .feed(idFeeder)
    .exec(
      amqp("publish to exchange").publish
        .queueExchange("test_q_in")
        .textMessage("Hello message - ${id}")
        .messageId("${id}")
        .priority(0)
    )

  setUp(
    scn(1).inject(rampUsersPerSec(1) to 5 during (60 seconds), constantUsersPerSec(5) during (1 minutes)),
    scn(2).inject(rampUsersPerSec(1) to 5 during (60 seconds), constantUsersPerSec(5) during (1 minutes)),
    scn(3).inject(rampUsersPerSec(1) to 5 during (60 seconds), constantUsersPerSec(5) during (1 minutes)),
    scn(4).inject(rampUsersPerSec(1) to 5 during (60 seconds), constantUsersPerSec(5) during (1 minutes)),
    scn(5).inject(rampUsersPerSec(1) to 5 during (60 seconds), constantUsersPerSec(5) during (1 minutes)),
    scn(6).inject(rampUsersPerSec(1) to 5 during (60 seconds), constantUsersPerSec(5) during (1 minutes)),
//    scn(8).inject(rampUsersPerSec(1) to 5 during (60 seconds), constantUsersPerSec(5) during (1 minutes)),
  ).protocols(amqpConf)
    .maxDuration(10 minutes)

}
