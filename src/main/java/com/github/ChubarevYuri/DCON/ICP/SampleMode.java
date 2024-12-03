package com.github.ChubarevYuri.DCON.ICP;

import com.github.ChubarevYuri.ByteConvertedObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SampleMode implements ByteConvertedObject {

    private final byte param;

    private SampleMode(byte value) {
        this.param = value;
    }

    /**
     * Normal mode (16 bits).
     */
    public static final SampleMode NORMAL = new SampleMode((byte) 0);

    /**
     * Fast mode (12 bits).
     */
    public static final SampleMode Fast = new SampleMode((byte) 1);

    /**
     * Return array of all valid values.
     * @return All values.
     */
    public static @NotNull SampleMode @NotNull [] all() {
        return new SampleMode[] {NORMAL, Fast};
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
        if (obj instanceof SampleMode) {
            return param == ((SampleMode) obj).param;
        }
        return false;
    }

    /**
     * Returns the number of SampleMode setting.
     * @return the number of SampleMode setting.
     */
    public int intValue() {
        return param;
    }

    /**
     * Returns a {@code boolean} representation of a {@code SampleMode}.
     * @return {@code boolean} representation of a {@code SampleMode}.
     */
    public boolean toBoolean() {
        return param > 0;
    }

    /**
     * Returns a {@code String} representation of a {@code SampleMode}.
     * @return {@code String} formatted "Value"Hz.
     */
    @Override
    public @NotNull String toString() {
        return param == 0 ? "Normal" : "Fast";
    }

    /**
     * Returns a {@code byte[]} representation of a {@code SampleMode}.
     * @return {@code byte[]} representation of a {@code SampleMode}.
     */
    @Override
    public byte @NotNull [] toBytes() {
        return new byte[] {param};
    }

    /**
     * Parses the {@code String} argument as a {@code SampleMode}.
     * <br>Correct values = [0..1] or [50, 60].
     * @param s a {@code String} containing the int representation to be parsed.
     * @return the {@code Filter} value represented by the argument.
     * @throws FilterFormatException if the {@code String} does not contain a parsable {@code SampleMode}.
     */
    public static @NotNull SampleMode parseSampleMode(@NotNull String s) throws SampleModeFormatException {
        return switch (s.toLowerCase()) {
            case "0", "n", "normal" -> NORMAL;
            case "1", "f", "fast" -> Fast;
            default -> throw new SampleModeFormatException(s + " not parsed to SampleMode");
        };
    }

    /**
     * Parses the {@code int} argument as a {@code SampleMode}.
     * <br>Correct values = [0..4].
     * @param i a {@code int} containing the int representation to be parsed.
     * @return the {@code Filter} value represented by the argument.
     * @throws FilterFormatException If the {@code int} does not contain a parsable {@code SampleMode}.
     */
    public static @NotNull SampleMode parseSampleMode(int i) throws SampleModeFormatException {
        return switch (i) {
            case 0 -> NORMAL;
            case 1 -> Fast;
            default -> throw new SampleModeFormatException(i + " not parsed to SampleMode");
        };
    }

    /**
     * Parses the {@code boolean} argument as a {@code SampleMode}.
     * @param i a {@code boolean} containing the int representation to be parsed.
     * @return the {@code SampleMode} value represented by the argument.
     */
    public static @NotNull SampleMode parseSampleMode(boolean i) {
        return i ? Fast : NORMAL;
    }

    /**
     * Parses the {@code byte[]} argument as a {@code SampleMode}.
     * @param arr array of bytes
     * @param startIndex start index to parsed
     * @return the {@code Filter} value represented by the argument.
     * @throws FilterFormatException if the {@code arr[startIndex]} does not contain a parsable {@code SampleMode}.
     */
    public static @NotNull SampleMode parseSampleMode(byte @NotNull [] arr, int startIndex) throws SampleModeFormatException {
        try {
            return parseSampleMode(arr[startIndex]);
        } catch (Exception e) {
            throw new SampleModeFormatException(e.getMessage());
        }
    }

    /**
     * Parses the {@code byte[]} argument as a {@code SampleMode}.
     * @param arr array of bytes
     * @return the {@code Filter} value represented by the argument.
     * @throws FilterFormatException if the {@code arr[0]} does not contain a parsable {@code SampleMode}.
     */
    public static @NotNull SampleMode parseSampleMode(byte @NotNull [] arr) throws SampleModeFormatException  {
        return parseSampleMode(arr, 0);
    }

    /**
     * Return this.
     * @return this {@code SampleMode}.
     * @throws CloneNotSupportedException Error in clone.
     */
    @Override
    public @NotNull SampleMode clone() throws CloneNotSupportedException {
        try {
            return parseSampleMode(param);
        } catch (SampleModeFormatException e) {
            throw new CloneNotSupportedException();
        }
    }
}
