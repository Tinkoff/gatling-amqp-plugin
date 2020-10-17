package ru.tinkoff.gatling.amqp.action

import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.ProtocolComponentsRegistry
import io.gatling.core.structure.ScenarioContext
import ru.tinkoff.gatling.amqp.protocol.{AmqpComponents, AmqpProtocol}
import ru.tinkoff.gatling.amqp.request.AmqpAttributes

case class PublishBuilder(attributes: AmqpAttributes, configuration: GatlingConfiguration) extends ActionBuilder {

  private def components(protocolComponentsRegistry: ProtocolComponentsRegistry): AmqpComponents =
    protocolComponentsRegistry.components(AmqpProtocol.amqpProtocolKey)

  override def build(ctx: ScenarioContext, next: Action): Action = {
    import ctx._
    val amqpComponents = components(protocolComponentsRegistry)
    val statsEngine    = coreComponents.statsEngine

    new Publish(
      attributes,
      amqpComponents,
      statsEngine,
      coreComponents.clock,
      next,
      coreComponents.throttler
    )
  }
}
