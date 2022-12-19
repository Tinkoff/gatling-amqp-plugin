package ru.tinkoff.gatling.javaapi.protocol;

import io.gatling.core.protocol.Protocol;
import io.gatling.javaapi.core.ProtocolBuilder;
import ru.tinkoff.gatling.amqp.protocol.AmqpProtocol;
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage;
import static scala.jdk.javaapi.CollectionConverters.asScala;
import scala.Function1;

import java.util.Map;

public class AmqpProtocolBuilder implements ProtocolBuilder{
    private ru.tinkoff.gatling.amqp.protocol.AmqpProtocolBuilder wrapped;

    public AmqpProtocolBuilder(ru.tinkoff.gatling.amqp.protocol.AmqpProtocolBuilder wrapped) {
        this.wrapped = wrapped;
    }

    public AmqpProtocolBuilder usePersistentDeliveryMode() {
        this.wrapped = wrapped.usePersistentDeliveryMode();
        return this;
    }

    public AmqpProtocolBuilder useNonPersistentDeliveryMode() {
        this.wrapped = wrapped.useNonPersistentDeliveryMode();
        return this;
    }

    public AmqpProtocolBuilder matchByMessageId() {
        this.wrapped = wrapped.matchByMessageId();
        return this;
    }

    public AmqpProtocolBuilder matchByCorrelationId() {
        this.wrapped = wrapped.matchByMessageId();
        return this;
    }

    public AmqpProtocolBuilder matchByMessage(Function1<AmqpProtocolMessage, String> extractId) {
        this.wrapped = wrapped.matchByMessage(extractId);
        return this;
    }

    public AmqpProtocolBuilder responseTransform(Function1<AmqpProtocolMessage, AmqpProtocolMessage> ext) {
        this.wrapped = wrapped.responseTransform(ext);
        return this;
    }

    public AmqpProtocolBuilder replyTimeout(Long timeout) {
        this.wrapped = wrapped.replyTimeout(timeout);
        return this;
    }

    public AmqpProtocolBuilder consumerThreadsCount(Integer threadCount) {
        this.wrapped = wrapped.consumerThreadsCount(threadCount);
        return this;
    }

    public AmqpProtocolBuilder declare(AmqpQueue q) {

        this.wrapped = wrapped.declare(ru.tinkoff.gatling.amqp.Predef.queue(
                q.getName(),
                q.getDurable(),
                q.getExclusive(),
                q.getAutoDelete(),
                scala.collection.immutable.Map.from(asScala(q.getArguments()))
        ));
        return this;
    }

    public AmqpProtocolBuilder declare(AmqpExchange e) {
        this.wrapped = wrapped.declare(ru.tinkoff.gatling.amqp.Predef.exchange(
                e.getName(),
                e.getExchangeType(),
                e.getDurable(),
                e.getAutoDelete(),
                scala.collection.immutable.Map.from(asScala(e.getArguments()))
        ));
        return this;
    }

    public AmqpProtocolBuilder bindQueue(
            AmqpQueue q,
            AmqpExchange e,
            String routingKey,
            Map<String, Object> args
    ) {
        this.wrapped = wrapped.bindQueue(
                ru.tinkoff.gatling.amqp.Predef.queue(
                        q.getName(),
                        q.getDurable(),
                        q.getExclusive(),
                        q.getAutoDelete(),
                        scala.collection.immutable.Map.from(asScala(q.getArguments()))
                ),
                ru.tinkoff.gatling.amqp.Predef.exchange(
                        e.getName(),
                        e.getExchangeType(),
                        e.getDurable(),
                        e.getAutoDelete(),
                        scala.collection.immutable.Map.from(asScala(e.getArguments()))
                ),
                routingKey,
                scala.collection.immutable.Map.from(asScala(args))
        );
        return this;
    }

    @Override
    public Protocol protocol() {
        return wrapped.build();
    }
}
