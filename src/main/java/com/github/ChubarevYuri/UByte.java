package com.github.ChubarevYuri;

import com.github.ChubarevYuri.DCON.UByteFormatException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UByte extends Number implements ByteConvertedObject,Comparable<UByte> {
    private final short value;
    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 255;

    public UByte(int value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new UByteFormatException("Value out of range: " + value);
        }
        this.value = (short) value;
    }

    @Override
    public String toString() {
        return "%02X".formatted(value);
    }

    @Override
    public byte @NotNull [] toBytes() {
        byte b = value > Byte.MAX_VALUE ? (byte) (value - 256) : (byte) value;
        return new byte[] {b};
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    /**
     * Compares this object with the specified object for order. Returns a negative integer, zero, or a positive
     * integer as this object is less than, equal to, or greater than the specified object.
     * @param obj the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     * the specified object.
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (obj instanceof UByte) {
            return value == ((UByte) obj).value;
        }
        return false;
    }

    @Override
    public int compareTo(@NotNull UByte o) {
        return value - o.value;
    }

    /**
     * @return bits array.
     */
    public boolean[] getBits() {
        boolean[] bits = new boolean[8];
        for (int i = 0; i < 8; i++) {
            bits[i] = (value & (1 << i)) != 0;
        }
        return bits;
    }

    /**
     * @param index position [0..7].
     * @return state.
     * @throws IndexOutOfBoundsException if {@code index} out of [0, 7].
     */
    public boolean getBit(int index) {
        if (index < 0 || index >= 8) {
            throw new IndexOutOfBoundsException("Out index of range [0, 7]");
        }
        return (value & (1 << index)) != 0;
    }

    /**
     * @param index position [0..7].
     * @param bit new state.
     * @return corrected {@code UByte}
     * @throws IndexOutOfBoundsException if {@code index} out of [0, 7].
     */
    public UByte setBit(int index, boolean bit) {
        if (index < 0 || index >= 8) {
            throw new IndexOutOfBoundsException("Out index of range [0, 7]");
        }
        int value = this.value;
        if (bit) {
            value |= 1 << index;
        } else {
            value &= ~(1 << index);
        }
        return new UByte(value);
    }

    /**
     * @param beginIndex [<b>beginIndex</b>, endIndex).
     * @param endIndex [beginIndex, <b>endIndex</b>).
     * @return value of bits.
     * @throws IndexOutOfBoundsException if {@code beginIndex} or {@code endIndex} out of [0, 7]
     * or {@code beginIndex} >= {@code endIndex}
     */
    public int getBits(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > 8 || beginIndex >= endIndex) {
            throw new IndexOutOfBoundsException("Out indexes of range [0, 7]");
        }
        int bits = 0;
        for (int i = endIndex - 1; i >= beginIndex; i--) {
            bits *= 2;
            if (getBit(i)) {
                bits += 1;
            }
        }
        return bits;
    }

    /**
     * @param beginIndex [<b>beginIndex</b>, endIndex).
     * @param endIndex [beginIndex, <b>endIndex</b>).
     * @param bits value.
     * @return corrected {@code UByte}
     * @throws IndexOutOfBoundsException if {@code beginIndex} or {@code endIndex} out of [0, 7]
     * or {@code beginIndex} >= {@code endIndex} or {@code} bits >= 2^({@code endIndex} - {@code beginIndex})
     */
    public UByte setBits(int beginIndex, int endIndex, int bits) {
        if (beginIndex < 0 || endIndex > 8 || beginIndex >= endIndex) {
            throw new IndexOutOfBoundsException("Out indexes of range [0, 7]");
        }
        int maxBits = 2;
        for (int i = beginIndex; i < endIndex-1; i++) {
            maxBits *= 2;
        }
        if (bits < 0 || bits >= maxBits) {
            throw new IndexOutOfBoundsException("Out bits of range");
        }
        UByte value = new UByte(this.value);
        for (int i = beginIndex; i < endIndex; i++) {
            value = value.setBit(i, bits % 2 == 1);
            bits /= 2;
        }
        return value;
    }

    /**
     * Parses the {@code String} argument as a {@code UByte}.
     * <br>Correct values = 02X.
     * @param s a {@code String} containing the int representation to be parsed.
     * @return the {@code UByte} value represented by the argument.
     * @throws DataBitsFormatException if the {@code String} does not contain a parsable {@code UByte}.
     */
    public static @NotNull UByte parseUByte(@NotNull String s) throws UByteFormatException {
        try {
            return new UByte(Integer.parseInt(s, 16));
        } catch (Exception e) {
            throw new UByteFormatException(s + " not parsed to UByte");
        }
    }

    /**
     * Parses the {@code int} argument as a {@code UByte}.
     * <br>Correct values = [0..255].
     * @param i a {@code int} containing the int representation to be parsed.
     * @return the {@code DataBits} value represented by the argument.
     * @throws DataBitsFormatException If the {@code int} does not contain a parsable {@code UByte}.
     */
    public static @NotNull UByte parseUByte(int i) throws UByteFormatException {
        try {
            return new UByte((short) i);
        } catch (Exception e) {
            throw new UByteFormatException(i + " not parsed to UByte");
        }
    }

    /**
     * Parses the {@code byte} argument as a {@code UByte}.
     * @param b a {@code byte} containing the int representation to be parsed.
     * @return the {@code DataBits} value represented by the argument.
     */
    public static @NotNull UByte parseUByte(byte b) {
        return new UByte(b < 0 ? 256 + b : b);
    }

    /**
     * Parses the {@code byte[]} argument as a {@code DataBits}.
     * @param arr array of bytes
     * @param startIndex start index to parsed
     * @return the {@code DataBits} value represented by the argument.
     * @throws DataBitsFormatException if the {@code arr[startIndex]} does not contain a parsable {@code DataBits}.
     */
    public static @NotNull UByte parseUByte(byte @NotNull [] arr, int startIndex) throws UByteFormatException {
        try {
            return parseUByte(arr[startIndex]);
        } catch (Exception e) {
            throw new UByteFormatException(e.getMessage());
        }
    }

    /**
     * Parses the {@code byte[]} argument as a {@code DataBits}.
     * @param arr array of bytes
     * @return the {@code DataBits} value represented by the argument.
     * @throws DataBitsFormatException if the {@code arr[0]} does not contain a parsable {@code DataBits}.
     */
    public static @NotNull UByte parseUByte(byte @NotNull [] arr) throws UByteFormatException {
        return parseUByte(arr, 0);
    }

    /**
     * Convert bytes array to {@link String}
     * @param arr converted object.
     * @return converted result.
     */
    public static @NotNull String convertToString(byte @NotNull ... arr) {
        StringBuilder builder = new StringBuilder("[");
        boolean fst = true;
        for (byte b : arr) {
            if (fst) {
                fst = false;
            } else {
                builder.append(", ");
            }
            builder.append("%02X".formatted(b));
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Convert {@link UByte} array to {@link String}
     * @param arr converted object.
     * @return converted result.
     */
    public static @NotNull String convertToString(@NotNull UByte @NotNull ... arr) {
        StringBuilder builder = new StringBuilder("[");
        boolean fst = true;
        for (UByte b : arr) {
            if (fst) {
                fst = false;
            } else {
                builder.append(", ");
            }
            builder.append(b.toString());
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Convert bytes array to {@link UByte} array
     * @param arr converted object.
     * @return converted result.
     */
    public static @NotNull UByte @NotNull [] convertToUByte(byte @NotNull ... arr) {
        UByte[] result = new UByte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = UByte.parseUByte(arr[i]);
        }
        return result;
    }

    /**
     * Convert bytes array to {@link UByte} array
     * @param arr converted object.
     * @return converted result.
     */
    public static byte @NotNull [] convertToByte(@NotNull UByte @NotNull ... arr) {
        byte[] result = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[i].byteValue();
        }
        return result;
    }
}
