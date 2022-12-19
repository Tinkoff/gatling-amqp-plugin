package ru.tinkoff.gatling.javaapi.request;

import io.gatling.javaapi.core.ActionBuilder;
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Map;

import static io.gatling.javaapi.core.internal.Expressions.*;

public class RequestReplyDslBuilder implements ActionBuilder {
    private ru.tinkoff.gatling.amqp.request.RequestReplyDslBuilder wrapped;

    public RequestReplyDslBuilder(ru.tinkoff.gatling.amqp.request.RequestReplyDslBuilder wrapped) {
        this.wrapped = wrapped;
    }

    public RequestReplyDslBuilder messageId(String value) {
        this.wrapped = wrapped.messageId(toStringExpression(value));
        return this;
    }

    public RequestReplyDslBuilder priority(Integer value) {
        this.wrapped = wrapped.priority(toIntExpression(value.toString()));
        return this;
    }

    public RequestReplyDslBuilder contentType(String value) {
        this.wrapped = wrapped.contentType(toStringExpression(value));
        return this;
    }

    public RequestReplyDslBuilder contentEncoding(String value) {
        this.wrapped = wrapped.contentEncoding(toStringExpression(value));
        return this;
    }

    public RequestReplyDslBuilder correlationId(String value) {
        this.wrapped = wrapped.correlationId(toStringExpression(value));
        return this;
    }

    public RequestReplyDslBuilder replyTo(String value) {
        this.wrapped = wrapped.replyTo(toStringExpression(value));
        return this;
    }

    public RequestReplyDslBuilder expiration(String value) {
        this.wrapped = wrapped.expiration(toStringExpression(value));
        return this;
    }

    public RequestReplyDslBuilder timestamp(String value) {
        this.wrapped = wrapped.timestamp(toExpression(value, java.util.Date.class));
        return this;
    }

    public RequestReplyDslBuilder amqpType(String value) {
        this.wrapped = wrapped.amqpType(toStringExpression(value));
        return this;
    }

    public RequestReplyDslBuilder userId(String value) {
        this.wrapped = wrapped.userId(toStringExpression(value));
        return this;
    }

    public RequestReplyDslBuilder appId(String value) {
        this.wrapped = wrapped.appId(toStringExpression(value));
        return this;
    }

    public RequestReplyDslBuilder clusterId(String value) {
        this.wrapped = wrapped.clusterId(toStringExpression(value));
        return this;
    }

    public RequestReplyDslBuilder header(String key, String value) {
        this.wrapped = wrapped.header(key, toStringExpression(value));
        return this;
    }

    public RequestReplyDslBuilder headers(Map<String, String> values) {
        var headersList = values
                .entrySet()
                .stream()
                .map(pair ->
                        Tuple2.apply(pair.getKey(), toStringExpression(pair.getValue()))
                ).toList();
        this.wrapped = wrapped.headers(scala.jdk.javaapi.CollectionConverters.asScala(headersList).toSeq());
        return this;
    }

    public RequestReplyDslBuilder check(io.gatling.core.check.Check<AmqpProtocolMessage>... checks) {
        this.wrapped = wrapped.check(scala.jdk.javaapi.CollectionConverters.asScala(Arrays.stream(checks).toList()).toSeq());
        return this;
    }

    public io.gatling.core.action.builder.ActionBuilder asScala(){
        return wrapped.build();
    }

}
