package ru.tinkoff.gatling.javaapi.aqmp.examples

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.*
import ru.tinkoff.gatling.javaapi.AmqpDsl.*
import ru.tinkoff.gatling.javaapi.protocol.AmqpProtocolBuilder

class PublishExample : Simulation() {
    val amqpConf: AmqpProtocolBuilder = amqp()
        .connectionFactory(
            rabbitmq()
                .host("localhost")
                .port(5672)
                .username("guest")
                .password("guest")
                .vhost("/")
                .build()
        )
        .usePersistentDeliveryMode()
        .declare(AmqpQueue("test_q_in", false, false, false, mapOf<String, Any>()))

    val scn: ScenarioBuilder = scenario("AMQP test")
        .feed(Utils.idFeeder)
        .exec(
            amqp("publish to exchange")
                .publish()
                .queueExchange("test_q_in")
                .textMessage("Hello message - #{id}")
                .messageId("#{id}")
                .priority(0),
        );

    init {
        setUp(
            scn.injectOpen(
                rampUsersPerSec(1.0).to(5.0).during(60),
                constantUsersPerSec(5.0).during(5 * 60)
            ),
        ).protocols(amqpConf)
            .maxDuration(10 * 60)
    }
}