package ru.tinkoff.gatling.amqp.request

import com.softwaremill.quicklens._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.{Expression, _}

import scala.collection.JavaConverters._

case class PublishDslBuilder(attributes: AmqpAttributes, factory: AmqpAttributes => ActionBuilder) {
  def property(key: Expression[String], value: Expression[Any]): PublishDslBuilder =
    this.modify(_.attributes.messageProperties).using(_ + (key -> value))

  def messageId(id: Expression[String]): PublishDslBuilder =
    this.modify(_.attributes.messageProperties).using(_ + ("messageId".expressionSuccess -> id))

  def priority(msgPriority: Expression[Int]): PublishDslBuilder =
    this.modify(_.attributes.messageProperties).using(_ + ("priority".expressionSuccess -> msgPriority))

  def headers(hdrs: Expression[Map[String, String]]): PublishDslBuilder =
    this.modify(_.attributes.messageProperties).using(_ + ("headers".expressionSuccess -> hdrs.map(_.asJava)))

  def build(): ActionBuilder = factory(attributes)
}
