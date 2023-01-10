package ru.tinkoff.gatling.amqp.javaapi.examples;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import ru.tinkoff.gatling.amqp.javaapi.protocol.AmqpProtocolBuilder;
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage;

import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static ru.tinkoff.gatling.amqp.javaapi.AmqpDsl.*;

public class RequestReplyWithOwnMatchingExample extends Simulation {

    public String matchByMessage(AmqpProtocolMessage message) {
        // do something with the message and extract the values you are interested in
        // method is called:
        // - for each message which will be sent out
        // - for each message which has been received
        return "1"; // just returning something,
    }

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
            .matchByMessage(this::matchByMessage)
            .usePersistentDeliveryMode();

    ScenarioBuilder scn = scenario("AMQP test")
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
                                    bodyString().is("Message processed")
                            )
            );

    {
        setUp(
                scn.injectOpen(
                        rampUsersPerSec(1)
                                .to(5)
                                .during(60),
                        constantUsersPerSec(5)
                                .during(2 * 60))
        ).protocols(amqpConf)
                .maxDuration(10 * 60);
    }
}
