package com.github.ChubarevYuri;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The standard number of stop bits per byte.
 */
public class StopBits implements ByteConvertedObject {

    private final byte param;

    private StopBits(byte value) {
        this.param = value;
    }

    /**
     * 0 stop bits per byte.
     */
    public static final StopBits NONE = new StopBits((byte) 0);

    /**
     * 1 stop bits per byte.
     */
    public static final StopBits ONE = new StopBits((byte) 1);

    /**
     * 2 stop bits per byte.
     */
    public static final StopBits TWO = new StopBits((byte) 2);

    /**
     * 1.5 stop bits per byte.
     */
    public static final StopBits ONE_POINT_FIVE = new StopBits((byte) 3);

    /**
     * Return array of all valid values.
     * @return All values.
     */
    public static @NotNull StopBits @NotNull [] all() {
        return new StopBits[] {NONE, ONE, TWO, ONE_POINT_FIVE};
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
        if (obj instanceof StopBits) {
            return param == ((StopBits) obj).param;
        }
        return false;
    }

    /**
     * Returns the number of stop bits per byte.
     * @return the number of stop bits per byte.
     */
    public int intValue() {
        return param;
    }

    /**
     * Returns a {@code String} representation of a {@code StopBits}.
     * @return {@code String} formatted "Value".
     */
    @Override
    public @NotNull String toString() {
        return "%ssb".formatted(param == 3 ? "1.5" : "%d".formatted(param));
    }

    /**
     * Returns a {@code byte[]} representation of a {@code StopBits}.
     * @return {@code byte[]} representation of a {@code StopBits}.
     */
    @Override
    public byte @NotNull [] toBytes() {
        return new byte[] {param};
    }

    /**
     * Parses the {@code String} argument as a {@code StopBits}.
     * <br>Correct values = [0, 1, 1.5, 2].
     * After value can be "sb".
     * @param s a {@code String} containing the int representation to be parsed.
     * @return the {@code StopBits} value represented by the argument.
     * @throws StopBitsFormatException if the {@code String} does not contain a parsable {@code StopBits}.
     */
    public static @NotNull StopBits parseStopBits(@NotNull String s) throws StopBitsFormatException {
        return switch (s.toLowerCase()) {
            case "0", "0sb" -> NONE;
            case "1", "1sb" -> ONE;
            case "2", "2sb" -> TWO;
            case "1.5", "1.5sb" -> ONE_POINT_FIVE;
            default -> throw new StopBitsFormatException(s + " not parsed to StopBits");
        };
    }

    /**
     * Parses the {@code int} argument as a {@code StopBits}.
     * <br>Correct values = [0..3].
     * @param i a {@code int} containing the int representation to be parsed.
     * @return the {@code StopBits} value represented by the argument.
     * @throws StopBitsFormatException If the {@code int} does not contain a parsable {@code StopBits}.
     */
    public static @NotNull StopBits parseStopBits(int i) throws StopBitsFormatException {
        return switch (i) {
            case 0 -> NONE;
            case 1 -> ONE;
            case 2 -> TWO;
            case 3 -> ONE_POINT_FIVE;
            default -> throw new StopBitsFormatException(i + " not parsed to StopBits");
        };
    }

    /**
     * Parses the {@code double} argument as a {@code StopBits}.
     * <br>Correct values = [0, 1, 1.5, 2].
     * @param d a {@code double} containing the int representation to be parsed.
     * @return the {@code StopBits} value represented by the argument.
     * @throws StopBitsFormatException If the {@code double} does not contain a parsable {@code StopBits}.
     */
    public static @NotNull StopBits parseStopBits(double d) throws StopBitsFormatException {
        if (d == 0.0) {
            return NONE;
        } else if (d == 1.0) {
            return ONE;
        } else if (d == 2.0) {
            return TWO;
        } else if (d == 1.5) {
            return ONE_POINT_FIVE;
        } else {
            throw new StopBitsFormatException(d + " not parsed to StopBits");
        }
    }

    /**
     * Parses the {@code float} argument as a {@code StopBits}.
     * <br>Correct values = [0, 1, 1.5, 2].
     * @param d a {@code float} containing the int representation to be parsed.
     * @return the {@code StopBits} value represented by the argument.
     * @throws StopBitsFormatException If the {@code float} does not contain a parsable {@code StopBits}.
     */
    public static @NotNull StopBits parseStopBits(float d) throws StopBitsFormatException {
        if (d == 0.0f) {
            return NONE;
        } else if (d == 1.0f) {
            return ONE;
        } else if (d == 2.0f) {
            return TWO;
        } else if (d == 1.5f) {
            return ONE_POINT_FIVE;
        } else {
            throw new StopBitsFormatException(d + " not parsed to StopBits");
        }
    }

    /**
     * Parses the {@code byte[]} argument as a {@code StopBits}.
     * @param arr array of bytes
     * @param startIndex start index to parsed
     * @return the {@code StopBits} value represented by the argument.
     * @throws StopBitsFormatException if the {@code arr[startIndex]} does not contain a parsable {@code StopBits}.
     */
    public static @NotNull StopBits parseStopBits(byte @NotNull [] arr, int startIndex) throws StopBitsFormatException {
        try {
            return parseStopBits(arr[startIndex]);
        } catch (Exception e) {
            throw new StopBitsFormatException(e.getMessage());
        }
    }

    /**
     * Parses the {@code byte[]} argument as a {@code StopBits}.
     * @param arr array of bytes
     * @return the {@code StopBits} value represented by the argument.
     * @throws StopBitsFormatException if the {@code arr[0]} does not contain a parsable {@code StopBits}.
     */
    public static @NotNull StopBits parseStopBits(byte @NotNull [] arr) throws StopBitsFormatException {
        return parseStopBits(arr, 0);
    }

    /**
     * Return this.
     * @return this {@code StopBits}.
     * @throws CloneNotSupportedException Error in clone.
     */
    @Override
    public @NotNull StopBits clone() throws CloneNotSupportedException {
        try {
            return parseStopBits(param);
        } catch (StopBitsFormatException e) {
            throw new CloneNotSupportedException();
        }
    }
}
