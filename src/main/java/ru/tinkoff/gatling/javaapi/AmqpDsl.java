package ru.tinkoff.gatling.javaapi;

import static io.gatling.javaapi.core.internal.Expressions.*;

import ru.tinkoff.gatling.javaapi.protocol.*;
import ru.tinkoff.gatling.javaapi.request.AmqpDslBuilderBase;

public final class AmqpDsl {

    public static AmqpProtocolBuilderBase amqp(){
        return new AmqpProtocolBuilderBase(ru.tinkoff.gatling.amqp.Predef.amqp(io.gatling.core.Predef.configuration()));
    }
    public static AmqpDslBuilderBase amqp(String requestName){
        return new AmqpDslBuilderBase(ru.tinkoff.gatling.amqp.Predef.amqp(toStringExpression(requestName)));
    }

    public static RabbitMQConnectionFactoryBuilderBase rabbitmq(){
        return new RabbitMQConnectionFactoryBuilderBase();
    }
}
