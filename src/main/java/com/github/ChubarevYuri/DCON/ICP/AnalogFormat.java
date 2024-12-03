package com.github.ChubarevYuri.DCON.ICP;

import com.github.ChubarevYuri.ByteConvertedObject;
import com.github.ChubarevYuri.DataBits;
import com.github.ChubarevYuri.DataBitsFormatException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Format of analog channels.
 */
public class AnalogFormat implements ByteConvertedObject {

    private final byte param;

    private AnalogFormat(byte value) {
        this.param = value;
    }

    /**
     * Engineering unit.
     */
    public final static AnalogFormat Engineering = new AnalogFormat((byte)0);

    /**
     * % of FSR (full scale range)
     */
    public final static AnalogFormat Percent = new AnalogFormat((byte)1);

    /**
     * 2‟s complement hexadecimal.
     */
    public final static AnalogFormat Hex = new AnalogFormat((byte)2);

    /**
     * Return array of all valid values.
     * @return All values.
     */
    public static @NotNull AnalogFormat @NotNull [] all() {
        return new AnalogFormat[] {Engineering, Percent, Hex};
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
        if (obj instanceof AnalogFormat) {
            return param == ((AnalogFormat) obj).param;
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
        return switch (param) {
            case 1 -> "Percent";
            case 2 -> "Hex";
            default -> "Engineering";
        };
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
     * Parses the {@code String} argument as a {@code AnalogFormat}.
     * @param s a {@code String} containing the int representation to be parsed.
     * @return the {@code AnalogFormat} value represented by the argument.
     * @throws AnalogFormatFormatException if the {@code String} does not contain a parsable {@code AnalogFormat}.
     */
    public static @NotNull AnalogFormat parseAnalogFormat(@NotNull String s) throws AnalogFormatFormatException {
        return switch (s.toLowerCase()) {
            case "engineering", "e", "0", "00" -> Engineering;
            case "percent", "p", "1", "01" -> Percent;
            case "hex", "h", "2", "10" -> Hex;
            default -> throw new AnalogFormatFormatException(s + " not parsed to AnalogFormat");
        };
    }

    /**
     * Parses the {@code int} argument as a {@code AnalogFormat}.
     * @param i a {@code int} containing the int representation to be parsed.
     * @return the {@code AnalogFormat} value represented by the argument.
     * @throws AnalogFormatFormatException If the {@code int} does not contain a parsable {@code AnalogFormat}.
     */
    public static @NotNull AnalogFormat parseAnalogFormat(int i) throws AnalogFormatFormatException {
        return switch (i) {
            case 0 -> Engineering;
            case 1 -> Percent;
            case 2 -> Hex;
            default -> throw new DataBitsFormatException(i + " not parsed to AnalogFormat");
        };
    }

    /**
     * Parses the {@code byte[]} argument as a {@code AnalogFormat}.
     * @param arr array of bytes
     * @param startIndex start index to parsed
     * @return the {@code AnalogFormat} value represented by the argument.
     * @throws AnalogFormatFormatException if the {@code arr[startIndex]} does not contain a parsable {@code AnalogFormat}.
     */
    public static @NotNull AnalogFormat parseAnalogFormat(byte @NotNull [] arr, int startIndex) throws AnalogFormatFormatException {
        try {
            return parseAnalogFormat(arr[startIndex]);
        } catch (Exception e) {
            throw new DataBitsFormatException(e.getMessage());
        }
    }

    /**
     * Parses the {@code byte[]} argument as a {@code AnalogFormat}.
     * @param arr array of bytes
     * @return the {@code AnalogFormat} value represented by the argument.
     * @throws AnalogFormatFormatException if the {@code arr[0]} does not contain a parsable {@code AnalogFormat}.
     */
    public static @NotNull AnalogFormat parseAnalogFormat(byte @NotNull [] arr) throws AnalogFormatFormatException {
        return parseAnalogFormat(arr, 0);
    }

    /**
     * Return this.
     * @return this {@code AnalogFormat}.
     * @throws CloneNotSupportedException Error in clone.
     */
    @Override
    public @NotNull AnalogFormat clone() throws CloneNotSupportedException {
        try {
            return parseAnalogFormat(param);
        } catch (DataBitsFormatException e) {
            throw new CloneNotSupportedException();
        }
    }
}
