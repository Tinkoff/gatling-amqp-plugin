package ru.tinkoff.gatling.javaapi.request;

public final class AmqpDslBuilderBase {
    private final ru.tinkoff.gatling.amqp.request.AmqpDslBuilderBase wrapped;

    public AmqpDslBuilderBase(ru.tinkoff.gatling.amqp.request.AmqpDslBuilderBase wrapped){
        this.wrapped = wrapped;
    }

    public PublishDslBuilderExchange publish(){
        return new PublishDslBuilderExchange(wrapped.publish(io.gatling.core.Predef.configuration()));
    }
    public RequestReplyDslBuilderExchange requestReply(){
        return new RequestReplyDslBuilderExchange(wrapped.requestReply(io.gatling.core.Predef.configuration()));
    }
}
