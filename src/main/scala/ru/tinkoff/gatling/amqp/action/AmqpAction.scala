package ru.tinkoff.gatling.amqp.action

import io.gatling.commons.validation.{Validation, _}
import io.gatling.core.action.RequestAction
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.util.NameGen
import ru.tinkoff.gatling.amqp.client.AmqpPublisher
import ru.tinkoff.gatling.amqp.protocol.AmqpComponents
import ru.tinkoff.gatling.amqp.request.{AmqpAttributes, AmqpProtocolMessage}

abstract class AmqpAction(
    attributes: AmqpAttributes,
    components: AmqpComponents,
    throttler: Throttler,
    throttled: Boolean
) extends RequestAction with AmqpLogging with NameGen {
  override val requestName: Expression[String] = attributes.requestName

  private val publisher = new AmqpPublisher(attributes.destination, components)

  override def sendRequest(requestName: String, session: Session): Validation[Unit] = {
    for {
      props <- resolveProperties(attributes.messageProperties, session)
      message <- attributes.message
                  .amqpProtocolMessage(session)
                  .map(_.mergeProperties(props + ("deliveryMode" -> components.protocol.deliveryMode)))
      around <- aroundPublish(requestName, session, message)
    } yield {

      if (throttled) {
        throttler.throttle(
          session.scenario,
          () => publisher.publish(message, around, session)
        )
      } else publisher.publish(message, around, session)

    }

  }

  private def resolveProperties(
      properties: Map[Expression[String], Expression[Any]],
      session: Session
  ): Validation[Map[String, Any]] =
    properties.foldLeft(Map.empty[String, Any].success) {
      case (resolvedProperties, (key, value)) =>
        for {
          key                <- key(session)
          value              <- value(session)
          resolvedProperties <- resolvedProperties
        } yield resolvedProperties + (key -> value)
    }

  protected def aroundPublish(requestName: String, session: Session, message: AmqpProtocolMessage): Validation[Around]

}
