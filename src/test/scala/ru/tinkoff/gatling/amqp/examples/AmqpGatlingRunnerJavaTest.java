package ru.tinkoff.gatling.amqp.examples;

import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;

import io.gatling.javaapi.core.Simulation;

import static io.gatling.javaapi.core.CoreDsl.*;
import static ru.tinkoff.gatling.amqp.javaapi.AmqpDsl.*;

public class AmqpGatlingRunnerJavaTest extends Simulation {
    {
        setUp(
                scenario("Java Scenario Test")
                        .exec(
                                amqp("Publish Test")
                                        .publish()
                                        .queueExchange("test_queue")
                                        .textMessage("test message")
                                        .messageId("1")
                                        .priority(0)
                        )
                        .exec(
                                amqp("Request Reply test")
                                        .requestReply()
                                        .queueExchange("test_queue")
                                        .replyExchange("test_queue")
                                        .textMessage("test message")
                                        .messageId("1")
                                        .priority(0)
                                        .contentType("String")
//                                        .headers(Map.of("test", "performance", "extra-test", "34-#{id}"))
                                        .check(
                                                xpath("").notExists(),
                                                jsonPath("").notExists(),
                                                bodyString().exists(),
                                                bodyString().is("Message processed"),
                                                simpleCheck((msg) -> msg.messageId().contains("Some"))
                                        )
                        )
                        .injectOpen(atOnceUsers(1))
        ).protocols(amqp().connectionFactory(rabbitmq()
                                .host("localhost")
                                .port(5672)
                                .username("rabbitmq")
                                .password("rabbitmq")
                                .vhost("/")
                                .build()
                        )
                        .replyTimeout(60000L)
                        .consumerThreadsCount(8)
                        .usePersistentDeliveryMode()
        );
    }
}
