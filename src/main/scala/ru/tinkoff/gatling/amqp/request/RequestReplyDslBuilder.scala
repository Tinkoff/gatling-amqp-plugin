package ru.tinkoff.gatling.amqp.request

import com.softwaremill.quicklens._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.{Expression, _}
import ru.tinkoff.gatling.amqp.AmqpCheck

import scala.collection.JavaConverters._

case class RequestReplyDslBuilder(attributes: AmqpAttributes, factory: AmqpAttributes => ActionBuilder) {

  private implicit def toExpression[T]: ((String, Expression[T])) => (Expression[String], Expression[T]) = {
    case (first, second) => (first.expressionSuccess, second)
  }

  def property(key: Expression[String], value: Expression[Any]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties).using(_ + (key -> value))

  def messageId(id: Expression[String]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties).using(_ + ("messageId" -> id))

  def priority(msgPriority: Expression[Int]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties).using(_ + ("priority" -> msgPriority))

  def headers(hdrs: Expression[Map[String, String]]): RequestReplyDslBuilder =
    this.modify(_.attributes.messageProperties).using(_ + ("headers" -> hdrs.map(_.asJava)))

  def check(checks: AmqpCheck*): RequestReplyDslBuilder = this.modify(_.attributes.checks).using(_ ::: checks.toList)

  def build(): ActionBuilder = factory(attributes)
}
