package ru.tinkoff.gatling.amqp.checks

import com.fasterxml.jackson.databind.JsonNode
import io.gatling.commons.validation._
import io.gatling.core.check.Check.PreparedCache
import io.gatling.core.check.{CheckResult, _}
import io.gatling.core.check.bytes.BodyBytesCheckType
import io.gatling.core.check.jmespath.JmesPathCheckType
import io.gatling.core.check.jsonpath.JsonPathCheckType
import io.gatling.core.check.string.BodyStringCheckType
import io.gatling.core.check.substring.SubstringCheckType
import io.gatling.core.check.xpath.XPathCheckType
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.json.JsonParsers
import io.gatling.core.session.Session
import net.sf.saxon.s9api.XdmNode
import ru.tinkoff.gatling.amqp.AmqpCheck
import ru.tinkoff.gatling.amqp.checks.AmqpResponseCodeCheckBuilder.{
  AmqpMessageCheckType,
  ExtendedDefaultFindCheckBuilder,
  ResponseCode,
}
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

import scala.annotation.implicitNotFound

trait AmqpCheckSupport {
  def messageCheck: AmqpMessageCheck.type                                                              = AmqpMessageCheck
  val responseCode: ExtendedDefaultFindCheckBuilder[AmqpMessageCheckType, AmqpProtocolMessage, String] = ResponseCode

  def simpleCheck(f: AmqpProtocolMessage => Boolean): AmqpCheck =
    Check.Simple(
      (response: AmqpProtocolMessage, _: Session, _: PreparedCache) =>
        if (f(response)) {
          CheckResult.NoopCheckResultSuccess
        } else {
          "AMQP check failed".failure
        },
      None,
    )

  @implicitNotFound("Could not find a CheckMaterializer. This check might not be valid for AMQP.")
  implicit def checkBuilder2AmqpCheck[T, P](
      checkBuilder: CheckBuilder[T, P],
  )(implicit materializer: CheckMaterializer[T, AmqpCheck, AmqpProtocolMessage, P]): AmqpCheck =
    checkBuilder.build(materializer)

  @implicitNotFound("Could not find a CheckMaterializer. This check might not be valid for AMQP.")
  implicit def validatorCheckBuilder2AmqpCheck[T, P, X](
      validate: CheckBuilder.Validate[T, P, X],
  )(implicit materializer: CheckMaterializer[T, AmqpCheck, AmqpProtocolMessage, P]): AmqpCheck =
    validate.exists

  @implicitNotFound("Could not find a CheckMaterializer. This check might not be valid for AMQP.")
  implicit def findCheckBuilder2AmqpCheck[T, P, X](
      find: CheckBuilder.Find[T, P, X],
  )(implicit materializer: CheckMaterializer[T, AmqpCheck, AmqpProtocolMessage, P]): AmqpCheck =
    find.find.exists

  implicit def amqpXPathMaterializer(implicit
      configuration: GatlingConfiguration,
  ): AmqpCheckMaterializer[XPathCheckType, XdmNode] =
    AmqpCheckMaterializer.xpath(configuration)

  implicit def amqpJsonPathMaterializer(implicit
      jsonParsers: JsonParsers,
      configuration: GatlingConfiguration,
  ): AmqpCheckMaterializer[JsonPathCheckType, JsonNode] =
    AmqpCheckMaterializer.jsonPath(jsonParsers, configuration)

  implicit def amqpJmesPathMaterializer(implicit
      jsonParsers: JsonParsers,
      configuration: GatlingConfiguration,
  ): AmqpCheckMaterializer[JmesPathCheckType, JsonNode] =
    AmqpCheckMaterializer.jmesPath(jsonParsers, configuration)

  implicit def amqpBodyStringMaterializer(implicit
      configuration: GatlingConfiguration,
  ): AmqpCheckMaterializer[BodyStringCheckType, String] =
    AmqpCheckMaterializer.bodyString(configuration)

  implicit def amqpSubstringMaterializer(implicit
      configuration: GatlingConfiguration,
  ): AmqpCheckMaterializer[SubstringCheckType, String] =
    AmqpCheckMaterializer.substring(configuration)

  implicit def amqpBodyByteMaterializer: AmqpCheckMaterializer[BodyBytesCheckType, Array[Byte]] =
    AmqpCheckMaterializer.bodyBytes

  implicit val amqpStatusCheckMaterializer: AmqpCheckMaterializer[AmqpMessageCheckType, AmqpProtocolMessage] =
    AmqpCheckMaterializer.amqpStatusCheck

  implicit val amqpUntypedConditionalCheckWrapper: UntypedCheckIfMaker[AmqpCheck] = _.checkIf(_)

  implicit val amqpTypedConditionalCheckWrapper: TypedCheckIfMaker[AmqpProtocolMessage, AmqpCheck] = _.checkIf(_)
}
