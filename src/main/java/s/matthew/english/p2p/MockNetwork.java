package s.matthew.english.p2p;

//import net.consensys.pantheon.ethereum.p2p.api.Message;
//import net.consensys.pantheon.ethereum.p2p.api.P2PNetwork;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import s.matthew.english.api.Message;
import s.matthew.english.api.P2PNetwork;
import s.matthew.english.api.Peer;


/**
 * Hello world!
 *
 */
public class MockNetwork
{
    public final Map<Peer, MockNetwork.MockP2PNetwork> peers = new ConcurrentHashMap<>();

    public P2PNetwork getForPeer(final Peer peer) {
      return peers.computeIfAbsent(peer, p -> new MockNetwork().MockP2PNetwork(peer, peers));
    }

    private static final class MockP2PNetwork implements P2PNetwork {

        private final Map<Peer, MockNetwork.MockP2PNetwork> connections;

        private final Peer self;

        private final Map<Long, Consumer<Message>> consumers = new ConcurrentHashMap<>();

        private final AtomicLong identifiers = new AtomicLong(0);

        MockP2PNetwork(final Peer self, final Map<Peer, MockNetwork.MockP2PNetwork> connections) {
            this.self = self;
            this.connections = connections;
        }

        @Override
        public void send(final Message msg) throws IOException {
            final Peer peer = msg.peer();
            if (connections.containsKey(peer)) {
                final Message rerouted = new MockNetwork().ReRoutedMessage(msg, self);
                connections.get(msg.peer()).consumers.values().forEach(c -> c.accept(rerouted));
            } else {
                throw new IOException(String.format("Not connected to %s", peer));
            }
        }

        @Override
        public long subscribe(final Consumer<Message> consumer) {
            final long id = identifiers.incrementAndGet();
            consumers.put(id, consumer);
            return id;
        }

        @Override
        public void unsubscribe(final long identifier) {
            consumers.remove(identifier);
        }

        @Override
        public void stop() {}

        @Override
        public void awaitStop() {}

        @Override
        public void run() {}

        @Override
        public void close() {}
    }

    private static final class ReRoutedMessage implements Message {

        private final Message message;

        private final Peer peer;

        ReRoutedMessage(final Message message, final Peer peer) {
            this.message = message;
            this.peer = peer;
        }

        @Override
        public int size() {
            return message.size();
        }

        @Override
        public int code() {
            return message.code();
        }

        @Override
        public void writeTo(final ByteBuf output) {
            message.writeTo(output);
        }

        @Override
        public void release() {
            message.release();
        }

        @Override
        public void retain() {
            message.retain();
        }

        @Override
        public Peer peer() {
            return peer;
        }
    }
}
























