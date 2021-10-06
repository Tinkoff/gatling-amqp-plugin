package ru.tinkoff.gatling.amqp.client

import com.rabbitmq.client.{Channel, Connection, ConnectionFactory}

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{ArrayBlockingQueue, ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

object ChannelsProvider {

  private abstract class BaseChannelProvider(blockingPool: ExecutorService) extends ChannelsProvider {
    protected implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(blockingPool)
    private val channelPoolCapacity             = Runtime.getRuntime.availableProcessors()
    private val channels                        = new ArrayBlockingQueue[Channel](channelPoolCapacity)
    private val created                         = new AtomicInteger(0)

    override def channel: Future[Channel] =
      for {
        c <- connection
        ch <- if (created.get() < channelPoolCapacity)
               Future(c.createChannel()).map { channel =>
                 created.getAndIncrement()
                 channel
               } else
               Future(channels.take())
      } yield ch

    override def releaseChannel(ch: Channel): Future[Unit] =
      if (ch.isOpen) Future(channels.put(ch))
      else
        for {
          c <- connection
          _ <- if (c.isOpen) Future(c.createChannel()).map(channels.put) else closeAll
        } yield ()

    override def closeAll: Future[Unit] =
      for {
        c <- connection
        _ <- if (c.isOpen) Future(c.close()) else Future.successful(())
        _ <- Future.sequence(channels.asScala.map(ch => if (ch.isOpen) Future(ch.close()) else Future.successful(())))
      } yield ()
  }

  private final class PublisherChannelProvider(connectionFactory: ConnectionFactory, blockingPool: ExecutorService)
      extends BaseChannelProvider(blockingPool) {

    protected val connection: Future[Connection] =
      Future(connectionFactory.newConnection())
  }

  private final class ConsumerChannelProvider(connectionFactory: ConnectionFactory,
                                              blockingPool: ExecutorService,
                                              consumersCount: Int)
      extends BaseChannelProvider(blockingPool) {
    private val consumersPool = Executors.newFixedThreadPool(consumersCount)
    protected val connection: Future[Connection] =
      Future(connectionFactory.newConnection(consumersPool))
  }

  def publisherProvider(connectionFactory: ConnectionFactory, blockingPool: ExecutorService): ChannelsProvider =
    new PublisherChannelProvider(connectionFactory, blockingPool)

  def consumerProvider(connectionFactory: ConnectionFactory,
                       blockingPool: ExecutorService,
                       consumersCount: Int): ChannelsProvider =
    new ConsumerChannelProvider(connectionFactory, blockingPool, consumersCount)
}

trait ChannelsProvider {
  protected val connection: Future[Connection]
  def channel: Future[Channel]
  def releaseChannel(ch: Channel): Future[Unit]
  def closeAll: Future[Unit]
}
