package com.github.ChubarevYuri.Checksum;

import com.github.ChubarevYuri.ParityFormatException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *Algorithm for calculating the checksum in the DCON protocol.
 */
public class LRC8 extends CHK<String> {

    private LRC8(boolean enable) {
        super(enable);
    }

    /**
     * Checksum control is not performed.
     */
    public static final LRC8 DISABLE = new LRC8(false);

    /**
     * Checksum control is performed.
     */
    public static final LRC8 ENABLE = new LRC8(true);

    /**
     * Return array of all valid values.
     * @return All values.
     */
    public static @NotNull LRC8 @NotNull [] all() {
        return new LRC8[] {DISABLE, ENABLE};
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
        if (obj instanceof LRC8) {
            return super.equals(obj);
        }
        return false;
    }

    /**
     * Parses the {@code String} argument as a {@code LRC8}.
     * <br> {@code DISABLE} correct values = "0", "d", "disable", "f", "false".
     * <br> {@code ENABLE} correct values = "1", "e", "enable", "t", "true".
     * @param s a {@code String} containing the int representation to be parsed.
     * @return the {@code Parity} value represented by the argument.
     * @throws ChkFormatException if the {@code String} does not contain a parsable {@code LRC8}.
     */
    public static @NotNull LRC8 parseLRC8(@NotNull String s) throws ChkFormatException {
        return switch (s.toLowerCase()) {
            case "0", "d", "disable", "f", "false" -> DISABLE;
            case "1", "e", "enable", "t", "true" -> ENABLE;
            default -> throw new ChkFormatException(s + " not parsed to LRC8");
        };
    }

    /**
     * Parses the {@code boolean} argument as a {@code LRC8}.
     * @param i a {@code boolean} containing the int representation to be parsed.
     * @return the {@code LRC8} value represented by the argument.
     */
    public static @NotNull LRC8 parseLRC8(boolean i) {
        return i ? ENABLE : DISABLE;
    }

    /**
     * Parses the {@code byte[]} argument as a {@code LRC8}.
     * @param arr array of bytes
     * @param startIndex start index to parsed
     * @return the {@code LRC8} value represented by the argument.
     * @throws ParityFormatException if the {@code arr[startIndex]} does not contain a parsable {@code LRC8}.
     */
    public static @NotNull LRC8 parseLRC8(byte @NotNull [] arr, int startIndex) throws ParityFormatException {
        try {
            return arr[startIndex] == 0 ? DISABLE : ENABLE;
        } catch (Exception e) {
            throw new ParityFormatException(e.getMessage());
        }
    }

    /**
     * Parses the {@code byte[]} argument as a {@code LRC8}.
     * @param arr array of bytes
     * @return the {@code Parity} value represented by the argument.
     * @throws ParityFormatException if the {@code arr[0]} does not contain a parsable {@code LRC8}.
     */
    public static @NotNull LRC8 parseLRC8(byte @NotNull [] arr) throws ParityFormatException  {
        return parseLRC8(arr, 0);
    }

    @Override
    protected @NotNull String calculate(@NotNull String v) {
        int cs = 0;
        for (char c : v.toCharArray()) {
            cs += (byte) c;
        }
        cs %= 256;
        return "%02X".formatted(cs);
    }

    @Override
    public boolean control(@NotNull String v) {
        if (!toBoolean()) {
            return true;
        } else {
            if (v.length() < 2) {
                return false;
            }
            return v.substring(v.length() - 2).equals(calculate(v.substring(0, v.length() - 2)));
        }
    }

    @Override
    public @NotNull String add(@NotNull String v) {
        return toBoolean() ? v + calculate(v) : v;
    }

    /**
     * Control checksum in data and remove checksum.
     * @param v data with checksum.
     * @return data without checksum.
     * @throws ChkControlException if {@code control()} return false.
     */
    @Override
    public @NotNull String controlAndRemove(@NotNull String v) throws ChkControlException {
        if (!toBoolean()) {
            return v;
        } else if (!control(v)) {
            throw new ChkControlException("Uncorrect checksum in " + v);
        } else {
            return v.substring(0, v.length() - 2);
        }
    }


}
