package ru.tinkoff.gatling.amqp.checks

import java.io.{ByteArrayInputStream, InputStreamReader}

import io.gatling.commons.validation.{safely, _}
import io.gatling.core.check.xpath.{Dom, XPathCheckType, XmlParsers}
import io.gatling.core.check.{CheckMaterializer, Preparer}
import org.xml.sax.InputSource
import ru.tinkoff.gatling.amqp.AmqpCheck
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

class AmqpXPathCheckMaterializer(xmlParsers: XmlParsers)
    extends CheckMaterializer[XPathCheckType, AmqpCheck, AmqpProtocolMessage, Option[Dom]](identity) {
  private val ErrorMapper = "Could not parse response into a DOM Document: " + _

  override protected def preparer: Preparer[AmqpProtocolMessage, Option[Dom]] =
    message =>
      safely(ErrorMapper) {
        message match {
          case AmqpProtocolMessage(_, payload, _) =>
            val in = new ByteArrayInputStream(payload)
            Some(xmlParsers.parse(new InputSource(new InputStreamReader(in)))).success
          case _ => "Unsupported message type".failure
        }
      }
}
