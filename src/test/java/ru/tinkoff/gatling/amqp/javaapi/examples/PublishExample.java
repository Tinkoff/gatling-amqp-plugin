package ru.tinkoff.gatling.amqp.javaapi.examples;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import ru.tinkoff.gatling.amqp.javaapi.protocol.AmqpProtocolBuilder;
import ru.tinkoff.gatling.amqp.javaapi.protocol.AmqpQueue;

import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static ru.tinkoff.gatling.amqp.javaapi.AmqpDsl.*;

public class PublishExample extends Simulation {

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
            .usePersistentDeliveryMode()
            .declare(new AmqpQueue("test_q_in", false, false, false, Map.of()));

    public ScenarioBuilder scn = scenario("AMQP test")
            .feed(Utils.idFeeder)
            .exec(
                    amqp("publish to exchange")
                            .publish()
                            .queueExchange("test_q_in")
                            .textMessage("Hello message - #{id}")
                            .messageId("#{id}")
                            .priority(0)
            );


    {
        setUp(
                scn.injectOpen(
                        rampUsersPerSec(1).to(5).during(60),
                        constantUsersPerSec(5).during(300))
        )
                .protocols(amqpConf)
                .maxDuration(600);
    }
}
