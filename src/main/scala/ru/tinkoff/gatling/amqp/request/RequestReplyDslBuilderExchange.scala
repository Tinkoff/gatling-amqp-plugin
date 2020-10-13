package ru.tinkoff.gatling.amqp.request

import io.gatling.core.Predef._
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression

case class RequestReplyDslBuilderExchange(requestName: Expression[String], configuration: GatlingConfiguration) {
  def directExchange(name: Expression[String], routingKey: Expression[String]): RequestReplyDslBuilderMessage =
    destination(AmqpDirectExchange(name, routingKey))

  def topicExchange(name: Expression[String], routingKey: Expression[String]): RequestReplyDslBuilderMessage =
    destination(AmqpTopicExchange(name, routingKey))

  def queueExchange(name: Expression[String]): RequestReplyDslBuilderMessage = destination(AmqpQueueExchange(name))

  def destination(dest: AmqpExchange) = RequestReplyDslBuilderMessage(
    requestName,
    dest,
    AmqpQueueExchange(""),
    setReplyTo = false,
    None,
    configuration
  )
}
