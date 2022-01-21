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
    throttler: Option[Throttler],
) extends RequestAction with AmqpLogging with NameGen {

  override val requestName: Expression[String] = attributes.requestName

  private val publisher = new AmqpPublisher(attributes.destination, components)

  override def sendRequest(session: Session): Validation[Unit] =
    for {
      reqName           <- requestName(session)
      props             <- AmqpMessageProperties.toBasicProperties(attributes.messageProperties, session)
      propsWithDelivery <- props.builder().deliveryMode(components.protocol.deliveryMode.mode).build().success
      message           <- attributes.message
                             .amqpProtocolMessage(session)
                             .map(_.copy(amqpProperties = propsWithDelivery))
                             .map(components.protocol.messageMatcher.prepareRequest)
    } yield throttler
      .fold(publishAndLogMessage(reqName, message, session))(
        _.throttle(session.scenario, () => publishAndLogMessage(reqName, message, session)),
      )

  protected def publishAndLogMessage(
      requestNameString: String,
      msg: AmqpProtocolMessage,
      session: Session,
      publisher: AmqpPublisher = publisher,
  ): Unit

}
