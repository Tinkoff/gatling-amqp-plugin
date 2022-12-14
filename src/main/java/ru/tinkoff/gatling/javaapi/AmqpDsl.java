package ru.tinkoff.gatling.javaapi;

import static io.gatling.javaapi.core.internal.Expressions.*;

public final class AmqpDsl {

    public static AmqpDslBuilderBase amqp(String requestName){
        return new AmqpDslBuilderBase(new ru.tinkoff.gatling.amqp.request.AmqpDslBuilderBase(toStringExpression(requestName)));//toStringExpression(requestName));
    }

}
