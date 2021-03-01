package ru.tinkoff.gatling.amqp.protocol

import com.rabbitmq.client.ConnectionFactory
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

case class AmqpProtocolBuilder(
    requestConnectionFactory: ConnectionFactory,
    replyConnectionFactory: ConnectionFactory,
    deliveryMode: DeliveryMode = NonPersistent(),
    messageMatcher: AmqpMessageMatcher = MessageIdMessageMatcher,
    consumerThreadsCount: Int = 1,
    replyTimeout: Option[Long] = None,
    responseTransformer: Option[AmqpProtocolMessage => AmqpProtocolMessage] = None,
    initActions: AmqpChannelInitActions = Nil
) {

  def usePersistentDeliveryMode: AmqpProtocolBuilder    = copy(deliveryMode = Persistent())
  def useNonPersistentDeliveryMode: AmqpProtocolBuilder = copy(deliveryMode = NonPersistent())

  def matchByMessageId: AmqpProtocolBuilder =
    messageMatcher(MessageIdMessageMatcher)

  def matchByCorrelationId: AmqpProtocolBuilder =
    messageMatcher(CorrelationIdMessageMatcher)

  def matchByMessage(extractId: AmqpProtocolMessage => String): AmqpProtocolBuilder =
    messageMatcher(AmqpProtocolMessageMatcher(extractId))

  def responseTransform(ext: AmqpProtocolMessage => AmqpProtocolMessage): AmqpProtocolBuilder =
    copy(responseTransformer = Some(ext))

  private def messageMatcher(matcher: AmqpMessageMatcher) =
    copy(messageMatcher = matcher)

  def replyTimeout(timeout: Long): AmqpProtocolBuilder            = copy(replyTimeout = Some(timeout))
  def consumerThreadsCount(threadCount: Int): AmqpProtocolBuilder = copy(consumerThreadsCount = threadCount)

  def declare(q: AmqpQueue): AmqpProtocolBuilder    = this.copy(initActions = this.initActions :+ QueueDeclare(q))
  def declare(e: AmqpExchange): AmqpProtocolBuilder = this.copy(initActions = this.initActions :+ ExchangeDeclare(e))

  def bindQueue(
      q: AmqpQueue,
      e: AmqpExchange,
      routingKey: String,
      args: Map[String, Any] = Map.empty
  ): AmqpProtocolBuilder =
    this.copy(initActions = this.initActions :+ BindQueue(q.name, e.name, routingKey, args))

  def build: AmqpProtocol =
    AmqpProtocol(
      requestConnectionFactory,
      replyConnectionFactory,
      deliveryMode,
      replyTimeout,
      consumerThreadsCount,
      messageMatcher,
      responseTransformer,
      initActions
    )
}
