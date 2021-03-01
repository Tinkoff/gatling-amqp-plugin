package ru.tinkoff.gatling.amqp.request

import com.softwaremill.quicklens._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression
import ru.tinkoff.gatling.amqp.AmqpCheck

import java.util.Date

case class RequestReplyDslBuilder(attributes: AmqpAttributes, factory: AmqpAttributes => ActionBuilder) {
  def messageId(value: Expression[String]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties.messageId).setTo(Some(value))

  def priority(value: Expression[Int]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties.priority).setTo(Some(value))

  def contentType(value: Expression[String]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties.contentType).setTo(Some(value))

  def contentEncoding(value: Expression[String]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties.contentEncoding).setTo(Some(value))

  def correlationId(value: Expression[String]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties.correlationId).setTo(Some(value))

  def replyTo(value: Expression[String]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties.replyTo).setTo(Some(value))

  def expiration(value: Expression[String]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties.expiration).setTo(Some(value))

  def timestamp(value: Expression[Date]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties.timestamp).setTo(Some(value))

  def amqpType(value: Expression[String]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties.`type`).setTo(Some(value))

  def userId(value: Expression[String]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties.userId).setTo(Some(value))

  def appId(value: Expression[String]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties.appId).setTo(Some(value))

  def clusterId(value: Expression[String]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties.clusterId).setTo(Some(value))

  def header(key: String, value: Expression[String]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties.headers).using(_ + (key -> value))

  def headers(hs: (String, Expression[String])*): RequestReplyDslBuilder     =
    hs.foldLeft(this) { case (rb, (k, v)) => rb.header(k, v) }

  def check(checks: AmqpCheck*): RequestReplyDslBuilder                      =
    this.modify(_.attributes.checks).using(_ ::: checks.toList)

  def build(): ActionBuilder = factory(attributes)
}
