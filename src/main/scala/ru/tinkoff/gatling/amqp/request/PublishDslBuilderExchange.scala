package ru.tinkoff.gatling.amqp.request

import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression

case class PublishDslBuilderExchange(
    requestName: Expression[String],
    configuration: GatlingConfiguration
) {
  def topicExchange(name: Expression[String], routingKey: Expression[String]):PublishDslBuilderMessage =
    destination(AmqpTopicExchange(name, routingKey = routingKey))

  def directExchange(name: Expression[String], routingKey: Expression[String]): PublishDslBuilderMessage =
    destination(AmqpDirectExchange(name, routingKey))

  def queueExchange(name: Expression[String]): PublishDslBuilderMessage = destination(AmqpQueueExchange(name))

  protected def destination(dest: AmqpExchange) = PublishDslBuilderMessage(requestName, dest, configuration)
}
