package ru.tinkoff.gatling.amqp.checks

import com.fasterxml.jackson.databind.JsonNode
import io.gatling.commons.validation.SuccessWrapper
import io.gatling.core.check.bytes.BodyBytesCheckType
import io.gatling.core.check.jmespath.JmesPathCheckType
import io.gatling.core.check.jsonpath.JsonPathCheckType
import io.gatling.core.check.string.BodyStringCheckType
import io.gatling.core.check.substring.SubstringCheckType
import io.gatling.core.check.xpath.XPathCheckType
import io.gatling.core.check.{CheckMaterializer, Preparer}
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.json.JsonParsers
import net.sf.saxon.s9api.XdmNode
import ru.tinkoff.gatling.amqp.AmqpCheck
import ru.tinkoff.gatling.amqp.checks.AmqpResponseCodeCheckBuilder.AmqpMessageCheckType
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

final class AmqpCheckMaterializer[T, P] private[AmqpCheckMaterializer] (
    override val preparer: Preparer[AmqpProtocolMessage, P]
) extends CheckMaterializer[T, AmqpCheck, AmqpProtocolMessage, P](identity)

object AmqpCheckMaterializer {
  def xpath(configuration: GatlingConfiguration): AmqpCheckMaterializer[XPathCheckType, XdmNode] =
    new AmqpCheckMaterializer(AmqpMessagePreparer.xmlPreparer(configuration))

  val bodyBytes: AmqpCheckMaterializer[BodyBytesCheckType, Array[Byte]] =
    new AmqpCheckMaterializer(AmqpMessagePreparer.bytesBodyPreparer)

  def bodyString(configuration: GatlingConfiguration): AmqpCheckMaterializer[BodyStringCheckType, String] =
    new AmqpCheckMaterializer(AmqpMessagePreparer.stringBodyPreparer(configuration))

  def substring(configuration: GatlingConfiguration): AmqpCheckMaterializer[SubstringCheckType, String] =
    new AmqpCheckMaterializer(AmqpMessagePreparer.stringBodyPreparer(configuration))

  def jsonPath(
      jsonParsers: JsonParsers,
      configuration: GatlingConfiguration
  ): AmqpCheckMaterializer[JsonPathCheckType, JsonNode] =
    new AmqpCheckMaterializer(AmqpMessagePreparer.jsonPathPreparer(jsonParsers, configuration))

  def jmesPath(
      jsonParsers: JsonParsers,
      configuration: GatlingConfiguration
  ): AmqpCheckMaterializer[JmesPathCheckType, JsonNode] =
    new AmqpCheckMaterializer(AmqpMessagePreparer.jsonPathPreparer(jsonParsers, configuration))

  val amqpStatusCheck: AmqpCheckMaterializer[AmqpMessageCheckType, AmqpProtocolMessage] =
    new AmqpCheckMaterializer(_.success)

}
