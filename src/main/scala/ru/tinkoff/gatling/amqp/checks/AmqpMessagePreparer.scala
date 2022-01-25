package ru.tinkoff.gatling.amqp.checks

import java.io.ByteArrayInputStream
import java.nio.charset.Charset

import com.fasterxml.jackson.databind.JsonNode
import io.gatling.commons.validation._
import io.gatling.core.check.Preparer
import io.gatling.core.check.xpath.XmlParsers
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.json.JsonParsers
import net.sf.saxon.s9api.XdmNode
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

import scala.util.Try

trait AmqpMessagePreparer[P] extends Preparer[AmqpProtocolMessage, P]

object AmqpMessagePreparer {

  private def messageCharset(cfg: GatlingConfiguration, msg: AmqpProtocolMessage): Validation[Charset] =
    Try(Charset.forName(msg.amqpProperties.getContentEncoding))
      .orElse(Try(cfg.core.charset))
      .toValidation

  def stringBodyPreparer(configuration: GatlingConfiguration): AmqpMessagePreparer[String] =
    msg =>
      messageCharset(configuration, msg)
        .map(cs => if (msg.payload.length > 0) new String(msg.payload, cs) else "")

  val bytesBodyPreparer: AmqpMessagePreparer[Array[Byte]] = msg =>
    (if (msg.payload.length > 0) msg.payload else Array.emptyByteArray).success

  private val CharsParsingThreshold = 200 * 1000

  def jsonPathPreparer(
      jsonParsers: JsonParsers,
      configuration: GatlingConfiguration,
  ): Preparer[AmqpProtocolMessage, JsonNode] =
    msg =>
      messageCharset(configuration, msg)
        .flatMap(bodyCharset =>
          if (msg.payload.length > CharsParsingThreshold)
            jsonParsers.safeParse(new ByteArrayInputStream(msg.payload), bodyCharset)
          else
            jsonParsers.safeParse(new String(msg.payload, bodyCharset)),
        )

  private val ErrorMapper = "Could not parse response into a DOM Document: " + _

  def xmlPreparer(configuration: GatlingConfiguration): AmqpMessagePreparer[XdmNode] =
    msg =>
      safely(ErrorMapper) {
        messageCharset(configuration, msg).map(cs => XmlParsers.parse(new ByteArrayInputStream(msg.payload), cs))
      }

}
