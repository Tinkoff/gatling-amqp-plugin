package ru.tinkoff.gatling.amqp.request

import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression
import ru.tinkoff.gatling.amqp.action.RequestReplyBuilder

import java.nio.charset.Charset

case class RequestReplyDslBuilderMessage(
    requestName: Expression[String],
    destination: AmqpExchange,
    replyDest: AmqpExchange,
    setReplyTo: Boolean,
    messageSelector: Option[String],
    configuration: GatlingConfiguration
) {

  /**
    * Add a reply queue
    */
  def replyExchange(name: Expression[String]): RequestReplyDslBuilderMessage = replyDestination(AmqpQueueExchange(name))
  private def replyDestination(destination: AmqpExchange)                    = this.copy(replyDest = destination)
  def noReplyTo: RequestReplyDslBuilderMessage                               = this.copy(setReplyTo = false)

  def textMessage(text: Expression[String], charset: Charset = configuration.core.charset): RequestReplyDslBuilder =
    message(TextAmqpMessage(text, charset))

  def bytesMessage(bytes: Expression[Array[Byte]]): RequestReplyDslBuilder = message(BytesAmqpMessage(bytes))

  private def message(mess: AmqpMessage) =
    RequestReplyDslBuilder(
      AmqpAttributes(requestName, destination, messageSelector, mess),
      RequestReplyBuilder(_, replyDest, setReplyTo, configuration)
    )
}
