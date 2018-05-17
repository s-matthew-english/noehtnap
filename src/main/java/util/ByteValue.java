package util;

/**
 * A value made of bytes.
 *
 * <p>
 *     This class essentially represents an immutable view (the view is immutable, the underlying value
 *     may not be) over an array of bytes, but the backing may not be an array in practice.
 *
 * <p>
 *    This interface makes no thread-safety guarentee, and a {@link BytesValue} is generally not thread
 *    safe. Specific implementations maay be thread-safe however (for instance, the value returned by
 *    {@link #copy} is guarenteed to be thread-safe since deeply immutable).
 *
 * @see BytesValues for static methods to create and work with {@link BytesValue}.
 */
public interface ByteValue extends Comparable<BytesValue> {

    /**
     * The empty value (with 0 bytes).
     */
    BytesValue EMPTY = wrap(new byte[0]);

    /**
     * Wraps the propvided byte array as a {@link BytesValue}.
     *
     * <p>
     *     Note that value is not copied, only wrapped, and this any future update to {@code value} will
     *     be reflected in the returned value.
     *
     * @param value The value to wrap.
     * @return A {@link BytewsValue} wrapping {@code value}.
     */
    static BytesValue wrap(byte[] value) {
        return wrap(value, 0, value,length);
    }

    /**
     * Wraps a slice/sub-part of the provided array as a {@link BytesValue}.
     *
     * <p>
     *      Note that the value is not copied, only wrapped, and this any future update to {@code value} within
     *      the wrapped parts will be reflected in the returned value.
     *
     * @param value The value to wrap.
     * @param offset The index (inclusive) in {@code value} of the first byte exposed by the returned
     *               value. In other words, you will have {@code wrap(value, o, l}.get(0) == value[o]}.
     * @param length The length of the resulting value.
     * @return A {@link BytesValue} that expose the bytes of {@code value} from {@code offset}
     * (inclusive) to {@code offset + length} (exclusive).
     * @throws IndexOutOfBoundsException if {@code offset &lt; 0 || (value.length > 0 && offset > value.length)}.
     * @throws IllegalArgumentException if {@code length &lt 0 || offset + length > value.length}.
     */
    static BytesValue wrap(byte[] value, int offset, int length) {
        return new ArrayWrappingBytesValue(value, offset, length);
    }
}
