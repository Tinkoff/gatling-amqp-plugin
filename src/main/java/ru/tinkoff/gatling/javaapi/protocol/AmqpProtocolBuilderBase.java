package ru.tinkoff.gatling.javaapi.protocol;

import com.rabbitmq.client.ConnectionFactory;

public class AmqpProtocolBuilderBase {

    private final ru.tinkoff.gatling.amqp.protocol.AmqpProtocolBuilderBase$ wrapped;

    public AmqpProtocolBuilderBase(ru.tinkoff.gatling.amqp.protocol.AmqpProtocolBuilderBase$ wrapped){
        this.wrapped = wrapped;
    }

    public AmqpProtocolBuilder connectionFactory(ConnectionFactory cf){
        return new AmqpProtocolBuilder(ru.tinkoff.gatling.amqp.protocol.AmqpProtocolBuilderBase.connectionFactory(cf));
    }

    public AmqpProtocolBuilder connectionFactory(ConnectionFactory requestConnectionFactory, ConnectionFactory replyConnectionFactory){
        return new AmqpProtocolBuilder(ru.tinkoff.gatling.amqp.protocol.AmqpProtocolBuilderBase.connectionFactory(requestConnectionFactory, replyConnectionFactory));
    }
}
