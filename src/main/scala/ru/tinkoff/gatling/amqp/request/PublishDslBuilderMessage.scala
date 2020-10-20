package ru.tinkoff.gatling.amqp.request

import java.nio.charset.Charset

import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression
import ru.tinkoff.gatling.amqp.action.PublishBuilder

case class PublishDslBuilderMessage(
    requestName: Expression[String],
    destination: AmqpExchange,
    configuration: GatlingConfiguration
) {

  def textMessage(text: Expression[String], charset: Charset = configuration.core.charset): PublishDslBuilder =
    message(TextAmqpMessage(text, charset))
  def bytesMessage(bytes: Expression[Array[Byte]]): PublishDslBuilder = message(BytesAmqpMessage(bytes))

  private def message(mess: AmqpMessage) =
    PublishDslBuilder(
      AmqpAttributes(requestName, destination, None, mess),
      PublishBuilder(_, configuration)
    )

}
