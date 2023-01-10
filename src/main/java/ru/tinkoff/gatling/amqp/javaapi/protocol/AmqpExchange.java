package ru.tinkoff.gatling.amqp.javaapi.protocol;

import com.rabbitmq.client.BuiltinExchangeType;

import java.util.Map;

public class AmqpExchange {
    private String name;
    private boolean durable;
    private BuiltinExchangeType exchangeType;
    private boolean autoDelete;
    private Map<String, Object> arguments;

    public AmqpExchange(String name, BuiltinExchangeType exchangeType, boolean durable, boolean autoDelete, Map<String, Object> arguments) {
        this.name = name;
        this.durable = durable;
        this.exchangeType = exchangeType;
        this.autoDelete = autoDelete;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public BuiltinExchangeType getExchangeType() {
        return exchangeType;
    }

    public boolean getDurable() {
        return durable;
    }

    public boolean getAutoDelete() {
        return autoDelete;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }
}
