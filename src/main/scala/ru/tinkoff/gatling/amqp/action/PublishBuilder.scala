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
    val amqpComponents = components(ctx.protocolComponentsRegistry)

    new Publish(
      attributes,
      amqpComponents,
      ctx.coreComponents,
      ctx.coreComponents.throttler,
      next
    )
  }
}
