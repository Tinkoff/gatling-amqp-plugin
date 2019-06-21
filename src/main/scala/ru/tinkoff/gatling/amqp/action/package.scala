package ru.tinkoff.gatling.amqp

import com.typesafe.scalalogging.StrictLogging
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

package object action {
  object Around {
    def apply(before: Unit, after: Unit): Around = new Around(() => before, () => after)
  }
  class Around(before: () => Unit, after: () => Unit) {

    def apply(f: => Any): Unit = {
      before()
      f
      after()
    }
  }

  trait AmqpLogging extends StrictLogging {
    def logMessage(text: => String, msg: AmqpProtocolMessage): Unit = {
      logger.debug(text)
      logger.trace(msg.toString)
    }
  }

  sealed trait Dest
  case class DirectDest(exchName: String, rk: String) extends Dest
  case class QueueDest(qName: String)                 extends Dest
}
