package s.matthew.english.api;

//import net.consensys.pantheon.ethereum.p2p.eth.DisconnectReason;
//import net.consensys.pantheon.ethereum.p2p.PeerInfo.Capability;

import java.io.Closeable;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import s.matthew.english.p2p.Capability;

public interface P2PNetwork extends Closeable, Runnable {

  /**
   * Returns a snapshot of the currently connected peer connections.
   *
   * @return Peers currently connected.
   */
  Collections<PeerConnection> peers();

  /**
   * Connects to a {@link Peer}.
   *
   * @param peer Peer to connect to.
   * @return Future of the established {@link PeerConnection}
   */
  CompletableFuture<PeerConnection> connect(Peer peer);

  /**
   * Subscribe a {@link Consumer} to all incoming {@link Message} of a given sub-protocol. Calling
   * {@link #run()} on an implementation without at least having one subscribed {@link Consumer} per
   * supported sub-protocol should throw a {@link RuntimeException}.
   *
   * @param capability Capability (sub-protocol) to subscribe to.
   * @param consumer Consumer to subcribe
   * @return Consumer Id that can be used for removing subscription via {@link #unsubscribe(long)}
   */
  long subscribe(Capability capability, Consumer<Message> consumer);

  /**
   * Subscribe a {@link Consumer} to all incoming new Peer connection events.
   *
   * @param consumer Consumer to subscribe
   * @return Consumer Id that can be used for removing subscription via {@link #unsubscribe(long)}
   */
  long subscribeConnect(Consumer<PeerConnection> consumer);

  /**
   * Subscribe a {@link Consumer} to all incoming new Peer disconnect events.
   *
   * @param consumer Consumer to subscribe
   * @return Consumer Id that can be used for removing subscription via {@link #unsubscribe(long)}
   */
  long subscribeDisconnect(BiConsumer<Peer, DisconnectReason> consumer);

  /**
   * Removes subscription for a given consumer.
   *
   * @param identifier of {@link Consumer} to unsubscribe.
   */
  void unsubscribe(long identifier);

  /**
   * Stops the P2P network layer.
   */
  void stop();

  /**
   * Blocks until the P2P network layer has stopped.
   */
  void awaitStop();
}






















