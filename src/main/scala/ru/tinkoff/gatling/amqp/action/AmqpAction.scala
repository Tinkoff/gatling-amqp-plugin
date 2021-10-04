package ru.tinkoff.gatling.amqp.action

import io.gatling.commons.stats.Status
import io.gatling.commons.validation.{Validation, _}
import io.gatling.core.CoreComponents
import io.gatling.core.action.{Action, RequestAction}
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.util.NameGen
import ru.tinkoff.gatling.amqp.protocol.AmqpComponents
import ru.tinkoff.gatling.amqp.request._

abstract class AmqpAction(
    attributes: AmqpAttributes,
    components: AmqpComponents,
    throttler: Option[Throttler],
    coreComponents: CoreComponents
) extends RequestAction with AmqpLogging with NameGen {
  override val requestName: Expression[String] = attributes.requestName

  protected def resolveDestination(destination: AmqpExchange, session: Session): Validation[(String, String)] = {
    destination match {
      case AmqpDirectExchange(name, routingKey, _) =>
        for {
          exName <- name(session)
          exKey  <- routingKey(session)
        } yield (exName, exKey)

      case AmqpQueueExchange(name, _) =>
        name(session).map(qName => ("", qName))

      case AmqpTopicExchange(name, routingKey, _) =>
        for {
          exName <- name(session)
          exKey  <- routingKey(session)
        } yield (exName, exKey)
    }
  }

  protected def executeNext(
      session: Session,
      sent: Long,
      received: Long,
      status: Status,
      next: Action,
      requestName: String,
      responseCode: Option[String],
      message: Option[String]
  ): Unit = {
    coreComponents.statsEngine.logResponse(session.scenario,
                                           session.groups,
                                           requestName,
                                           sent,
                                           received,
                                           status,
                                           responseCode,
                                           message)
    next ! session.logGroupRequestTimings(sent, received)
  }

  override def sendRequest(requestName: String, session: Session): Validation[Unit] =
    for {
      props             <- AmqpMessageProperties.toBasicProperties(attributes.messageProperties, session)
      propsWithDelivery <- props.builder().deliveryMode(components.protocol.deliveryMode.mode).build().success
      message <- attributes.message
                  .amqpProtocolMessage(session)
                  .map(_.copy(amqpProperties = propsWithDelivery))
                  .map(components.protocol.messageMatcher.prepareRequest)
    } yield
      throttler
        .fold(publishAndLogMessage(requestName, message, session))(
          _.throttle(session.scenario, () => publishAndLogMessage(requestName, message, session))
        )

  protected def publishAndLogMessage(requestNameString: String, msg: AmqpProtocolMessage, session: Session): Unit

}
