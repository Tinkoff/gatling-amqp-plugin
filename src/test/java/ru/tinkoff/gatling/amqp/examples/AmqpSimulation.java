package ru.tinkoff.gatling.amqp.examples;

import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static ru.tinkoff.gatling.amqp.examples.AmqpScenario.*;

import io.gatling.javaapi.core.Simulation;

public class AmqpSimulation extends Simulation {
    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(AmqpProtocol.amqpProtocol);
    }
}
