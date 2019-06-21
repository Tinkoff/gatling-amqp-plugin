package ru.tinkoff.gatling.amqp.client

import com.rabbitmq.client.{Channel, Connection}
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.{BasePooledObjectFactory, PooledObject}

class AmqpChannelFactory(rabbitmqConnection: Connection) extends BasePooledObjectFactory[Channel] {
  override def create(): Channel = rabbitmqConnection.createChannel()

  override def wrap(obj: Channel): PooledObject[Channel] = new DefaultPooledObject[Channel](obj)

  override def destroyObject(p: PooledObject[Channel]): Unit =
    if (validateObject(p))
      p.getObject.close()

  override def validateObject(p: PooledObject[Channel]): Boolean = p.getObject.isOpen
}
