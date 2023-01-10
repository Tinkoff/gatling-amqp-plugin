package ru.tinkoff.gatling.amqp.javaapi.request;

import static io.gatling.javaapi.core.internal.Expressions.*;
public class PublishDslBuilderExchange {

    private final ru.tinkoff.gatling.amqp.request.PublishDslBuilderExchange wrapped;

    public PublishDslBuilderExchange(ru.tinkoff.gatling.amqp.request.PublishDslBuilderExchange wrapped) {
        this.wrapped = wrapped;
    }

    public PublishDslBuilderMessage topicExchange(String name, String routingKey){
        return new PublishDslBuilderMessage(wrapped.topicExchange(toStringExpression(name),toStringExpression(routingKey)));
    }

    public PublishDslBuilderMessage directExchange(String name, String routingKey){
        return new PublishDslBuilderMessage(wrapped.directExchange(toStringExpression(name),toStringExpression(routingKey)));
    }

    public PublishDslBuilderMessage queueExchange(String name){
        return new PublishDslBuilderMessage(wrapped.queueExchange(toStringExpression(name)));
    }
}
