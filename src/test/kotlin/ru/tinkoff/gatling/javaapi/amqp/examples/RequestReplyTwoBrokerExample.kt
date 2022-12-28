package ru.tinkoff.gatling.javaapi.amqp.examples

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.ScenarioBuilder
import io.gatling.javaapi.core.Simulation
import ru.tinkoff.gatling.javaapi.AmqpDsl
import ru.tinkoff.gatling.javaapi.AmqpDsl.amqp
import ru.tinkoff.load.cosmos.feeders.Feeders


class RequestReplyTwoBrokerExample : Simulation() {
    override fun before() {
        // For this test-example we define a consumer in your setup this should not be required, because
        // you already have a rabbitmq-consumer.
        SimpleRabbitMQClient.setup()
        SimpleRabbitMQClient.readAndWrite()
    }

    override fun after() {
        SimpleRabbitMQClient.tearDown()
    }

    val amqpConf = AmqpDsl.amqp()
        .connectionFactory(
            AmqpDsl.rabbitmq()
                .host("localhost")
                .port(5672)
                .username("guest")
                .password("guest")
                .vhost("/")
                .build(),
            AmqpDsl.rabbitmq()
                .host("localhost")
                .port(5673)
                .username("guest")
                .password("guest")
                .vhost("/")
                .build()
        )
        .replyTimeout(60000L)
        .consumerThreadsCount(8)
        .matchByMessageId()
        .usePersistentDeliveryMode()

    val scn: ScenarioBuilder = CoreDsl.scenario("Request Reply AMQP test")
        .feed(Utils.idFeeder)
        .exec(
            amqp("Request Reply exchange test")
                .requestReply()
                .queueExchange("readQueue")
                .replyExchange("writeQueue")
                .textMessage("{\"msg\": \"Hello message - #{id}\"}")
                .messageId("#{id}")
                .priority(0)
                .contentType("application/json")
                .headers(mapOf(Pair("test", "performance"), Pair("extra-test", "34-#{id}")))
                .check(
                    bodyString().exists(),
                    bodyString().`is`("Message processed")
                )
        )

    init {
        setUp(
            scn.injectOpen(
                rampUsersPerSec(1.0)
                    .to(5.0)
                    .during(60),
                constantUsersPerSec(5.0)
                    .during(2 * 60)

            )
        )
            .protocols(amqpConf)
            .maxDuration(10 * 60);
    }
}