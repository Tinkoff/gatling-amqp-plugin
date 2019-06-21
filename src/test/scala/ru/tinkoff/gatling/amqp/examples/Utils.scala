package ru.tinkoff.gatling.amqp.examples

import io.gatling.core.feeder.Feeder

object Utils {
  private val counter = Iterator.from(1)

  val idFeeder:Feeder[Int] = Iterator.continually(Map("id" -> counter.next()))
}
