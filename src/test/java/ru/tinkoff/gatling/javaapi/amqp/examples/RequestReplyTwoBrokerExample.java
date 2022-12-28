package ru.tinkoff.gatling.javaapi.amqp.examples;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import ru.tinkoff.gatling.amqp.examples.utils.SimpleRabbitMQClient;
import ru.tinkoff.gatling.javaapi.protocol.*;

import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static ru.tinkoff.gatling.javaapi.AmqpDsl.*;

public class RequestReplyTwoBrokerExample extends Simulation {

    @Override
    public void before() {
        // For this test-example we define a consumer in your setup this should not be required, because
        // you already have a rabbitmq-consumer.
        SimpleRabbitMQClient.setup();
        SimpleRabbitMQClient.readAndWrite();
    }

    @Override
    public void after() {
        SimpleRabbitMQClient.tearDown();
    }

    public AmqpProtocolBuilder amqpConf = amqp()
            .connectionFactory(
                    rabbitmq()
                            .host("localhost")
                            .port(5672)
                            .username("guest")
                            .password("guest")
                            .vhost("/")
                            .build(),
                    rabbitmq()
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
            .usePersistentDeliveryMode();

    public ScenarioBuilder scn =

            scenario("Request Reply AMQP test")
                    .feed(Utils.idFeeder)
                    .exec(
                            amqp("Request Reply exchange test").requestReply()
                                    .queueExchange("readQueue")
                                    .replyExchange("writeQueue")
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
                scn.injectOpen(rampUsersPerSec(1)
                                .to(5)
                                .during(60),
                        constantUsersPerSec(5)
                                .during(2 * 60)

                ))
                .protocols(amqpConf)
                .maxDuration(10 * 60);
    }
}
