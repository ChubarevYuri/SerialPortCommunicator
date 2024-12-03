package com.github.ChubarevYuri.DCON.ICP;

import com.github.ChubarevYuri.ByteConvertedObject;
import com.github.ChubarevYuri.Checksum.LRC8;
import com.github.ChubarevYuri.Parity;
import com.github.ChubarevYuri.ParityFormatException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Filter settings.
 */
public class Filter implements ByteConvertedObject {

    private final byte param;

    private Filter(byte value) {
        this.param = value;
    }

    /**
     * 60Hz rejection.
     */
    public static final Filter Hz60 = new Filter((byte) 0);

    /**
     * 50Hz rejection.
     */
    public static final Filter Hz50 = new Filter((byte) 1);

    /**
     * Return array of all valid values.
     * @return All values.
     */
    public static @NotNull Filter @NotNull [] all() {
        return new Filter[] {Hz60, Hz50};
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
        if (obj instanceof Filter) {
            return param == ((Filter) obj).param;
        }
        return false;
    }

    /**
     * Returns the number of filter setting.
     * @return the number of filter setting.
     */
    public int intValue() {
        return param;
    }

    /**
     * Returns a {@code boolean} representation of a {@code Filter}.
     * @return {@code boolean} representation of a {@code Filter}.
     */
    public boolean toBoolean() {
        return param > 0;
    }

    /**
     * Returns a {@code String} representation of a {@code Filter}.
     * @return {@code String} formatted "Value"Hz.
     */
    @Override
    public @NotNull String toString() {
        return param == 0 ? "60Hz" : "50Hz";
    }

    /**
     * Returns a {@code byte[]} representation of a {@code Filter}.
     * @return {@code byte[]} representation of a {@code Filter}.
     */
    @Override
    public byte @NotNull [] toBytes() {
        return new byte[] {param};
    }

    /**
     * Parses the {@code String} argument as a {@code Filter}.
     * <br>Correct values = [0..1] or [50, 60].
     * @param s a {@code String} containing the int representation to be parsed.
     * @return the {@code Filter} value represented by the argument.
     * @throws FilterFormatException if the {@code String} does not contain a parsable {@code Filter}.
     */
    public static @NotNull Filter parseFilter(@NotNull String s) throws FilterFormatException {
        return switch (s.toLowerCase()) {
            case "0", "60", "60hz" -> Hz60;
            case "1", "50", "50hz" -> Hz50;
            default -> throw new FilterFormatException(s + " not parsed to Filter");
        };
    }

    /**
     * Parses the {@code int} argument as a {@code Filter}.
     * <br>Correct values = [0..4].
     * @param i a {@code int} containing the int representation to be parsed.
     * @return the {@code Filter} value represented by the argument.
     * @throws FilterFormatException If the {@code int} does not contain a parsable {@code Filter}.
     */
    public static @NotNull Filter parseFilter(int i) throws FilterFormatException {
        return switch (i) {
            case 0 -> Hz60;
            case 1 -> Hz50;
            default -> throw new FilterFormatException(i + " not parsed to Filter");
        };
    }

    /**
     * Parses the {@code boolean} argument as a {@code Filter}.
     * @param i a {@code boolean} containing the int representation to be parsed.
     * @return the {@code Filter} value represented by the argument.
     */
    public static @NotNull Filter parseFilter(boolean i) {
        return i ? Hz50 : Hz60;
    }

    /**
     * Parses the {@code byte[]} argument as a {@code Filter}.
     * @param arr array of bytes
     * @param startIndex start index to parsed
     * @return the {@code Filter} value represented by the argument.
     * @throws FilterFormatException if the {@code arr[startIndex]} does not contain a parsable {@code Filter}.
     */
    public static @NotNull Filter parseFilter(byte @NotNull [] arr, int startIndex) throws FilterFormatException {
        try {
            return parseFilter(arr[startIndex]);
        } catch (Exception e) {
            throw new FilterFormatException(e.getMessage());
        }
    }

    /**
     * Parses the {@code byte[]} argument as a {@code Filter}.
     * @param arr array of bytes
     * @return the {@code Filter} value represented by the argument.
     * @throws FilterFormatException if the {@code arr[0]} does not contain a parsable {@code Filter}.
     */
    public static @NotNull Filter parseFilter(byte @NotNull [] arr) throws FilterFormatException  {
        return parseFilter(arr, 0);
    }

    /**
     * Return this.
     * @return this {@code Filter}.
     * @throws CloneNotSupportedException Error in clone.
     */
    @Override
    public @NotNull Filter clone() throws CloneNotSupportedException {
        try {
            return parseFilter(param);
        } catch (FilterFormatException e) {
            throw new CloneNotSupportedException();
        }
    }
}
