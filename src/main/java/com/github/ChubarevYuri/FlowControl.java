package com.github.ChubarevYuri;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A mechanism that slows down the data transmitter when the receiver is not ready.
 */
public class FlowControl implements ByteConvertedObject {

    private final byte param;

    private FlowControl(byte value) {
        this.param = value;
    }

    /**
     * No control.
     */
    public static FlowControl NONE = new FlowControl((byte) 0);

    /**
     * A hardware mechanism in which "ready/busy" signals are transmitted over separate physical communication lines.
     * The most well-known implementation is in the RS-232 interface.
     * <br> For receiver.
     */
    public static FlowControl RTSCTS_IN = new FlowControl((byte) 1);

    /**
     * A hardware mechanism in which "ready/busy" signals are transmitted over separate physical communication lines.
     * The most well-known implementation is in the RS-232 interface.
     * <br> For transmitter.
     */
    public static FlowControl RTSCTS_OUT = new FlowControl((byte) 2);

    /**
     * A software mechanism in which the "ready/busy" program flag is cocked and reset by inserting a special unique
     * sequence (XOn/XOff) into the data stream. It is used in software drivers of the RS-232 interface as an
     * alternative to hardware flow control in cases of incomplete connection cable.
     * <br> For receiver.
     */
    public static FlowControl XONXOFF_IN = new FlowControl((byte) 3);

    /**
     * A software mechanism in which the "ready/busy" program flag is cocked and reset by inserting a special unique
     * sequence (XOn/XOff) into the data stream. It is used in software drivers of the RS-232 interface as an
     * alternative to hardware flow control in cases of incomplete connection cable.
     * <br> For transmitter.
     */
    public static FlowControl XONXOFF_OUT = new FlowControl((byte) 4);

    /**
     * Return array of all valid values.
     * @return All values.
     */
    public static @NotNull FlowControl @NotNull [] all() {
        return new FlowControl[] {NONE, RTSCTS_IN, RTSCTS_OUT, XONXOFF_IN, XONXOFF_OUT};
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
        if (obj instanceof FlowControl) {
            return param == ((FlowControl) obj).param;
        }
        return false;
    }

    /**
     * Returns the number of FlowControl.
     * @return the number of FlowControl.
     */
    public int intValue() {
        return param;
    }

    /**
     * Returns a {@code String} representation of a {@code FlowControl}.
     * @return {@code String} formatted "Value".
     */
    @Override
    public @NotNull String toString() {
        return switch (param) {
            case 1 -> "RTS-CTS IN";
            case 2 -> "RTS-CTS OUT";
            case 3 -> "XON-XOFF IN";
            case 4 -> "XON-XOFF OUT";
            default -> "NONE";
        };
    }

    /**
     * Returns a {@code byte[]} representation of a {@code FlowControl}.
     * @return {@code byte[]} representation of a {@code FlowControl}.
     */
    @Override
    public byte @NotNull [] toBytes() {
        return new byte[] {param};
    }

    /**
     * Parses the {@code String} argument as a {@code FlowControl}.
     * @param s a {@code String} containing the int representation to be parsed.
     * @return the {@code FlowControl} value represented by the argument.
     * @throws ParityFormatException if the {@code String} does not contain a parsable {@code FlowControl}.
     */
    public static @NotNull FlowControl parseFlowControl(@NotNull String s) throws FlowControlFormatException {
        return switch (s.toLowerCase()) {
            case "0", "none", "n" -> NONE;
            case "1", "rts-cts in", "rtscts in", "rts-cts_in", "rtscts_in", "ri" -> RTSCTS_IN;
            case "2", "rts-cts out", "rtscts out", "rts-cts_out", "rtscts_out", "ro" -> RTSCTS_OUT;
            case "3", "xon-xoff in", "xonxoff in", "xon-xoff_in", "xonxoff_in", "xi" -> XONXOFF_IN;
            case "4", "xon-xoff out", "xonxoff out", "xon-xoff_out", "xonxoff_out", "xo" -> XONXOFF_OUT;
            default -> throw new FlowControlFormatException(s + " not parsed to FlowControl");
        };
    }

    /**
     * Parses the {@code int} argument as a {@code FlowControl}.
     * <br>Correct values = [0..4].
     * @param i a {@code int} containing the int representation to be parsed.
     * @return the {@code FlowControl} value represented by the argument.
     * @throws FlowControlFormatException If the {@code int} does not contain a parsable {@code FlowControl}.
     */
    public static @NotNull FlowControl parseFlowControl(int i) throws FlowControlFormatException {
        return switch (i) {
            case 0 -> NONE;
            case 1 -> RTSCTS_IN;
            case 2 -> RTSCTS_OUT;
            case 3 -> XONXOFF_IN;
            case 4 -> XONXOFF_OUT;
            default -> throw new FlowControlFormatException(i + " not parsed to FlowControl");
        };
    }

    /**
     * Parses the {@code byte[]} argument as a {@code FlowControl}.
     * @param arr array of bytes
     * @param startIndex start index to parsed
     * @return the {@code FlowControl} value represented by the argument.
     * @throws FlowControlFormatException if the {@code arr[startIndex]} does not contain a parsable {@code FlowControl}.
     */
    public static @NotNull FlowControl parseFlowControl(byte @NotNull [] arr, int startIndex) throws FlowControlFormatException {
        try {
            return parseFlowControl(arr[startIndex]);
        } catch (Exception e) {
            throw new FlowControlFormatException(e.getMessage());
        }
    }

    /**
     * Parses the {@code byte[]} argument as a {@code FlowControl}.
     * @param arr array of bytes
     * @return the {@code FlowControl} value represented by the argument.
     * @throws FlowControlFormatException if the {@code arr[0]} does not contain a parsable {@code FlowControl}.
     */
    public static @NotNull FlowControl parseFlowControl(byte @NotNull [] arr) throws FlowControlFormatException {
        return parseFlowControl(arr, 0);
    }

    /**
     * Return this.
     * @return this {@code BaudRate}.
     * @throws CloneNotSupportedException Error in clone.
     */
    @Override
    public @NotNull FlowControl clone() throws CloneNotSupportedException {
        try {
            return parseFlowControl(param);
        } catch (FlowControlFormatException e) {
            throw new CloneNotSupportedException();
        }
    }
}
