package com.github.ChubarevYuri;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The standard number of data bits per byte.
 */
public class DataBits implements ByteConvertedObject {

    private final byte param;

    private DataBits(byte value) {
        this.param = value;
    }

    /**
     * 7  data bits per byte.
     */
    public static final DataBits SEVEN = new DataBits((byte) 7);

    /**
     * 8 data bits per byte.
     */
    public static final DataBits EIGHT = new DataBits((byte) 8);

    /**
     * Return array of all valid values.
     * @return All values.
     */
    public static @NotNull DataBits @NotNull [] all() {
        return new DataBits[] {SEVEN, EIGHT};
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
        if (obj instanceof DataBits) {
            return param == ((DataBits) obj).param;
        }
        return false;
    }

    /**
     * Returns the standard number of data bits per byte.
     * @return the standard number of data bits per byte.
     */
    public int intValue() {
        return param;
    }

    /**
     * Returns a {@code String} representation of a {@code DataBits}.
     * @return {@code String} formatted "Value"+"db".
     */
    @Override
    public @NotNull String toString() {
        return "%ddb".formatted(param);
    }

    /**
     * Returns a {@code byte[]} representation of a {@code DataBits}.
     * @return {@code byte[]} representation of a {@code DataBits}.
     */
    @Override
    public byte @NotNull [] toBytes() {
        return new byte[] {param};
    }

    /**
     * Parses the {@code String} argument as a {@code DataBits}.
     * <br>Correct values = [7, 8].
     * After value can be "db".
     * @param s a {@code String} containing the int representation to be parsed.
     * @return the {@code DataBits} value represented by the argument.
     * @throws DataBitsFormatException if the {@code String} does not contain a parsable {@code DataBits}.
     */
    public static @NotNull DataBits parseDataBits(@NotNull String s) throws DataBitsFormatException {
        return switch (s.toLowerCase()) {
            case "7db", "7" -> SEVEN;
            case "8db", "8" -> EIGHT;
            default -> throw new DataBitsFormatException(s + " not parsed to DataBits");
        };
    }

    /**
     * Parses the {@code int} argument as a {@code DataBits}.
     * <br>Correct values = [7, 8].
     * @param i a {@code int} containing the int representation to be parsed.
     * @return the {@code DataBits} value represented by the argument.
     * @throws DataBitsFormatException If the {@code int} does not contain a parsable {@code DataBits}.
     */
    public static @NotNull DataBits parseDataBits(int i) throws DataBitsFormatException {
        return switch (i) {
            case 7 -> SEVEN;
            case 8 -> EIGHT;
            default -> throw new DataBitsFormatException(i + " not parsed to DataBits");
        };
    }

    /**
     * Parses the {@code byte[]} argument as a {@code DataBits}.
     * @param arr array of bytes
     * @param startIndex start index to parsed
     * @return the {@code DataBits} value represented by the argument.
     * @throws DataBitsFormatException if the {@code arr[startIndex]} does not contain a parsable {@code DataBits}.
     */
    public static @NotNull DataBits parseDataBits(byte @NotNull [] arr, int startIndex) throws DataBitsFormatException {
        try {
            return parseDataBits(arr[startIndex]);
        } catch (Exception e) {
            throw new DataBitsFormatException(e.getMessage());
        }
    }

    /**
     * Parses the {@code byte[]} argument as a {@code DataBits}.
     * @param arr array of bytes
     * @return the {@code DataBits} value represented by the argument.
     * @throws DataBitsFormatException if the {@code arr[0]} does not contain a parsable {@code DataBits}.
     */
    public static @NotNull DataBits parseDataBits(byte @NotNull [] arr) throws DataBitsFormatException {
        return parseDataBits(arr, 0);
    }

    /**
     * Return this.
     * @return this {@code DataBits}.
     * @throws CloneNotSupportedException Error in clone.
     */
    @Override
    public @NotNull DataBits clone() throws CloneNotSupportedException {
        try {
            return parseDataBits(param);
        } catch (DataBitsFormatException e) {
            throw new CloneNotSupportedException();
        }
    }
}
