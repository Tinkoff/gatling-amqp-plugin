package ru.tinkoff.gatling.amqp.javaapi.examples

import com.rabbitmq.client.BuiltinExchangeType
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.ScenarioBuilder
import io.gatling.javaapi.core.Simulation
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage
import ru.tinkoff.gatling.amqp.javaapi.AmqpDsl.*
import ru.tinkoff.gatling.amqp.javaapi.protocol.*

class RequestReplyExample: Simulation(){
    private val topic = AmqpExchange("test_queue_in", BuiltinExchangeType.TOPIC, false, false, mapOf())
    private val innerQ = AmqpQueue("test_queue_inner_in", false, false, false, mapOf())
    private val outQueue = AmqpQueue("test_queue_out", false, false, false, mapOf())

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
        .matchByMessageId()
        .usePersistentDeliveryMode()
        .declare(topic)
        .declare(innerQ)
        .declare(outQueue)
        .bindQueue(innerQ, topic, "we", mapOf())

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
                    bodyString().`is`("Message processed"),
                    simpleCheck { msg: AmqpProtocolMessage ->
                        msg.messageId().contains("Some")
                    }
                )
        )

    init {
        setUp(
            scn.injectOpen(
                rampUsersPerSec(1.0).to(5.0).during(60),
                constantUsersPerSec(5.0).during(2 * 60)
            )
        ).protocols(amqpConf)
            .maxDuration(10 * 60);
    }
}