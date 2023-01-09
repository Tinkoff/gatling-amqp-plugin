package ru.tinkoff.gatling.amqp.javaapi;

import static io.gatling.javaapi.core.internal.Expressions.*;

import ru.tinkoff.gatling.amqp.checks.AmqpResponseCodeCheckBuilder;
import ru.tinkoff.gatling.amqp.javaapi.check.ExtendedCheckBuilder;
import ru.tinkoff.gatling.amqp.javaapi.protocol.AmqpProtocolBuilderBase;
import ru.tinkoff.gatling.amqp.javaapi.protocol.RabbitMQConnectionFactoryBuilderBase;
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage;
import ru.tinkoff.gatling.amqp.javaapi.check.AmqpChecks;
import ru.tinkoff.gatling.amqp.javaapi.protocol.*;
import ru.tinkoff.gatling.amqp.javaapi.request.AmqpDslBuilderBase;
import scala.Function1;

public final class AmqpDsl {

    public static AmqpProtocolBuilderBase amqp() {
        return new AmqpProtocolBuilderBase();
    }

    public static AmqpDslBuilderBase amqp(String requestName) {
        return new AmqpDslBuilderBase(ru.tinkoff.gatling.amqp.Predef.amqp(toStringExpression(requestName)));
    }

    public static RabbitMQConnectionFactoryBuilderBase rabbitmq() {
        return new RabbitMQConnectionFactoryBuilderBase();
    }

    public static AmqpChecks.AmqpCheckTypeWrapper simpleCheck(Function1<AmqpProtocolMessage, Boolean> f) {
        return new AmqpChecks.AmqpCheckTypeWrapper(new AmqpChecks.SimpleChecksScala().simpleCheck(f.andThen(Boolean::valueOf)));
    }

    public static ExtendedCheckBuilder responseCode() {
        return new ExtendedCheckBuilder(AmqpResponseCodeCheckBuilder.ResponseCode());
    }
}
