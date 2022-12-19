package ru.tinkoff.gatling.amqp.examples;

import io.gatling.javaapi.core.ScenarioBuilder;
import static ru.tinkoff.gatling.amqp.examples.AmqpActions.*;

import static io.gatling.javaapi.core.CoreDsl.*;

public class AmqpScenario {

    public static ScenarioBuilder scn = scenario("AMQP Scenario")
            .exec(action)
            .exec(action2);
}
