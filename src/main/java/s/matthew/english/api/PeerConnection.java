package s.matthew.english.api;


//import net.consensys.pantheon.ethereum.p2p.PeerInfo.Capability;
//import net.consensys.pantheon.ethereum.p2p.PeerInfo.PeerInfo;

import java.io.IOException;
import s.matthew.english.p2p.Capability;
import s.matthew.english.p2p.PeerInfo;
import util.DisconnectReason;

/**
 * A P2P connection to another node.
 */
public interface PeerConnection {

  /**
   * Send given data to the connected node.
   *
   * @param message Data to send
   * @param protocol Sub-protocol to use
   * @throws IOException On failure to enqueue sending data
   */
  void send(Capability protocol, MessageData message) throws IOException;

  /**
   * Returns the Peer's Description.
   *
   * @return Peer Description
   */
  PeerInfo peer();

  /**
   * Disconnect from this Peer.
   *
   * @param reason Reason for disconnecting
   */
  void disconnect(DisconnectReason reason);
}
