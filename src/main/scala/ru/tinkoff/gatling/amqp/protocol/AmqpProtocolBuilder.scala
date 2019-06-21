package ru.tinkoff.gatling.amqp.protocol

import com.rabbitmq.client.ConnectionFactory
import javax.jms.DeliveryMode
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

case class AmqpProtocolBuilder(connectionFactory: ConnectionFactory,
                               deliveryMode: Int = DeliveryMode.NON_PERSISTENT,
                               messageMatcher: AmqpMessageMatcher = MessageIdMessageMatcher,
                               consumerThreadsCount: Int = 1,
                               replyTimeout: Option[Long] = None,
                               responseTransformer: Option[AmqpProtocolMessage => AmqpProtocolMessage] = None) {

  def usePersistentDeliveryMode: AmqpProtocolBuilder    = copy(deliveryMode = DeliveryMode.PERSISTENT)
  def useNonPersistentDeliveryMode: AmqpProtocolBuilder = copy(deliveryMode = DeliveryMode.NON_PERSISTENT)

  def matchByMessageId: AmqpProtocolBuilder     = messageMatcher(MessageIdMessageMatcher)
  def matchByCorrelationId: AmqpProtocolBuilder = messageMatcher(CorrelationIdMessageMatcher)
  def matchByMessage(extractId: AmqpProtocolMessage => String): AmqpProtocolBuilder =
    messageMatcher(AmqpProtocolMessageMatcher(extractId))

  def responseTransform(ext: AmqpProtocolMessage => AmqpProtocolMessage): AmqpProtocolBuilder =
    copy(responseTransformer = Some(ext))
  private def messageMatcher(matcher: AmqpMessageMatcher) = copy(messageMatcher = matcher)

  def replyTimeout(timeout: Long): AmqpProtocolBuilder            = copy(replyTimeout = Some(timeout))
  def consumerThreadsCount(threadCount: Int): AmqpProtocolBuilder = copy(consumerThreadsCount = threadCount)

  def build =
    AmqpProtocol(connectionFactory, deliveryMode, replyTimeout, consumerThreadsCount, messageMatcher, responseTransformer)
}
