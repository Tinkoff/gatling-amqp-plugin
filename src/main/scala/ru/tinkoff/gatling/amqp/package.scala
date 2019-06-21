package ru.tinkoff.gatling

import io.gatling.core.check.Check
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

package object amqp {
  type AmqpCheck = Check[AmqpProtocolMessage]
}
