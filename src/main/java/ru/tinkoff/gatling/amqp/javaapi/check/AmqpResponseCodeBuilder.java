package ru.tinkoff.gatling.amqp.javaapi.check;

import io.gatling.core.check.CheckBuilder;
import ru.tinkoff.gatling.amqp.checks.AmqpResponseCodeCheckBuilder.AmqpMessageCheckType;
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage;

public class AmqpResponseCodeBuilder implements io.gatling.javaapi.core.CheckBuilder{

    private final CheckBuilder<AmqpMessageCheckType, AmqpProtocolMessage> wrapped;

    public AmqpResponseCodeBuilder(CheckBuilder<AmqpMessageCheckType, AmqpProtocolMessage> wrapped){
        this.wrapped = wrapped;
    }

    @Override
    public CheckBuilder<?, ?> asScala() {
        return wrapped;
    }

    @Override
    public CheckType type() {
        return AmqpCheckType.ResponseCode;
    }
}
