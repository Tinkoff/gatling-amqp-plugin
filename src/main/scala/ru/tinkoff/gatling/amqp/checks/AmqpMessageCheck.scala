package ru.tinkoff.gatling.amqp.checks

import io.gatling.commons.validation._
import io.gatling.core.check.CheckResult
import io.gatling.core.session.{Expression, Session}
import ru.tinkoff.gatling.amqp.AmqpCheck
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

import java.util.{Map => JMap}

case class AmqpMessageCheck(wrapped: AmqpCheck) extends AmqpCheck {

  override def check(response: AmqpProtocolMessage, session: Session, preparedCache: JMap[Any, Any]): Validation[CheckResult] =
    wrapped.check(response, session, preparedCache)

  override def checkIf(condition: Expression[Boolean]): AmqpMessageCheck = copy(wrapped.checkIf(condition))

  override def checkIf(condition: (AmqpProtocolMessage, Session) => Validation[Boolean]): AmqpMessageCheck = copy(
    wrapped.checkIf(condition),
  )
}
