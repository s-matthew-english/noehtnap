package s.matthew.english;

//import net.consensys.pantheon.ethereum.rlp.RLPOutput;
//import net.consensys.pantheon.util.bytes.BytesValue;

import java.util.Random;

//import com.google.common.annotations.VisibleForTesting;

public interface Peer {

  /**
   * The ID of the peer, equivalent to its public key. In public Ethereum, the public key is derived
   * from the signatures the peer attaches to certain messages.
   *
   * @return The peer's ID.
   */
  BytesValue id();

  /**
   * A struct-like immutable object encapsulating the peer's network coordinates, namely their
   * hostname (as an IP address in the current implementation), UDP port and optional TCP port for
   * RLPx communications.
   *
   * @return An object encapsulating the peer's network coordinates.
   */
  Endpoint endpoint();

  /**
   * <strong>This method should only be used for testing.</strong>
   * Generates a pseudo-random peer ID in a non-secure manner.
   *
   * @return The generated peer ID.
   */
  @VisibleForTesting
  static BytesValue randomId() {
    byte[] id = new byte[64];
    new Random().nextBytes(id);
    return BytesValue.wrap(id);
  }

  /**
   * Encodes this peer to its RLP representation.
   *
   * @param out The RLP output stream to which to write.
   */
  default void writeTo(RLPOutput out) {
    out.startList();
    endpoint().encodeInline(out);
    out.writeBytesValue(id());
    out.endList();
  }


}
