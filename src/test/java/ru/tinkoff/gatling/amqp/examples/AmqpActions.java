package ru.tinkoff.gatling.amqp.examples;

import static ru.tinkoff.gatling.javaapi.AmqpDsl.*;

import ru.tinkoff.gatling.javaapi.request.PublishDslBuilder;
import ru.tinkoff.gatling.javaapi.request.RequestReplyDslBuilder;

public class AmqpActions {
    public static PublishDslBuilder action =
            amqp("action")
                    .publish()
                    .queueExchange("test_queue_hw")
                    .textMessage("Hello message - ${messageId}")
                    .messageId("1")
                    .priority(0);

    public static RequestReplyDslBuilder action2 = amqp("action2")
            .requestReply()
            .queueExchange("test_queue_hw")
            .replyExchange("test_queue_hw")
            .textMessage("Hello message - #{messageId}")
            .messageId("1234");
}
