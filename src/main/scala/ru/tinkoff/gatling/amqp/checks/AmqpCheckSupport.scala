package ru.tinkoff.gatling.amqp.checks

import java.nio.charset.Charset

import io.gatling.commons.validation._
import io.gatling.core.check._
import io.gatling.core.check.bytes.BodyBytesCheckType
import io.gatling.core.check.string.BodyStringCheckType
import io.gatling.core.check.xpath.XmlParsers
import io.gatling.core.json.JsonParsers
import ru.tinkoff.gatling.amqp.AmqpCheck
import ru.tinkoff.gatling.amqp.checks.AmqpResponseCodeCheckBuilder.{AmqpMessageCheckType, ExtendedDefaultFindCheckBuilder, _}
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

import scala.annotation.implicitNotFound
import scala.util.Try

trait AmqpCheckSupport {
  def messageCheck: AmqpMessageCheck.type                                                              = AmqpMessageCheck
  val responseCode: ExtendedDefaultFindCheckBuilder[AmqpMessageCheckType, AmqpProtocolMessage, String] = ResponseCode

  @implicitNotFound("Could not find a CheckMaterializer. This check might not be valid for AMQP.")
  implicit def checkBuilder2AmqpCheck[A, P, X](checkBuilder: CheckBuilder[A, P, X])(
      implicit materializer: CheckMaterializer[A, AmqpCheck, AmqpProtocolMessage, P]): AmqpCheck =
    checkBuilder.build(materializer)

  @implicitNotFound("Could not find a CheckMaterializer. This check might not be valid for AMQP.")
  implicit def validatorCheckBuilder2AmqpCheck[A, P, X](validatorCheckBuilder: ValidatorCheckBuilder[A, P, X])(
      implicit materializer: CheckMaterializer[A, AmqpCheck, AmqpProtocolMessage, P]): AmqpCheck =
    validatorCheckBuilder.exists

  @implicitNotFound("Could not find a CheckMaterializer. This check might not be valid for AMQP.")
  implicit def findCheckBuilder2AmqpCheck[A, P, X](findCheckBuilder: FindCheckBuilder[A, P, X])(
      implicit materializer: CheckMaterializer[A, AmqpCheck, AmqpProtocolMessage, P]): AmqpCheck =
    findCheckBuilder.find.exists

  implicit def amqpXPathMaterializer(implicit xmlParsers: XmlParsers): AmqpXPathCheckMaterializer =
    new AmqpXPathCheckMaterializer(xmlParsers)

  implicit def amqpJsonPathMaterializer(implicit jsonParsers: JsonParsers): AmqpJsonPathCheckMaterializer =
    new AmqpJsonPathCheckMaterializer(jsonParsers)

  implicit def amqpBodyStringMaterializer: AmqpCheckMaterializer[BodyStringCheckType, String] =
    new CheckMaterializer[BodyStringCheckType, AmqpCheck, AmqpProtocolMessage, String](identity) {
      override protected def preparer: Preparer[AmqpProtocolMessage, String] = replyMessage => {
        val bodyCharset = Try(Charset.forName(replyMessage.amqpProperties.getContentEncoding))
          .getOrElse(Charset.defaultCharset())
        if (replyMessage.payload.length > 0) {
          new String(replyMessage.payload, bodyCharset).success
        } else "".success
      }
    }

  implicit def amqpBodyByteMaterializer: AmqpCheckMaterializer[BodyBytesCheckType, Array[Byte]] =
    new CheckMaterializer[BodyBytesCheckType, AmqpCheck, AmqpProtocolMessage, Array[Byte]](identity) {
      override protected def preparer: Preparer[AmqpProtocolMessage, Array[Byte]] = replyMessage => {
        if (replyMessage.payload.length > 0) {
          replyMessage.payload.success
        } else Array.emptyByteArray.success
      }
    }

  implicit val httpStatusCheckMaterializer: AmqpCheckMaterializer[AmqpMessageCheckType, AmqpProtocolMessage] =
    new AmqpCheckMaterializer[AmqpMessageCheckType, AmqpProtocolMessage](identity) {
      override val preparer: Preparer[AmqpProtocolMessage, AmqpProtocolMessage] = _.success
    }

}
