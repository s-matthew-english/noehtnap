package s.matthew.english.api;

/**
 * A P2P network message received from another peer.
 */
public interface Message {

  /**
   * Returns the {@link MessageData} contained in the message.
   *
   * @return Data in the message
   */
  MessageData data();

  /**
   * {@link PeerConnection} this message was sent from.
   *
   * @return PeerConnection this message was sent from.
   */
  PeerConnection connection();
}
