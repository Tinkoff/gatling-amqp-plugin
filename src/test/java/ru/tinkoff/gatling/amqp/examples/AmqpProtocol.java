package ru.tinkoff.gatling.amqp.examples;

import ru.tinkoff.gatling.javaapi.protocol.AmqpProtocolBuilder;

import static ru.tinkoff.gatling.javaapi.AmqpDsl.amqp;
import static ru.tinkoff.gatling.javaapi.AmqpDsl.rabbitmq;

public class AmqpProtocol {
    public static AmqpProtocolBuilder amqpProtocol = amqp()
            .connectionFactory(
                    rabbitmq()
                            .host("localhost")
                            .port(5672)
                            .username("rabbitmq")
                            .password("rabbitmq")
                            .vhost("/")
                            .build()
            )
            .replyTimeout(60000L)
            .consumerThreadsCount(8)
            .usePersistentDeliveryMode();
}
