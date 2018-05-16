package s.matthew.english.p2p;

//import static net.consensys.pantheon.ethereum.p2p.wire.WireProtocol.wireError;
//import static net.consensys.pantheon.util.bytes.BytesValue.wrap;

//import net.consensys.pantheon.ethereum.rlpx.RLPInput;
//import net.consensys.pantheon.ethereum.rlpx.RLPOutput;
//import net.consensys.pantheon.util.BytesValue;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Represents a client capability.
 *
 * <p>
 * This class is lenient when parsing capabilities name, as a deviation as been observed in mainnet
 * where names may be longer than 3-letter ASCII strings.
 *
 * @see <a href="https://github.com/ethereum/wiki/wiki/%C3%90%CE%9EVp2p-Wire-Protocol#p2p">
 * Capability wire format
 * </a>
 */
public class Capability {
  private final String name;
  private final int version;

  private Capability(String name, int version) {
    if (name.length() != 3) {
      //throw wireError("Expected capability to contain 3-letter ASCII name, was: %s", name);
    }
    this.name = name;
    this.version = version;
  }

  public static Capability create(String name, int version) {
    return new Capability(name, version);
  }

  public String name() {
    return name;
  }

  public int version() {
    return version;
  }

  public void writeTo(RLPOutput out) {
    out.startList();
    out.writeBytesValue(wrap(name().getBytes(StandardCharsets.US_ASCII)));
    out.writeUnsignedByte(version());
    out.endList();
  }

  public static Capability readFrom(RLPInput in) {
    in.enterList();
    final String name = in.readBytesValue(BytesValues::asString);
    final int version = in.readUnsignedByte();
    in.leaveList();
    return Capability.create(name, version);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Capability that = (Capability) o;
    return version == that.version && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, version);
  }

  @Override
  public String toString() {
    return name + "/" + version;
  }
}
