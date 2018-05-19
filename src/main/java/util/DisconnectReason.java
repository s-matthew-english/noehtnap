package util;

import java.util.stream.Stream;

public enum DisconnectReason {

    REQUESTED((byte) 0x00),
    TCP_SUBSYSTEM_ERROR((byte) 0x01),
    BREACH_OF_PROTOCOL((byte) 0x02),
    USELESS_PEER((byte) 0x03),
    TOO_MANY_PEERS((byte) 0x04),
    ALREADY_CONNECTED((byte) 0x05),
    INCOMPATIBLE_P2P_PROTOCOL_VERSION((byte) 0x06),
    NULL_NODE((byte) 0x07),
    CLIENT_QUITTING((byte) 0x08),
    UNEXPECTED_ID((byte) 0x09),
    LOCAL_IDENTITY((byte) 0x0a),
    TIMEOUT((byte) 0x0b),
    SUBPROTOCOL_TRIGGERED((byte) 0x10);

    private final static DisconnectReason[] BY_ID;
    private final byte code;

    static {
        final int maxValue = Stream.of(values()).mapToInt(dr -> (int) dr.value()).max().getAsInt();
        BY_ID = new DisconnectReason[maxValue + 1];
        Stream.of(values()).forEach(dr -> BY_ID[dr.value()] = dr);
    }

    public static DisconnectReason forCode(byte code) {
        code = (byte) (code & 0xff);
        DisconnectReason reason = BY_ID[code];
        return reason;
    }

    DisconnectReason(byte code) {this.code = code;}

    public byte value() {return code;}
}
