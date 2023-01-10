package ru.tinkoff.gatling.amqp.javaapi.protocol;

import java.util.Map;

public class AmqpQueue {
    private String name;
    private boolean durable;
    private boolean exclusive;
    private boolean autoDelete;
    private Map<String, Object> arguments;

    public AmqpQueue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments) {
        this.name = name;
        this.durable = durable;
        this.exclusive = exclusive;
        this.autoDelete = autoDelete;
        this.arguments = arguments;
    }

    public String getName(){
        return name;
    }

    public boolean getDurable(){
        return durable;
    }

    public boolean getExclusive(){
        return exclusive;
    }

    public boolean getAutoDelete(){
        return autoDelete;
    }

    public Map<String, Object> getArguments(){
        return arguments;
    }
}
