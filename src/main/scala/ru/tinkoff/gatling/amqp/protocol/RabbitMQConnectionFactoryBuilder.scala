package ru.tinkoff.gatling.amqp.protocol

import com.rabbitmq.client.ConnectionFactory

case class RabbitMQConnectionFactoryBuilder(
    host: Option[String] = None,
    port: Option[Int] = None,
    username: Option[String] = None,
    password: Option[String] = None,
    virtualHost: Option[String] = None
) {

  def username(rabbitUsername: String): RabbitMQConnectionFactoryBuilder =
    this.copy(username = Some(rabbitUsername))
  def password(rabbitPassword: String): RabbitMQConnectionFactoryBuilder =
    this.copy(password = Some(rabbitPassword))

  def port(rabbitPort: Int): RabbitMQConnectionFactoryBuilder =
    this.copy(port = Some(rabbitPort))

  def vhost(rabbitVHost: String): RabbitMQConnectionFactoryBuilder =
    this.copy(virtualHost = Some(rabbitVHost))

  def build: ConnectionFactory = {
    val cf = new ConnectionFactory()

    host.foreach(cf.setHost)
    port.foreach(cf.setPort)
    username.foreach(cf.setUsername)
    password.foreach(cf.setPassword)
    virtualHost.foreach(cf.setVirtualHost)

    cf

  }
}
