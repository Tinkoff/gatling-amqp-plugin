package ru.tinkoff.gatling.amqp.client

import java.util.concurrent.Executors

import com.rabbitmq.client.{Channel, Connection, ConnectionFactory}
import org.apache.commons.pool2.ObjectPool
import org.apache.commons.pool2.impl.{GenericObjectPool, GenericObjectPoolConfig}

class AmqpConnectionPool(factory: ConnectionFactory, consumerThreadsCount: Int) {

  private val connection: Connection = factory.newConnection(Executors.newFixedThreadPool(consumerThreadsCount))

  private val poolConfig = new GenericObjectPoolConfig[Channel]()
  poolConfig.setMaxTotal(16)

  private val channelPool: ObjectPool[Channel] = new GenericObjectPool[Channel](new AmqpChannelFactory(connection))

  def createConsumerChannel: Channel = connection.createChannel()

  def close(): Unit = {
    if (connection != null) {
      channelPool.close()
      if (connection.isOpen) {
        connection.close()
      }
    }
  }

  def channel: Channel = {
    channelPool.borrowObject()
  }
  def returnChannel(ch: Channel): Unit = channelPool.returnObject(ch)

  def invalidate(ch: Channel): Unit = channelPool.invalidateObject(ch)
}
