package ru.tinkoff.gatling.amqp.javaapi.request;

import java.util.Map;
import static io.gatling.javaapi.core.internal.Expressions.*;

import io.gatling.javaapi.core.ActionBuilder;
import scala.Tuple2;

public class PublishDslBuilder implements ActionBuilder {

    private ru.tinkoff.gatling.amqp.request.PublishDslBuilder wrapped;

    public PublishDslBuilder(ru.tinkoff.gatling.amqp.request.PublishDslBuilder wrapped) {
        this.wrapped = wrapped;
    }

    public PublishDslBuilder messageId(String value) {
        this.wrapped = wrapped.messageId(toStringExpression(value));
        return this;
    }

    public PublishDslBuilder priority(Integer value) {
        this.wrapped = wrapped.priority(toIntExpression(value.toString()));
        return this;
    }

    public PublishDslBuilder contentType(String value) {
        this.wrapped = wrapped.contentType(toStringExpression(value));
        return this;
    }

    public PublishDslBuilder contentEncoding(String value) {
        this.wrapped = wrapped.contentEncoding(toStringExpression(value));
        return this;
    }

    public PublishDslBuilder correlationId(String value) {
        this.wrapped = wrapped.correlationId(toStringExpression(value));
        return this;
    }

    public PublishDslBuilder replyTo(String value) {
        this.wrapped = wrapped.replyTo(toStringExpression(value));
        return this;
    }

    public PublishDslBuilder expiration(String value) {
        this.wrapped = wrapped.expiration(toStringExpression(value));
        return this;
    }

    public PublishDslBuilder timestamp(String value) {
        this.wrapped = wrapped.timestamp(toExpression(value, java.util.Date.class));
        return this;
    }

    public PublishDslBuilder amqpType(String value) {
        this.wrapped = wrapped.amqpType(toStringExpression(value));
        return this;
    }

    public PublishDslBuilder userId(String value) {
        this.wrapped = wrapped.userId(toStringExpression(value));
        return this;
    }

    public PublishDslBuilder appId(String value) {
        this.wrapped = wrapped.appId(toStringExpression(value));
        return this;
    }

    public PublishDslBuilder clusterId(String value) {
        this.wrapped = wrapped.clusterId(toStringExpression(value));
        return this;
    }

    public PublishDslBuilder header(String key, String value) {
        this.wrapped = wrapped.header(key, toStringExpression(value));
        return this;
    }

    public PublishDslBuilder headers(Map<String, String> values) {
        var headersList = values
                .entrySet()
                .stream()
                .map(pair ->
                        Tuple2.apply(pair.getKey(), toStringExpression(pair.getValue()))
                ).toList();
        this.wrapped = wrapped.headers(scala.jdk.javaapi.CollectionConverters.asScala(headersList).toSeq());
        return this;
    }

    public io.gatling.core.action.builder.ActionBuilder asScala(){
        return wrapped.build();
    }
}
