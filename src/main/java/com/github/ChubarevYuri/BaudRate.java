package com.github.ChubarevYuri;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The number of transmitted bits per second
 */
public class BaudRate implements Comparable<BaudRate>, ByteConvertedObject {

    private final byte param;

    private BaudRate(byte value) {
        this.param = value;
    }

    /**
     * 110 bits per second.
     */
    public static final BaudRate BPS110 = new BaudRate((byte) 0);

    /**
     * 300 bits per second.
     */
    public static final BaudRate BPS300 = new BaudRate((byte) 1);

    /**
     * 600 bits per second.
     */
    public static final BaudRate BPS600 = new BaudRate((byte) 2);

    /**
     * 1200 bits per second.
     */
    public static final BaudRate BPS1200 = new BaudRate((byte) 3);

    /**
     * 2400 bits per second.
     */
    public static final BaudRate BPS2400 = new BaudRate((byte) 4);

    /**
     * 4800 bits per second.
     */
    public static final BaudRate BPS4800 = new BaudRate((byte) 5);

    /**
     * 9600 bits per second.
     */
    public static final BaudRate BPS9600 = new BaudRate((byte) 6);

    /**
     * 14400 bits per second.
     */
    public static final BaudRate BPS14400 = new BaudRate((byte) 7);

    /**
     * 19200 bits per second.
     */
    public static final BaudRate BPS19200 = new BaudRate((byte) 8);

    /**
     * 38400 bits per second.
     */
    public static final BaudRate BPS38400 = new BaudRate((byte) 9);

    /**
     * 57600 bits per second.
     */
    public static final BaudRate BPS57600 = new BaudRate((byte) 10);

    /**
     * 115200 bits per second.
     */
    public static final BaudRate BPS115200 = new BaudRate((byte) 11);

    /**
     * 128000 bits per second.
     */
    public static final BaudRate BPS128000 = new BaudRate((byte) 12);

    /**
     * 256000 bits per second.
     */
    public static final BaudRate BPS256000 = new BaudRate((byte) 13);

    /**
     * Return array of all valid values.
     * @return All values.
     */
    public static @NotNull BaudRate @NotNull [] all() {
        return new BaudRate[] {
                BPS110,
                BPS300,
                BPS600,
                BPS1200,
                BPS2400,
                BPS4800,
                BPS9600,
                BPS14400,
                BPS19200,
                BPS38400,
                BPS57600,
                BPS115200,
                BPS128000,
                BPS256000
        };
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
        if (obj instanceof BaudRate) {
            return param == ((BaudRate) obj).param;
        }
        return false;
    }

    /**
     * Compares {@code this} with the specified {@code BaudRate} for order. Returns a negative integer, zero, or a
     * positive integer as {@code this} is less than, equal to, or greater than the specified {@code BaudRate}.
     * @param o the {@code BaudRate} to be compared.
     * @return a negative integer, zero, or a positive integer as {@code this} is less than, equal to, or greater than
     * the specified {@code BaudRate}.
     */
    @Override
    public int compareTo(@NotNull BaudRate o) {
        return param - o.param;
    }

    /**
     * Returns the number of transmitted bits per second.
     * @return the number of transmitted bits per second.
     */
    public int intValue() {
        return switch (param){
            case 0 -> 110;
            case 1 -> 300;
            case 2 -> 600;
            case 3 -> 1200;
            case 4 -> 2400;
            case 5 -> 4800;
            case 7 -> 14400;
            case 8 -> 19200;
            case 9 -> 38400;
            case 10 -> 57600;
            case 11 -> 115200;
            case 12 -> 128000;
            case 13 -> 256000;
            default -> 9600;
        };
    }

    /**
     * Returns a {@code String} representation of a {@code BaudRate}.
     * @return {@code String} formatted "Value".
     */
    @Override
    public @NotNull String toString() {
        return switch (param) {
            case 0 -> "110bps";
            case 1 -> "300bps";
            case 2 -> "600bps";
            case 3 -> "1200bps";
            case 4 -> "2400bps";
            case 5 -> "4800bps";
            case 7 -> "14400bps";
            case 8 -> "19200bps";
            case 9 -> "38400bps";
            case 10 -> "57600bps";
            case 11 -> "115200bps";
            case 12 -> "128000bps";
            case 13 -> "256000bps";
            default -> "9600bps";
        };
    }

    /**
     * Returns a {@code byte[]} representation of a {@code BaudRate}.
     * @return {@code byte[]} representation of a {@code BaudRate}.
     */
    @Override
    public byte @NotNull [] toBytes() {
        return new byte[] {param};
    }

