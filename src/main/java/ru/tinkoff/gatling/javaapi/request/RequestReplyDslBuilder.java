package ru.tinkoff.gatling.javaapi.request;

import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.CheckBuilder;
import scala.Tuple2;

import ru.tinkoff.gatling.javaapi.checks.AmqpChecks;

import java.util.Arrays;
import java.util.List;
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

    public RequestReplyDslBuilder check(CheckBuilder... checks) {
        return check(Arrays.asList(checks));
    }
    public RequestReplyDslBuilder check(List<CheckBuilder> checks) {
        this.wrapped = wrapped.check(AmqpChecks.toScalaChecks(checks));
        return this;
    }

    public io.gatling.core.action.builder.ActionBuilder asScala(){
        return wrapped.build();
    }

}
