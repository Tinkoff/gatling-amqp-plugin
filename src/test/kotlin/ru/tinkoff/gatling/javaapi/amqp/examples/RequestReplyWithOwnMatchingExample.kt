package ru.tinkoff.gatling.javaapi.amqp.examples

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage
import ru.tinkoff.gatling.javaapi.AmqpDsl.*

class RequestReplyWithOwnMatchingExample : Simulation() {

    fun matchByMessage(message: AmqpProtocolMessage): String {
//         do something with the message and extract the values you are interested in
//         method is called:
//         - for each message which will be sent out
//         - for each message which has been received
        return "1" // just returning something,
    }

    val amqpConf = amqp()
        .connectionFactory(
            rabbitmq()
                .host("localhost")
                .port(5672)
                .username("guest")
                .password("guest")
                .vhost("/")
                .build()
        )
        .replyTimeout(60000L)
        .consumerThreadsCount(8)
        .matchByMessage { message: AmqpProtocolMessage ->
            matchByMessage(
                message
            )
        }
        .usePersistentDeliveryMode()

    val scn: ScenarioBuilder = scenario("AMQP test")
        .feed(Utils.idFeeder)
        .exec(
            amqp("Request Reply exchange test")
                .requestReply()
                .topicExchange("test_queue_in", "we")
                .replyExchange("test_queue_out")
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