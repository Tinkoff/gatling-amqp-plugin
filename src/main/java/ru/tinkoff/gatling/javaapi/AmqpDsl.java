package ru.tinkoff.gatling.javaapi;

import static io.gatling.javaapi.core.internal.Expressions.*;

import io.gatling.core.check.Check;
import ru.tinkoff.gatling.amqp.checks.AmqpResponseCodeCheckBuilder;
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage;
import ru.tinkoff.gatling.javaapi.check.AmqpChecks;
import ru.tinkoff.gatling.javaapi.check.ExtendedCheckBuilder;
import ru.tinkoff.gatling.javaapi.protocol.*;
import ru.tinkoff.gatling.javaapi.request.AmqpDslBuilderBase;
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

    public static Check<AmqpProtocolMessage> simpleCheck(Function1<AmqpProtocolMessage, Boolean> f) {
        return new AmqpChecks.SimpleChecksScala().simpleCheck(f.andThen(Boolean::valueOf));
    }

    public static ExtendedCheckBuilder responseCode() {
        return new ExtendedCheckBuilder(AmqpResponseCodeCheckBuilder.ResponseCode());
    }
}
