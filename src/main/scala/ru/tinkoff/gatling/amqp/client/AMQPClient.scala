package ru.tinkoff.gatling.amqp.client

import com.rabbitmq.client.AMQP.{Exchange, Queue}
import com.rabbitmq.client.{CancelCallback, ConnectionFactory, DeliverCallback, Delivery}
import ru.tinkoff.gatling.amqp.protocol.{AmqpExchange, AmqpQueue, BindQueue}
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

import java.util.concurrent.{ExecutorService, TimeUnit}
import scala.concurrent._
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success}

object AMQPClient {

  private val defaultCancelCallback: CancelCallback = (_: String) => ()

  private def deliverCallback(f: AmqpProtocolMessage => Unit): DeliverCallback =
    (_: String, message: Delivery) => {
      f(AmqpProtocolMessage(message.getProperties, message.getBody))
    }

  private final class AsyncAMQPClient(publisherFactory: ConnectionFactory,
                                      consumerFactory: ConnectionFactory,
                                      blockingPool: ExecutorService,
                                      consumersThreadCount: Int,
  ) extends AMQPClient {
    private implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(blockingPool)
    private val publisherProvider             = ChannelsProvider.publisherProvider(publisherFactory, blockingPool)
    private val consumerProvider =
      ChannelsProvider.consumerProvider(consumerFactory, blockingPool, consumersThreadCount)

    private def withCompletion[T, U](fut: Future[T])(s: T => U, f: Throwable => U): Unit = fut.onComplete {
      case Success(value)     => s(value)
      case Failure(exception) => f(exception)
    }

    override def basicPublish(name: String, routingKey: String, msg: AmqpProtocolMessage)(f: Unit => Unit,
                                                                                          e: Throwable => Unit): Unit =
      withCompletion(
        for {
          ch <- publisherProvider.channel
          _  <- Future(ch.basicPublish(name, routingKey, msg.amqpProperties, msg.payload))
          _  <- publisherProvider.releaseChannel(ch)
        } yield ()
      )(f, e)

    override def consumeWith(sourceQueue: String)(f: AmqpProtocolMessage => Unit, e: Throwable => Unit): Unit =
      withCompletion(
        for {
          ch <- consumerProvider.channel
          _  <- Future(ch.basicConsume(sourceQueue, true, deliverCallback(f), defaultCancelCallback))
          _  <- publisherProvider.releaseChannel(ch)
        } yield ()
      )(identity, e)

    override def exchangeDeclare(ex: AmqpExchange)(f: Exchange.DeclareOk => Unit, e: Throwable => Unit): Unit =
      withCompletion(
        for {
          ch <- publisherProvider.channel
          r <- Future(
                ch.exchangeDeclare(ex.name,
                                   ex.exchangeType,
                                   ex.durable,
                                   ex.autoDelete,
                                   ex.arguments.asJava.asInstanceOf[java.util.Map[String, Object]])
              )
          _ <- publisherProvider.releaseChannel(ch)
        } yield r
      )(f, e)

    override def queueDeclare(q: AmqpQueue)(f: Queue.DeclareOk => Unit, e: Throwable => Unit): Unit =
      withCompletion(
        for {
          ch <- publisherProvider.channel
          r <- Future(
                ch.queueDeclare(q.name,
                                q.durable,
                                q.exclusive,
                                q.autoDelete,
                                q.arguments.asJava.asInstanceOf[java.util.Map[String, Object]])
              )
          _ <- publisherProvider.releaseChannel(ch)
        } yield r
      )(f, e)

    override def queueBind(bind: BindQueue)(f: Queue.BindOk => Unit, e: Throwable => Unit): Unit =
      withCompletion(
        for {
          ch <- publisherProvider.channel
          r <- Future(
                ch.queueBind(bind.queueName,
                             bind.exchangeName,
                             bind.routingKey,
                             bind.args.asJava.asInstanceOf[java.util.Map[String, Object]])
              )
          _ <- publisherProvider.releaseChannel(ch)
        } yield r
      )(f, e)

    override def close(): Unit = {
      withCompletion(
        for {
          _ <- publisherProvider.closeAll
          _ <- consumerProvider.closeAll
          _ <- Future {
                blockingPool.shutdown()
                blockingPool.awaitTermination(60, TimeUnit.SECONDS)
              }
        } yield ()
      )(identity, _ => ())
    }
  }

  def async(publisherFactory: ConnectionFactory,
            consumerFactory: ConnectionFactory,
            blockingPool: ExecutorService,
            consumersThreadCount: Int,
  ): AMQPClient = new AsyncAMQPClient(publisherFactory, consumerFactory, blockingPool, consumersThreadCount)

}

trait AMQPClient extends AutoCloseable {
  def basicPublish(name: String, routingKey: String, msg: AmqpProtocolMessage)(f: Unit => Unit,
                                                                               e: Throwable => Unit): Unit
  def consumeWith(sourceQueue: String)(f: AmqpProtocolMessage => Unit, e: Throwable => Unit): Unit
  def exchangeDeclare(ex: AmqpExchange)(f: Exchange.DeclareOk => Unit, e: Throwable => Unit): Unit
  def queueDeclare(q: AmqpQueue)(f: Queue.DeclareOk => Unit, e: Throwable => Unit): Unit
  def queueBind(bind: BindQueue)(f: Queue.BindOk => Unit, e: Throwable => Unit): Unit
}
