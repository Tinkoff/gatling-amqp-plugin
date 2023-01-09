package ru.tinkoff.gatling.amqp.javaapi.protocol;

import scala.Option;

public class RabbitMQConnectionFactoryBuilderBase {
    public RabbitMQConnectionFactoryBuilder host(String host) {
        return new RabbitMQConnectionFactoryBuilder(ru.tinkoff.gatling.amqp.protocol.RabbitMQConnectionFactoryBuilder.apply(Option.apply(host), null, null,null,null));
    }
}
