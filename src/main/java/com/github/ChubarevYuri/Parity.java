package com.github.ChubarevYuri;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parity control protocol.
 */
public class Parity implements ByteConvertedObject {

    private final byte param;

    private Parity(byte value) {
        this.param = value;
    }

    /**
     * Parity control is not performed.
     */
    public static final Parity NONE = new Parity((byte) 0);

    /**
     * Sets the parity bit so that the number of set bits is always odd.
     */
    public static final Parity ODD = new Parity((byte) 1);

    /**
     * Sets the parity bit so that the number of set bits is always even.
     */
    public static final Parity EVEN = new Parity((byte) 2);

    /**
     * Leaves the parity bit equal to 1.
     */
    public static final Parity MARK = new Parity((byte) 3);

    /**
     * Leaves the parity bit equal to 0.
     */
    public static final Parity SPACE = new Parity((byte) 4);

    /**
     * Return array of all valid values.
     * @return All values.
     */
    public static @NotNull Parity @NotNull [] all() {
        return new Parity[] {NONE, ODD, EVEN, MARK, SPACE};
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
        if (obj instanceof Parity) {
            return param == ((Parity) obj).param;
        }
        return false;
    }

    /**
     * Returns the number of parity control protocol.
     * @return the number of parity control protocol.
     */
    public int intValue() {
        return param;
    }

    /**
     * Returns a {@code String} representation of a {@code Parity}.
     * @return {@code String} formatted "Value".
     */
    @Override
    public @NotNull String toString() {
        return switch (param) {
            case 1 -> "Odd";
            case 2 -> "Even";
            case 3 -> "Mark";
            case 4 -> "Space";
            default -> "None";
        };
    }

    /**
     * Returns a {@code byte[]} representation of a {@code Parity}.
     * @return {@code byte[]} representation of a {@code Parity}.
     */
    @Override
    public byte @NotNull [] toBytes() {
        return new byte[] {param};
    }

    /**
     * Parses the {@code String} argument as a {@code Parity}.
     * <br>Correct values = [0..4] or typename or first char of typename.
     * @param s a {@code String} containing the int representation to be parsed.
     * @return the {@code Parity} value represented by the argument.
     * @throws ParityFormatException if the {@code String} does not contain a parsable {@code Parity}.
     */
    public static @NotNull Parity parseParity(@NotNull String s) throws ParityFormatException {
        return switch (s.toLowerCase()) {
            case "0", "n", "none" -> NONE;
            case "1", "o", "odd" -> ODD;
            case "2", "e", "even" -> EVEN;
            case "3", "m", "mark" -> MARK;
            case "4", "s", "space" -> SPACE;
            default -> throw new ParityFormatException(s + " not parsed to Parity");
        };
    }

    /**
     * Parses the {@code int} argument as a {@code Parity}.
     * <br>Correct values = [0..4].
     * @param i a {@code int} containing the int representation to be parsed.
     * @return the {@code Parity} value represented by the argument.
     * @throws ParityFormatException If the {@code int} does not contain a parsable {@code Parity}.
     */
    public static @NotNull Parity parseParity(int i) throws ParityFormatException {
        return switch (i) {
            case 0 -> NONE;
            case 1 -> ODD;
            case 2 -> EVEN;
            case 3 -> MARK;
            case 4 -> SPACE;
            default -> throw new ParityFormatException(i + " not parsed to Parity");
        };
    }

    /**
     * Parses the {@code byte[]} argument as a {@code Parity}.
     * @param arr array of bytes
     * @param startIndex start index to parsed
     * @return the {@code Parity} value represented by the argument.
     * @throws ParityFormatException if the {@code arr[startIndex]} does not contain a parsable {@code Parity}.
     */
    public static @NotNull Parity parseParity(byte @NotNull [] arr, int startIndex) throws ParityFormatException {
        try {
            return parseParity(arr[startIndex]);
        } catch (Exception e) {
            throw new ParityFormatException(e.getMessage());
        }
    }

    /**
     * Parses the {@code byte[]} argument as a {@code Parity}.
     * @param arr array of bytes
     * @return the {@code Parity} value represented by the argument.
     * @throws ParityFormatException if the {@code arr[0]} does not contain a parsable {@code Parity}.
     */
    public static @NotNull Parity parseParity(byte @NotNull [] arr) throws ParityFormatException  {
        return parseParity(arr, 0);
    }

    /**
     * Return this.
     * @return this {@code Parity}.
     * @throws CloneNotSupportedException Error in clone.
     */
    @Override
    public @NotNull Parity clone() throws CloneNotSupportedException {
        try {
            return parseParity(param);
        } catch (ParityFormatException e) {
            throw new CloneNotSupportedException();
        }
    }
}
