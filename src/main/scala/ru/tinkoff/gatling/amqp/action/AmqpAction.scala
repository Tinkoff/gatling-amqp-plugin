package ru.tinkoff.gatling.amqp.action

import io.gatling.commons.validation.{Validation, _}
import io.gatling.core.action.RequestAction
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.util.NameGen
import ru.tinkoff.gatling.amqp.client.AmqpPublisher
import ru.tinkoff.gatling.amqp.protocol.AmqpComponents
import ru.tinkoff.gatling.amqp.request.{AmqpAttributes, AmqpMessageProperties, AmqpProtocolMessage}

abstract class AmqpAction(
    attributes: AmqpAttributes,
    components: AmqpComponents,
    throttler: Option[Throttler]
) extends RequestAction with AmqpLogging with NameGen {
  override val requestName: Expression[String] = attributes.requestName

  private val publisher = new AmqpPublisher(attributes.destination, components)

  override def sendRequest(requestName: String, session: Session): Validation[Unit] =
    for {
      props             <- AmqpMessageProperties.toBasicProperties(attributes.messageProperties, session)
      propsWithDelivery <- props.builder().deliveryMode(components.protocol.deliveryMode.mode).build().success
      message           <- attributes.message
                             .amqpProtocolMessage(session)
                             .map(_.copy(amqpProperties = propsWithDelivery))
                             .map(components.protocol.messageMatcher.prepareRequest)
      around            <- aroundPublish(requestName, session, message)
    } yield throttler
      .fold(publisher.publish(message, around, session))(
        _.throttle(session.scenario, () => publisher.publish(message, around, session))
      )

  protected def aroundPublish(requestName: String, session: Session, message: AmqpProtocolMessage): Validation[Around]

}
