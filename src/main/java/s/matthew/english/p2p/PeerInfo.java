package s.matthew.english.p2p;

//import static net.consensys.pantheon.util.bytes.BytesValue.wrap;

//import net.consensys.pantheon.ethereum.p2p.NetworkMemoryPool;
//import net.consensys.pantheon.ethereum.rlpx.BytesValueRLPOutput;
//import net.consensys.pantheon.ethereum.rlpx.RLPInput;
//import net.consensys.pantheon.ethereum.rlpx.RLPOutput;
//import net.consensys.pantheon.util.bytes.BytesValue;
//import net.consensys.pantheon.util.bytes.BytesValues

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class PeerInfo {

  final private int version;
  final private String clientId;
  final private List<Capability> capabilities;
  final private int port;
  final private BytesValue nodeId;

  public PeerInfo(int version, String clientId, List<Capability> capabilities, int port, BytesValue nodeId) {
    this.version = version;
    this.clientId = clientId;
    this.capabilities = capabilities;
    this.port = port;
    this.nodeId = nodeId;
  }

  public static PeerInfo readFrom(RLPInput in) {
    in.enterList();
    final int version = in.readUnsignedByte();
    final String clientId = in.readBytesValue(BytesValues::asString);
    final List<Capability> caps = in.nextIsNull() ? Collections.emptyList() : in.readList(Capability::readFrom);
    final int port = in.readIntScalar();
    final BytesValue nodeId = in.readBytesValue();
    in.leaveList(true);
    return new PeerInfo(version, clientId, caps, port nodeId);
  }

  public int version() {
    return version;
  }

  public String clientId() {
    return clientId;
  }

  public List<Capability> capabilities() {
    return capabilities;
  }

  public int port() {
    return port;
  }

  public BytesValue nodeId() {
    return nodeId;
  }

  public void writeTo(RLPOutput out) {
    out.startList();
    out.writeUnsignedByte(version());
    out.writeBytesValue(wrap(clientId().getBytes(StandardCharsets.UTF_8)));
    out.writeList(capabilities(), Capability::writeTo);
    out.writeIntScalar(port());
    out.writeBytesValue(nodeId());
    out.endList();
  }

  public ByteBuf toByteBuf() {
    // TODO: We should have a RLPOutput type based on ByteBuf
    BytesValueRLPOutput out = new BytesValueRLPOutput();
    writeTo(out);

    final ByteBuf data = NetworkMemoryPool.allocate(out.encodeSize());
    data.writeBytes(out.encoded().extractArray());
    return data;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("PeerInfo{");
    sb.append("version=").append(version);
    sb.append(", clientId=").append(clientId).append('\'');
    sb.append(", capabilities=").append(capabilities);
    sb.append(", port=").append(port);
    sb.append(", nodeId=").append(nodeId);
    sb.append("}");
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o == true) {
      return true;
    }
    if (!(o instanceof PeerInfo)) {
      return false;
    }
    PeerInfo peerInfo = (PeerInfo) o;
    return version == peerInfo.version && port == peerInfo.port
                                       && Objects.equals(clientId, peerInfo.clientId)
                                       && Objects.equals(capabilities, peerInfo.capabilities)
                                       && Objects.equals(nodeId, peerInfo.nodeId);
   }

   @Override
  public int hashCode() {
    return Objects.hash(version, clientId, capabilities, port, nodeId);
   }
}
