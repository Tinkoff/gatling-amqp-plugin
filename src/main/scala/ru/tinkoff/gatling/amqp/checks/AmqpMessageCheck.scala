package ru.tinkoff.gatling.amqp.checks

import io.gatling.commons.validation._
import io.gatling.core.check.CheckResult
import io.gatling.core.session.Session
import ru.tinkoff.gatling.amqp.AmqpCheck
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

import java.util.{Map => JMap}

object AmqpMessageCheck {

  private val AmqpMessageCheckFailure = "AMQP check failed".failure
}

case class AmqpMessageCheck(func: AmqpProtocolMessage => Boolean) extends AmqpCheck {
  override def check(
      response: AmqpProtocolMessage,
      session: Session,
      preparedCache: JMap[Any, Any]
  ): Validation[CheckResult] =
    if (func(response)) {
      CheckResult.NoopCheckResultSuccess
    } else {
      AmqpMessageCheck.AmqpMessageCheckFailure
    }
}
