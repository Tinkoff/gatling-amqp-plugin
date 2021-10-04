package ru.tinkoff.gatling.amqp.checks

import com.fasterxml.jackson.databind.JsonNode
import io.gatling.commons.validation._
import io.gatling.core.Predef.Session
import io.gatling.core.check._
import io.gatling.core.check.bytes.BodyBytesCheckType
import io.gatling.core.check.jmespath.JmesPathCheckType
import io.gatling.core.check.jsonpath.JsonPathCheckType
import io.gatling.core.check.string.BodyStringCheckType
import io.gatling.core.check.substring.SubstringCheckType
import io.gatling.core.check.xpath.XPathCheckType
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.json.JsonParsers
import io.gatling.core.session.Expression
import net.sf.saxon.s9api.XdmNode
import ru.tinkoff.gatling.amqp.AmqpCheck
import ru.tinkoff.gatling.amqp.checks.AmqpResponseCodeCheckBuilder.{
  AmqpMessageCheckType,
  ExtendedDefaultFindCheckBuilder,
  ResponseCode
}
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

import java.util.{Map => JMap}
import scala.annotation.implicitNotFound

trait AmqpCheckSupport {
  def messageCheck: AmqpMessageCheck.type                                                              = AmqpMessageCheck
  val responseCode: ExtendedDefaultFindCheckBuilder[AmqpMessageCheckType, AmqpProtocolMessage, String] = ResponseCode

  @implicitNotFound("Could not find a CheckMaterializer. This check might not be valid for AMQP.")
  implicit def checkBuilder2AmqpCheck[A, P, X](
      checkBuilder: CheckBuilder[A, P, X]
  )(implicit materializer: CheckMaterializer[A, AmqpCheck, AmqpProtocolMessage, P]): AmqpCheck =
    checkBuilder.build(materializer)

  @implicitNotFound("Could not find a CheckMaterializer. This check might not be valid for AMQP.")
  implicit def validatorCheckBuilder2AmqpCheck[A, P, X](
      validatorCheckBuilder: ValidatorCheckBuilder[A, P, X]
  )(implicit materializer: CheckMaterializer[A, AmqpCheck, AmqpProtocolMessage, P]): AmqpCheck =
    validatorCheckBuilder.exists

  @implicitNotFound("Could not find a CheckMaterializer. This check might not be valid for AMQP.")
  implicit def findCheckBuilder2AmqpCheck[A, P, X](
      findCheckBuilder: FindCheckBuilder[A, P, X]
  )(implicit materializer: CheckMaterializer[A, AmqpCheck, AmqpProtocolMessage, P]): AmqpCheck =
    findCheckBuilder.find.exists

  implicit def amqpXPathMaterializer(
      implicit
      configuration: GatlingConfiguration): AmqpCheckMaterializer[XPathCheckType, XdmNode] =
    AmqpCheckMaterializer.xpath(configuration)

  implicit def amqpJsonPathMaterializer(
      implicit
      jsonParsers: JsonParsers,
      configuration: GatlingConfiguration): AmqpCheckMaterializer[JsonPathCheckType, JsonNode] =
    AmqpCheckMaterializer.jsonPath(jsonParsers, configuration)

  implicit def amqpJmesPathMaterializer(
      implicit
      jsonParsers: JsonParsers,
      configuration: GatlingConfiguration): AmqpCheckMaterializer[JmesPathCheckType, JsonNode] =
    AmqpCheckMaterializer.jmesPath(jsonParsers, configuration)

  implicit def amqpBodyStringMaterializer(
      implicit
      configuration: GatlingConfiguration): AmqpCheckMaterializer[BodyStringCheckType, String] =
    AmqpCheckMaterializer.bodyString(configuration)

  implicit def amqpSubstringMaterializer(
      implicit
      configuration: GatlingConfiguration): AmqpCheckMaterializer[SubstringCheckType, String] =
    AmqpCheckMaterializer.substring(configuration)

  implicit def amqpBodyByteMaterializer: AmqpCheckMaterializer[BodyBytesCheckType, Array[Byte]] =
    AmqpCheckMaterializer.bodyBytes

  implicit val amqpStatusCheckMaterializer: AmqpCheckMaterializer[AmqpMessageCheckType, AmqpProtocolMessage] =
    AmqpCheckMaterializer.amqpStatusCheck

  implicit val amqpUntypedConditionalCheckWrapper: UntypedConditionalCheckWrapper[AmqpCheck] =
    (condition: Expression[Boolean], thenCheck: AmqpCheck) =>
      new Check[AmqpProtocolMessage] {
        private val typedCondition = (_: AmqpProtocolMessage, ses: Session) => condition(ses)

        override def check(
            response: AmqpProtocolMessage,
            session: Session,
            preparedCache: JMap[Any, Any]
        ): Validation[CheckResult] =
          ConditionalCheck(typedCondition, thenCheck).check(response, session, preparedCache)
    }

  implicit val amqpTypedConditionalCheckWrapper: TypedConditionalCheckWrapper[AmqpProtocolMessage, AmqpCheck] =
    (condition: (AmqpProtocolMessage, Session) => Validation[Boolean], thenCheck: AmqpCheck) =>
      (response: AmqpProtocolMessage, session: Session, preparedCache: JMap[Any, Any]) =>
        ConditionalCheck(condition, thenCheck).check(response, session, preparedCache)
}
