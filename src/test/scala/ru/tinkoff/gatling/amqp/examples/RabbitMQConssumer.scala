package ru.tinkoff.gatling.amqp.examples

import com.rabbitmq.client._

// Testclient which consumes messages and writes something in other queue

object RabbitMQConssumer {

  val deliverCallback: DeliverCallback = {
    new DeliverCallback {
      override def handle(consumerTag: String, message: Delivery): Unit = {
        print("message")
        val connection = getConnection(writePort)
        val channel = connection.createChannel()
        channel.queueDeclare(writeQueue, true, false, false, null)
        channel.basicPublish("", writeQueue, null, message.getBody)
      }
    }
  }

  val cancelCallback = new CancelCallback {
    override def handle(consumerTag: String): Unit = {
    }
  }

  def readAndWrite() = {
    val connection = getConnection(readPort)
    val channel = connection.createChannel()
    channel.queueDeclare(readQueue, true, false, false, null)
    channel.basicConsume(readQueue, deliverCallback, cancelCallback)

  }

  def main(args: Array[String]): Unit = {
    setup()
    readAndWrite()
    Thread.sleep(1000000)
    tearDown()
    System.exit(1
    )
  }

  private val readQueue = "readQueue"
  private val readPort = 5672

  private val writeQueue = "writeQueue"
  private val writePort = 5673

  def tearDown(): Unit = {
    val readConnection: Connection = getConnection(readPort)
    val readChannel = readConnection.createChannel()
    readChannel.queueDelete(readQueue)

    val writeConnection: Connection = getConnection(writePort)
    val writeChannel = writeConnection.createChannel()
    writeChannel.queueDelete(writeQueue)

  }

  def setup(): Unit = {
    val readConnection: Connection = getConnection(readPort)
    val readChannel = readConnection.createChannel()
    readChannel.queueDeclare(readQueue, true, false, false, null)

    val writeConnection: Connection = getConnection(writePort)
    val writeChannel = writeConnection.createChannel()
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
