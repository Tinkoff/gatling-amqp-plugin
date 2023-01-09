package ru.tinkoff.gatling.amqp.javaapi.check

import com.fasterxml.jackson.databind.JsonNode
import io.gatling.core.check._
import io.gatling.core.check.bytes.BodyBytesCheckType
import io.gatling.core.check.jmespath.JmesPathCheckType
import io.gatling.core.check.jsonpath.JsonPathCheckType
import io.gatling.core.check.string.BodyStringCheckType
import io.gatling.core.check.substring.SubstringCheckType
import io.gatling.core.check.xpath.XPathCheckType
import io.gatling.javaapi.core.internal.CoreCheckType
import net.sf.saxon.s9api.XdmNode
import ru.tinkoff.gatling.amqp.AmqpCheck
import ru.tinkoff.gatling.amqp.checks._
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

import java.{util => ju}
import scala.jdk.CollectionConverters._

object AmqpChecks {
  case class AmqpCheckTypeWrapper(value: Check[AmqpProtocolMessage])
  class SimpleChecksScala extends AmqpCheckSupport {}
  private def toScalaCheck(javaCheck: Object): AmqpCheck = {
    javaCheck match {
      case _: io.gatling.javaapi.core.CheckBuilder =>
        val checkBuilder = javaCheck.asInstanceOf[io.gatling.javaapi.core.CheckBuilder]
        val scalaCheck   = checkBuilder.asScala
        checkBuilder.`type` match {
          case CoreCheckType.BodyBytes    =>
            scalaCheck.asInstanceOf[CheckBuilder[BodyBytesCheckType, Array[Byte]]].build(AmqpCheckMaterializer.bodyBytes)
          case CoreCheckType.BodyString   =>
            scalaCheck
              .asInstanceOf[CheckBuilder[BodyStringCheckType, String]]
              .build(AmqpCheckMaterializer.bodyString(io.gatling.core.Predef.configuration))
          case CoreCheckType.Substring    =>
            scalaCheck
              .asInstanceOf[CheckBuilder[SubstringCheckType, String]]
              .build(AmqpCheckMaterializer.substring(io.gatling.core.Predef.configuration))
          case CoreCheckType.XPath        =>
            scalaCheck
              .asInstanceOf[CheckBuilder[XPathCheckType, XdmNode]]
              .build(AmqpCheckMaterializer.xpath(io.gatling.core.Predef.configuration))
          case CoreCheckType.JsonPath     =>
            scalaCheck
              .asInstanceOf[CheckBuilder[JsonPathCheckType, JsonNode]]
              .build(
                AmqpCheckMaterializer.jsonPath(io.gatling.core.Predef.defaultJsonParsers, io.gatling.core.Predef.configuration),
              )
          case CoreCheckType.JmesPath     =>
            scalaCheck
              .asInstanceOf[CheckBuilder[JmesPathCheckType, JsonNode]]
              .build(
                AmqpCheckMaterializer.jmesPath(io.gatling.core.Predef.defaultJsonParsers, io.gatling.core.Predef.configuration),
              )
          case AmqpCheckType.ResponseCode =>
            scalaCheck
              .asInstanceOf[CheckBuilder[AmqpResponseCodeCheckBuilder.AmqpMessageCheckType, AmqpProtocolMessage]]
              .build(
                AmqpCheckMaterializer.amqpStatusCheck,
              )
          case unknown                    => throw new IllegalArgumentException(s"AMQP DSL doesn't support $unknown")
        }
      case amqpCheck: AmqpCheckTypeWrapper         => amqpCheck.value
      case unknown                                 => throw new IllegalArgumentException(s"AMQP DSL doesn't support $unknown")
    }
  }

  def toScalaChecks(javaChecks: ju.List[Object]): Seq[AmqpCheck] =
    javaChecks.asScala.map(toScalaCheck).toSeq

  val responseCode: ExtendedCheckBuilder =
    new ExtendedCheckBuilder(AmqpResponseCodeCheckBuilder.ResponseCode)
}
