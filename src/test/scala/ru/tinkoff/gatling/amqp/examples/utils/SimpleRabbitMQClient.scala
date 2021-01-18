package ru.tinkoff.gatling.amqp.examples.utils

import com.rabbitmq.client._

import java.nio.charset.StandardCharsets

/**
  * Simple RabbitMQClient which consumes messages from one broker and write them to other broker.
  */
object SimpleRabbitMQClient {
  private val readQueue = "readQueue"
  private val readPort = 5672
  private val readChannel = getConnection(readPort).createChannel()

  private val writeQueue = "writeQueue"
  private val writePort = 5673
  private val writeChannel = getConnection(writePort).createChannel()

  val deliverCallback: DeliverCallback = {
    (consumerTag: String, message: Delivery) => {
      println("Received a message")
      writeChannel.queueDeclare(writeQueue, true, false, false, null)
      writeChannel.basicPublish("", writeQueue, message.getProperties, "Message processed".getBytes(StandardCharsets.UTF_8))
    }
  }

  val cancelCallback: CancelCallback = (consumerTag: String) => {
  }

  def readAndWrite(): String = {
    readChannel.queueDeclare(readQueue, true, false, false, null)
    readChannel.basicConsume(readQueue, true, deliverCallback, cancelCallback)
  }

  def tearDown(): Unit = {
    readChannel.queueDelete(readQueue)
    writeChannel.queueDelete(writeQueue)
  }

  def setup(): Unit = {
    readChannel.queueDeclare(readQueue, true, false, false, null)
    writeChannel.queueDeclare(writeQueue, true, false, false, null)
  }

  private def getConnection(port: Int) = {
    val connectionFactory = new ConnectionFactory()
    connectionFactory.setHost("localhost")
    connectionFactory.setPort(port)
    val connection = connectionFactory.newConnection()
    connection
  }

}
