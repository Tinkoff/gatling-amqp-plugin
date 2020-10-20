package ru.tinkoff.gatling.amqp.request

import java.util.Date

import com.rabbitmq.client.AMQP
import io.gatling.commons.validation._
import io.gatling.core.session.{Expression, Session}

import scala.collection.JavaConverters._

case class AmqpMessageProperties(
    contentType: Option[Expression[String]] = None,
    contentEncoding: Option[Expression[String]] = None,
    headers: Map[String, Expression[AnyRef]] = Map.empty,
    deliveryMode: Option[Expression[Int]] = None,
    priority: Option[Expression[Int]] = None,
    correlationId: Option[Expression[String]] = None,
    replyTo: Option[Expression[String]] = None,
    expiration: Option[Expression[String]] = None,
    messageId: Option[Expression[String]] = None,
    timestamp: Option[Expression[Date]] = None,
    `type`: Option[Expression[String]] = None,
    userId: Option[Expression[String]] = None,
    appId: Option[Expression[String]] = None,
    clusterId: Option[Expression[String]] = None,
)

object AmqpMessageProperties {

  private implicit class OptExpressionUtil[T](val optExp: Option[Expression[T]]) extends AnyVal {
    def apply(session: Session,
              p: AMQP.BasicProperties.Builder,
              setProperty: T => AMQP.BasicProperties.Builder): Validation[AMQP.BasicProperties.Builder] =
      optExp.fold(p.success)(_(session).map(setProperty))
  }

  def toBasicProperties(p: AmqpMessageProperties, s: Session): Validation[AMQP.BasicProperties] = {
    val bp = new AMQP.BasicProperties().builder()
    import p._
    contentType(s, bp, bp.contentType)
    contentEncoding(s, bp, bp.contentEncoding)
    deliveryMode(s, bp, i => bp.deliveryMode(i))
    priority(s, bp, i => bp.deliveryMode(i))
    correlationId(s, bp, bp.correlationId)
    replyTo(s, bp, bp.replyTo)
    expiration(s, bp, bp.expiration)
    messageId(s, bp, bp.messageId)
    timestamp(s, bp, bp.timestamp)
    `type`(s, bp, bp.`type`)
    userId(s, bp, bp.userId)
    appId(s, bp, bp.appId)
    clusterId(s, bp, bp.clusterId)
      .flatMap(
        b =>
          headers
            .foldLeft(Map.empty[String, AnyRef].success) {
              case (resolvedHeaders, (key, value)) =>
                for {
                  v  <- value(s)
                  rh <- resolvedHeaders
                } yield rh + (key -> v)
            }
            .map(h => b.headers(h.asJava)))
      .map(_.build())

  }
}
