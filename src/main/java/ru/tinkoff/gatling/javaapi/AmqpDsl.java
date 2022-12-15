package ru.tinkoff.gatling.javaapi;

import ru.tinkoff.gatling.javaapi.request.AmqpDslBuilderBase;

import static io.gatling.javaapi.core.internal.Expressions.*;

public final class AmqpDsl {

    public static AmqpDslBuilderBase amqp(String requestName){
        return new AmqpDslBuilderBase(new ru.tinkoff.gatling.amqp.request.AmqpDslBuilderBase(toStringExpression(requestName)));
    }

}
