package ru.tinkoff.gatling.amqp.javaapi.examples;

import com.rabbitmq.client.BuiltinExchangeType;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import ru.tinkoff.gatling.amqp.javaapi.protocol.AmqpExchange;
import ru.tinkoff.gatling.amqp.javaapi.protocol.AmqpProtocolBuilder;
import ru.tinkoff.gatling.amqp.javaapi.protocol.AmqpQueue;

import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static ru.tinkoff.gatling.amqp.javaapi.AmqpDsl.*;

public class RequestReplyExample extends Simulation {
    private AmqpExchange topic = new AmqpExchange("test_queue_in", BuiltinExchangeType.TOPIC, false, false, Map.of());
    private AmqpQueue innerQ = new AmqpQueue("test_queue_inner_in", false, false, false, Map.of());
    private AmqpQueue outQueue = new AmqpQueue("test_queue_out", false, false, false, Map.of());

    public AmqpProtocolBuilder amqpConf = amqp()
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
            .bindQueue(innerQ, topic, "we", Map.of());

    public ScenarioBuilder scn = scenario("AMQP test")
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
                            .headers(Map.of("test", "performance", "extra-test", "34-#{id}"))
                            .check(
                                    bodyString().exists(),
                                    bodyString().is("Message processed"),
                                    simpleCheck((msg) -> msg.messageId().contains("Some"))
                            )
            );

    {
        setUp(
                scn.injectOpen(
                        rampUsersPerSec(1).to(5).during(60),
                        constantUsersPerSec(5).during(2 * 60))
        ).protocols(amqpConf)
                .maxDuration(10 * 60);
    }
}
