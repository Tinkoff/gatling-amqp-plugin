package ru.tinkoff.gatling.amqp.request

import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session._

case class AmqpDslBuilderBase(requestName: Expression[String]) {
  def publish(implicit configuration: GatlingConfiguration)      = PublishDslBuilderExchange(requestName, configuration)
  def requestReply(implicit configuration: GatlingConfiguration) =
    RequestReplyDslBuilderExchange(requestName, configuration)
}
