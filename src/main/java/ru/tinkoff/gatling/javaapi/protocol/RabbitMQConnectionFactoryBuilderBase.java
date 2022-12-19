package ru.tinkoff.gatling.javaapi.protocol;

public class RabbitMQConnectionFactoryBuilderBase {
    public RabbitMQConnectionFactoryBuilder host(String host) {
        return new RabbitMQConnectionFactoryBuilder(ru.tinkoff.gatling.amqp.protocol.RabbitMQConnectionFactoryBuilderBase.host(host));
    }
}
