package com.github.ChubarevYuri.Checksum;

import com.github.ChubarevYuri.ParityFormatException;
import com.github.ChubarevYuri.UByte;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 *Algorithm for calculating the checksum in the Modbus RTU protocol.
 */
public class CRC16 extends CHK<byte[]> {

    private CRC16(boolean enable) {
        super(enable);
    }

    /**
     * Checksum control is not performed.
     */
    public static final CRC16 DISABLE = new CRC16(false);

    /**
     * Checksum control is performed.
     */
    public static final CRC16 ENABLE = new CRC16(true);

    /**
     * Return array of all valid values.
     * @return All values.
     */
    public static @NotNull CRC16 @NotNull [] all() {
        return new CRC16[] {DISABLE, ENABLE};
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
        if (obj instanceof CRC16) {
            return super.equals(obj);
        }
        return false;
    }

    /**
     * Parses the {@code String} argument as a {@code CRC16}.
     * <br> {@code DISABLE} correct values = "0", "d", "disable", "f", "false".
     * <br> {@code ENABLE} correct values = "1", "e", "enable", "t", "true".
     * @param s a {@code String} containing the int representation to be parsed.
     * @return the {@code Parity} value represented by the argument.
     * @throws ChkFormatException if the {@code String} does not contain a parsable {@code CRC16}.
     */
    public static @NotNull CRC16 parseCRC16(@NotNull String s) throws ChkFormatException {
        return switch (s.toLowerCase()) {
            case "0", "d", "disable", "f", "false" -> DISABLE;
            case "1", "e", "enable", "t", "true" -> ENABLE;
            default -> throw new ChkFormatException(s + " not parsed to LRC8");
        };
    }

    /**
     * Parses the {@code boolean} argument as a {@code CRC16}.
     * @param i a {@code boolean} containing the int representation to be parsed.
     * @return the {@code CRC16} value represented by the argument.
     */
    public static @NotNull CRC16 parseCRC16(boolean i) {
        return i ? ENABLE : DISABLE;
    }

    /**
     * Parses the {@code byte[]} argument as a {@code CRC16}.
     * @param arr array of bytes
     * @param startIndex start index to parsed
     * @return the {@code CRC16} value represented by the argument.
     * @throws ParityFormatException if the {@code arr[startIndex]} does not contain a parsable {@code CRC16}.
     */
    public static @NotNull CRC16 parseCRC16(byte @NotNull [] arr, int startIndex) throws ParityFormatException {
        try {
            return arr[startIndex] == 0 ? DISABLE : ENABLE;
        } catch (Exception e) {
            throw new ParityFormatException(e.getMessage());
        }
    }

    /**
     * Parses the {@code byte[]} argument as a {@code CRC16}.
     * @param arr array of bytes
     * @return the {@code Parity} value represented by the argument.
     * @throws ParityFormatException if the {@code arr[0]} does not contain a parsable {@code CRC16}.
     */
    public static @NotNull CRC16 parseCRC16(byte @NotNull [] arr) throws ParityFormatException  {
        return parseCRC16(arr, 0);
    }

    @Override
    protected byte @NotNull [] calculate(byte @NotNull [] v) {
        int crc = 0x0000FFFF;
        for (byte b : v) {
            crc ^= b & 0x000000ff;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x00000001) != 0) {
                    crc >>= 1;
                    crc ^= 0x0000A001;
                } else {
                    crc >>= 1;
                }
            }
        }
        byte[] result = new byte[4];
        ByteBuffer.wrap(result).putInt(crc);
        byte[] s = new byte[2];
        s[0] = result[3];
        s[1] = result[2];
        return s;
    }

    @Override
    public boolean control(byte @NotNull [] v) {
        if (!toBoolean()) {
            return true;
        } else {
            if (v.length < 2) {
                return false;
            }
            byte[] crc = new byte[2];
            System.arraycopy(v, v.length - 2, crc, 0, 2);

            byte[] body = new byte[v.length - 2];
            System.arraycopy(v, 0, body, 0, body.length);

            byte[] checker = calculate(body);

            for (int i = 0; i < crc.length; i++) {
                if (crc[i] != checker[i]) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public byte @NotNull [] add(byte @NotNull [] v) {
        byte[] crc = calculate(v);

        byte[] result = new byte[v.length + crc.length];

        System.arraycopy(v, 0, result, 0, v.length);

        System.arraycopy(crc, 0, result, v.length, crc.length);

        return result;
    }

    /**
     * Control checksum in data and remove checksum.
     * @param v data with checksum.
     * @return data without checksum.
     * @throws ChkControlException if {@code control()} return false.
     */
    @Override
    public byte @NotNull [] controlAndRemove(byte @NotNull [] v) throws ChkControlException {
        if (!toBoolean()) {
            return v;
        } else if (!control(v)) {
            throw new ChkControlException("Uncorrect checksum in " + UByte.convertToString(v));
        } else {
            byte[] result = new byte[v.length - 2];
            System.arraycopy(v, 0, result, 0, result.length);
            return result;
        }
    }
}
