package s.matthew.english.p2p.rlpx;

//import net.consensys.pantheon.util.Bytes32;
//import net.consensys.pantheon.util.BytesValue;
//import net.consensys.pantheon.util.UInt256;
//import net.consensys.pantheon.util.UInt256Value;

/**
 * An input used to decode data in ELP encoding.
 *
 * <p>
 *   An RLP "value" is fundamentally an {@code Item} defined the following way:
 *
 *   <pre>
 *     Item ::= List | Bytes
 *     List ::= [Item, ..., Item]
 *     Bytes ::= a binary value (comprised of an arbitrary number of bytes).
 *   </pre>
 *
 *   In other words, RLP encodes binary data organized in arbitrary nested lists.
 *
 * <p>
 *   A {@link RLPInput} thus provides methods to decode both lists and binary values. A list in the
 *   input is "entered" by calling {@link #enterList()} and left by calling {@link #leaveList()}.
 *   Binary values can be read directly with {@link #readBytesValue()} (!!!), but the {@link RLPInput}
 *   interface provides a wealth of convenience methods to read specific types of data that are in
 *   specific encoding.
 *
 * <p>
 *   Amongst the methods to read binary data, some methods are provided to read "scalr". A scalar
 *   should simply be understood as a positive integer that is encoded with no leading zeros. In other
 *   words (!!!), a method like {@link #readLongScalar()} does not expect an encoded value of exactly 8 bytes
 *   (by opposition to {@link #readLong} (!!!)), but rather one that is "up to" 8 bytes.
 *
 *  @see BytesValueRLPInput for a {@link RLPInput} that decode an RLP encoded value stored in a
 *       {@link BytesValue}.
 */
public class RLPInput {

}
