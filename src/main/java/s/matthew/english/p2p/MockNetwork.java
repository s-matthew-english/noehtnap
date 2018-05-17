package s.matthew.english.p2p;

//import net.consensys.pantheon.ethereum.p2p.api.Message;
//import net.consensys.pantheon.ethereum.p2p.api.P2PNetwork;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import s.matthew.english.api.*;


/**
 * Mock network implementation that allows passing {@link MessageData} between arbitary peers. This
 * completely bypasses the TCP layer by directly passing {@link MessageData} from
 * {@link MockNetork.MockPeerConnection#send(Capability, MessageData} to callbacks registered on
 * {@link MockNetwork.MockP2PNetwork}.
 */
public final class MockNetwork {

    public final Map<Peer, MockNetwork.MockP2PNetwork> nodes = new HashMap<>();

    /**
     * Get the {@link P2PNetwork} that assumes a given {@link Peer} as the local node. This does not
     * connect {@link Peer} to any other peer. Any connections established by {@link #connect(Peer, Peer)}
     * require that both participating {@link Peer} have previously been passed to this method.
     *
     * @param peer Peer to get {@link P2PNetwork} for
     * @return P2PNetwork as seen by {@link Peer}
     */
    public P2PNetwork setup(final Peer peer) {
        synchronized (this) {
            return nodes.computeIfAbsent(peer, p -> new MockNetwork.MockP2PNetwork(peer, this));
        }
    }

    private PeerConnection connect(final Peer source, final Peer target) {
        synchronized (this) {
            final MockNetwork.MockPeerConnection establishedConnection =
                    new MockNetwork.MockPeerConnection(source, target, this);
            final MockP2PNetwork sourceNode = nodes.get(source);
            final MockP2PNetwork targetNode = nodes.get(target);
            sourceNode.connections.put(target, establishedConnection);
            final MockNetwork.MockPeerConnection backChannel = new MockNetwork.MockPeerConnection(target, source, this);
            targetNode.connections.put(source, backChannel);
            sourceNode.connectConsumers.values().forEach(c -> c.accept(establishedConnection));
            targetNode.connectConsumers.values().forEach(c -> c.accept(backChannel));
            return establishedConnection;
        }
    }

    private void disconnect(final MockNetwork.MockPeerConnection connection, final DisconnectReason reason) {
        synchronized (this) {
            final MockP2PNetwork sourceNode = nodes.get(connection.from);
            final MockP2PNetwork targetNode = nodes.get(connection.to);
            if (targetNode.connections.remove(connection.from) == null || sourceNode.connections.remove(connection.to) == null) {
                throw new IllegalArgumentException(
                        String.format("No connection between %s and %s", connection.from, connection.to));
            }
            targetNode.disconnectConsumers.values().forEach(c -> c.accept(connection.from, reason));
            sourceNode.disconnectConsumers.values().forEach(c -> c.accept(connection.to, DisconnectReason.REQUESTED));
        }
    }

    private static final class MockP2PNetwork implements P2PNetwork {

        private final MockNetwork network;

        private final Map<Peer, MockNetwork.MockPeerConnection> connections = new HashMap<>();

        private final Peer self;

        private final Map<Long, Consumer<Message>> ethConsumers = new ConcurrentHashMap<>();

        private final Map<Long, Consumer<PeerConnection>> connectConsumer = new ConcurrentHashMap<>();

        private final Map<Long, BiConsumer<Peer, DisconnectReason>> disconnectConsumers = new ConcurrentHashMap<>();

        private final AtomicLong identifiers = new AtomicLong(0);

        MockP2PNetwork(final Peer self, final MockNetwork network) {
            this.self = self;
            this.network = network;
        }

        @Override
        public Collection<PeerConnection> peers() {
            synchronized (network) {
                return new ArrayList<>(connections.values());
            }
        }

        @Override
        public CompletableFuture<PeerConnection> connect(final Peer peer) {
            synchronized (network) {
                if (network.nodes.containsKey(peer)) {
                    final PeerConnection connection = connections.get(peer);
                    if (connection == null) {
                        return CompletableFuture.completedFuture(network.connect(self, peer));
                    } else {
                        return CompletableFuture.completedFuture(connection);
                    }
                } else {
                    return CompletableFuture.supplyAsync(() -> {
                        throw new IllegalStateException(
                                String.format("Tried to connect to unknown peer %s", peer));
                    });
                }
            }
        }

        @Override
        public long subscribe(final Capability capability, final Consumer<Message> consumer) {
            if (!capability.equals(EthPV62.CAPABILITY)) {
                throw new IllegalArgumentException("Mock network supports ETH v62");
            }
            final long id = identifiers.incrementAndGet();
            ethConsumers.put(id, consumer);
            return id;
        }

        @Override
        public long subscribeConnect(final Consumer<PeerConnection> consumer) {
            final long id = identifiers.incrementAndGet();
            connectConsumer.put(id, consumer);
            return id;
        }

        @Override
        public long subscribeDisconnect(final BiConsumer<Peer, DisconnectReason> consumer) {
            final long id = identifiers.incrementAndGet();
            disconnectConsumers.put(id, consumer);
            return id;
        }

        @Override
        public void unsubscribe(final long identifier) {
            if (ethConsumers.remove(identifier) == null) {
                connectConsumers.remove(identifier);
            }
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

    /**
     * A mock connection between two peers that simply invokes the callbacks on the other side's
     * {@link MockNetwork.MockP2PNetwork}.
     */
    private static final class MockPeerConnection implements PeerConnection {

        /**
         * {@link Peer} that this connection originates from.
         */
        private final Peer from;

        /**
         * Peer that this connection targets and that will receive {@link Message}s sent via
         * {@link #send(Capability, MessageData)}.
         */
        private final Peer to;

        private final MockNetwork network;

        MockPeerConnection(final Peer source, final Peer target, final MockNetwork network) {
            from = source;
            to = target;
            this.network = network;
        }

        @Override
        public void send(final Capability capability, final MessageData message) throws IOException {
            if (!capability.equals(EthPV62.CAPABILITY)) {
                throw new IllegalArgumentException("Mock network only supports ETH v62");
            }
            synchronized (network) {
                final MockNetwork.MockP2PNetwork target = network.nodes.get(to);
                final MockNetwork.MockPeerConnection backChannel = target.connections.get(from);
                if (backChannel != null) {
                    final Message msg = new DefaultMessage(backChannel, message);
                    target.ethConsumers.values().forEach(c -> c.accept(msg));
                } else {
                    throw new IOException(String.format("%s not connected to %s", to, from));
                }
            }
        }

        @Override
        public PeerInfo peer() {
            return new PeerInfo(5, "mock-network-client", Collections.singletonList(EthPV62.CAPABILITY),
                    to.endpoint().tcpPort().getAsInt(), to.id());
        }

        @Override
        public void disconnect(final DisconnectReason reason) {
            network.disconnect(this, reason);
        }
    }
}
























