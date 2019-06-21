package ru.tinkoff.gatling.amqp.client

import com.rabbitmq.client.Channel

trait WithAmqpChannel {
  protected val pool: AmqpConnectionPool
  def withChannel[T](channelAction: Channel => T): T = {
    val ch     = pool.channel
    val result = channelAction(ch)
    pool.returnChannel(ch)
    result
  }

}
