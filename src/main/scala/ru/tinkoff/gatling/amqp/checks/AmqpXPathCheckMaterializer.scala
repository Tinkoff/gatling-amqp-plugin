package ru.tinkoff.gatling.amqp.checks

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import io.gatling.commons.validation.{safely, _}
import io.gatling.core.check.xpath.{XPathCheckType, XmlParsers}
import io.gatling.core.check.{CheckMaterializer, Preparer}
import ru.tinkoff.gatling.amqp.AmqpCheck
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

class AmqpXPathCheckMaterializer()
  extends CheckMaterializer[XPathCheckType, AmqpCheck, AmqpProtocolMessage, Any](identity) {
  private val ErrorMapper = "Could not parse response into a DOM Document: " + _

  override protected def preparer: Preparer[AmqpProtocolMessage, Any] =
    message =>
      safely(ErrorMapper) {
        message match {
          case AmqpProtocolMessage(_, payload, _) =>
            val in = new ByteArrayInputStream(payload)
            Some(XmlParsers.parse(in, StandardCharsets.UTF_8)).success
          case _ => "Unsupported message type".failure
        }
      }
}
