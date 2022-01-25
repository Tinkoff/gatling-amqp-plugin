package ru.tinkoff.gatling.amqp.examples

import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

/** This object simply provides a `main` method that wraps [[io.gatling.app.Gatling]].main, which allows us to do some
  * configuration and setup before Gatling launches.
  */
object RequestReplyTwoBrokerRunner {

  def main(args: Array[String]): Unit = {
    // This sets the class for the simulation we want to run.
    val simClass = classOf[RequestReplyTwoBrokerExample].getName

    val props = new GatlingPropertiesBuilder
    props.binariesDirectory("./target/scala-2.11/classes")
    props.simulationClass(simClass)

    Gatling.fromMap(props.build)
    System.exit(0)
  }
}