    /**
     * Parses the {@code String} argument as a {@code BaudRate}.
     * <br>Correct values = [110, 300, 600, 1200, 2400, 4800, 9600, 14400, 19200, 38400, 57600, 115200, 128000, 256000].
     * After value can be "bps".
     * @param s a {@code String} containing the int representation to be parsed.
     * @return the {@code BaudRate} value represented by the argument.
     * @throws BaudRateFormatException if the {@code String} does not contain a parsable {@code BaudRate}.
     */
    public static @NotNull BaudRate parseBaudRate(@NotNull String s) throws BaudRateFormatException {
        return switch (s.toLowerCase()) {
            case "110bps", "110" -> BPS110;
            case "300bps", "300" -> BPS300;
            case "600bps", "600" -> BPS600;
            case "1200bps", "1200" -> BPS1200;
            case "2400bps", "2400" -> BPS2400;
            case "4800bps", "4800" -> BPS4800;
            case "9600bps", "9600" -> BPS9600;
            case "14400bps", "14400" -> BPS14400;
            case "19200bps", "19200" -> BPS19200;
            case "38400bps", "38400" -> BPS38400;
            case "57600bps", "57600" -> BPS57600;
            case "115200bps", "115200" -> BPS115200;
            case "128000bps", "128000" -> BPS128000;
            case "256000bps", "256000" -> BPS256000;
            default -> throw new BaudRateFormatException(s + " not parsed to BaudRate");
        };
    }

    /**
     * Parses the {@code byte} argument as a {@code BaudRate}.
     * <br>Correct values = [0..13].
     * @param b a {@code byte} containing the int representation to be parsed.
     * @return the {@code BaudRate} value represented by the argument.
     * @throws BaudRateFormatException If the {@code byte} does not contain a parsable {@code BaudRate}.
     */
    public static @NotNull BaudRate parseBaudRate(byte b) throws BaudRateFormatException {
        return switch (b) {
            case 0 -> BPS110;
            case 1 -> BPS300;
            case 2 -> BPS600;
            case 3 -> BPS1200;
            case 4 -> BPS2400;
            case 5 -> BPS4800;
            case 6 -> BPS9600;
            case 7 -> BPS14400;
            case 8 -> BPS19200;
            case 9 -> BPS38400;
            case 10 -> BPS57600;
            case 11 -> BPS115200;
            case 12 -> BPS128000;
            case 13 -> BPS256000;
            default -> throw new BaudRateFormatException(b + " not parsed to BaudRate");
        };
    }

    /**
     * Parses the {@code int} argument as a {@code BaudRate}.
     * <br>Correct values = [110, 300, 600, 1200, 2400, 4800, 9600, 14400, 19200, 38400, 57600, 115200, 128000, 256000].
     * @param i a {@code int} containing the int representation to be parsed.
     * @return the {@code BaudRate} value represented by the argument.
     * @throws BaudRateFormatException If the {@code int} does not contain a parsable {@code BaudRate}.
     */
    public static @NotNull BaudRate parseBaudRate(int i) throws BaudRateFormatException {
        return switch (i) {
            case 110 -> BPS110;
            case 300 -> BPS300;
            case 600 -> BPS600;
            case 1200 -> BPS1200;
            case 2400 -> BPS2400;
            case 4800 -> BPS4800;
            case 9600 -> BPS9600;
            case 14400 -> BPS14400;
            case 19200 -> BPS19200;
            case 38400 -> BPS38400;
            case 57600 -> BPS57600;
            case 115200 -> BPS115200;
            case 122000 -> BPS128000;
            case 256000 -> BPS256000;
            default -> throw new BaudRateFormatException(i + " not parsed to BaudRate");
        };
    }

    /**
     * Parses the {@code byte[]} argument as a {@code BaudRate}.
     * @param arr array of bytes
     * @param startIndex start index to parsed
     * @return the {@code BaudRate} value represented by the argument.
     * @throws BaudRateFormatException if the {@code arr[startIndex]} does not contain a parsable {@code BaudRate}.
     */
    public static @NotNull BaudRate parseBaudRate(byte @NotNull [] arr, int startIndex) throws BaudRateFormatException {
        try {
            return parseBaudRate(arr[startIndex]);
        } catch (Exception e) {
            throw new BaudRateFormatException(e.getMessage());
        }
    }

    /**
     * Parses the {@code byte[]} argument as a {@code BaudRate}.
     * @param arr array of bytes
     * @return the {@code BaudRate} value represented by the argument.
     * @throws BaudRateFormatException if the {@code arr[0]} does not contain a parsable {@code BaudRate}.
     */
    public static @NotNull BaudRate parseBaudRate(byte @NotNull [] arr) throws BaudRateFormatException {
        return parseBaudRate(arr, 0);
    }

    /**
     * Return this.
     * @return this {@code BaudRate}.
     * @throws CloneNotSupportedException Error in clone.
     */
    @Override
    public @NotNull BaudRate clone() throws CloneNotSupportedException {
        try {
            return parseBaudRate(param);
        } catch (BaudRateFormatException e) {
            throw new CloneNotSupportedException();
        }
    }

    /**
     * Return how long nanoseconds does it take to send 1 byte.
     * @return How long nanoseconds does it take to send 1 byte.
     */
    public int nanosecondsPerByte() {
        return switch (param) {
            case 0 -> 90910; //110
            case 1 -> 33334; //300
            case 2 -> 16667; //600
            case 3 -> 8334; //1200
            case 4 -> 4167; //2400
            case 5 -> 2084; //4800
            case 7 -> 695; //14400
            case 8 -> 521; //19200
            case 9 -> 261; //38400
            case 10 -> 174; //57600
            case 11 -> 87; //115200
            case 12 -> 79; //128000
            case 13 -> 40; //256000
            default -> 1042; //9600
        };
    }
}
