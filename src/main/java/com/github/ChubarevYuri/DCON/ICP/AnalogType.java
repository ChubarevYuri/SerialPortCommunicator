package com.github.ChubarevYuri.DCON.ICP;

import com.github.ChubarevYuri.ByteConvertedObject;
import com.github.ChubarevYuri.Parity;
import com.github.ChubarevYuri.ParityFormatException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Type of analog channel.
 */
public class AnalogType implements ByteConvertedObject {

    private final byte param;

    private AnalogType(byte value) {
        this.param = value;
    }

    /**
     * -15 .. +15 mV
     */
    public static final AnalogType M15_P15_mV = new AnalogType((byte) 0);
    /**
     * -50 .. +50 mV
     */
    public static final AnalogType M50_P50_mV = new AnalogType((byte) 1);
    /**
     * -100 .. +100 mV
     */
    public static final AnalogType M100_P100_mV = new AnalogType((byte) 2);
    /**
     * -500m .. +500 mV
     */
    public static final AnalogType M500_P500_mV = new AnalogType((byte) 3);
    /**
     * -1 .. +1 V
     */
    public static final AnalogType M1_P1_V = new AnalogType((byte) 4);
    /**
     * -2.5 .. +2.5 V
     */
    public static final AnalogType M2_5_P2_5_V = new AnalogType((byte) 5);
    /**
     * -20 .. +20 mA
     */
    public static final AnalogType M20_P20_mA = new AnalogType((byte) 6);
    /**
     * +4 .. +20 mA
     */
    public static final AnalogType P4_P20_mA = new AnalogType((byte) 7);
    /**
     * -10 .. +10 V
     */
    public static final AnalogType M10_P10_V = new AnalogType((byte) 8);
    /**
     * -5 .. +5 V
     */
    public static final AnalogType M5_P5_V = new AnalogType((byte) 9);
    /**
     * -150 .. +150 mV
     */
    public static final AnalogType M150_P150_mV = new AnalogType((byte) 12);
    /**
     * -210 .. +760 °C
     */
    public static final AnalogType Thermocouple_J = new AnalogType((byte) 14);
    /**
     * -270 .. +1372 °C
     */
    public static final AnalogType Thermocouple_K = new AnalogType((byte) 15);
    /**
     * -270 .. +400 °C
     */
    public static final AnalogType Thermocouple_T = new AnalogType((byte) 16);
    /**
     * -270 .. +1000 °C
     */
    public static final AnalogType Thermocouple_E = new AnalogType((byte) 17);
    /**
     * 0 .. +1768 °C
     */
    public static final AnalogType Thermocouple_R = new AnalogType((byte) 18);
    /**
     * 0 .. +1768 °C
     */
    public static final AnalogType Thermocouple_S = new AnalogType((byte) 19);
    /**
     * 0 .. +1820 °C
     */
    public static final AnalogType Thermocouple_B = new AnalogType((byte) 20);
    /**
     * -270 .. +1300 °C
     */
    public static final AnalogType Thermocouple_N = new AnalogType((byte) 21);
    /**
     * 0 .. +2320 °C
     */
    public static final AnalogType Thermocouple_C = new AnalogType((byte) 22);
    /**
     * -200 .. +800 °C
     */
    public static final AnalogType Thermocouple_L = new AnalogType((byte) 23);
    /**
     * -200 .. +100 °C
     */
    public static final AnalogType Thermocouple_M = new AnalogType((byte) 24);
    /**
     * -200 .. +900 °C
     */
    public static final AnalogType Thermocouple_L_DIN43710 = new AnalogType((byte) 25);
    /**
     * -150 .. +150 V
     */
    public static final AnalogType M150_P150_V = new AnalogType((byte) 27);
    /**
     * -50 .. +50 V
     */
    public static final AnalogType M50_P50_V = new AnalogType((byte) 28);
    /**
     * 0 .. +20mA
     */
    public static final AnalogType ZERO_P20_mA = new AnalogType((byte) 48);
    /**
     * 0 .. +10 V
     */
    public static final AnalogType ZERO_P10_V = new AnalogType((byte) 50);
    /**
     * 0 .. +5 V
     */
    public static final AnalogType ZERO_P5_V = new AnalogType((byte) 52);

    /**
     * Return array of all valid values.
     * @return All values.
     */
    public static @NotNull AnalogType @NotNull [] all() {
        return new AnalogType[] {
                M15_P15_mV,
                M50_P50_mV,
                M100_P100_mV,
                M500_P500_mV,
                M1_P1_V,
                M2_5_P2_5_V,
                M20_P20_mA,
                P4_P20_mA,
                M10_P10_V,
                M5_P5_V,
                M150_P150_mV,
                Thermocouple_J,
                Thermocouple_K,
                Thermocouple_T,
                Thermocouple_E,
                Thermocouple_R,
                Thermocouple_S,
                Thermocouple_B,
                Thermocouple_N,
                Thermocouple_C,
                Thermocouple_L,
                Thermocouple_M,
                Thermocouple_L_DIN43710,
                M150_P150_V,
                M50_P50_V,
                ZERO_P20_mA,
                ZERO_P10_V,
                ZERO_P5_V
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
        if (obj instanceof AnalogType) {
            return param == ((AnalogType) obj).param;
        }
        return false;
    }

    /**
     * Returns the number of {@code AnalogType}.
     * @return the number of {@code AnalogType}.
     */
    public int intValue() {
        return param;
    }

    /**
     * Returns a {@code String} representation of a {@code AnalogType}.
     * @return {@code String} formatted "mim..max format".
     */
    @Override
    public @NotNull String toString() {
        return switch (param) {
            case 1 -> "-50..+50 mV";
            case 2 -> "-100..+100 mV";
            case 3 -> "-500..+500 mV";
            case 4 -> "-1..+1 V";
            case 5 -> "-2.5..+2.5 V";
            case 6 -> "-20..+20 mA";
            case 7 -> "+4..+20 mA";
            case 8 -> "-10..+10 V";
            case 9 -> "-5..+5 V";
            case 12 -> "-150..+150 mV";
            case 14 -> "J";
            case 15 -> "K";
            case 16 -> "T";
            case 17 -> "E";
            case 18 -> "R";
            case 19 -> "S";
            case 20 -> "B";
            case 21 -> "N";
            case 22 -> "C";
            case 23 -> "L";
            case 24 -> "M";
            case 25 -> "L DIN43710";
            case 27 -> "-150..+150 V";
            case 28 -> "-50..+50 V";
            case 48 -> "0..+20 mA";
            case 50 -> "0..+10 V";
            case 52 -> "0..+5 V";
            default -> "-15..+15 mV";
        };
    }

    /**
     * Returns a {@code byte[]} representation of a {@code AnalogType}.
     * @return {@code byte[]} representation of a {@code AnalogType}.
     */
    @Override
    public byte @NotNull [] toBytes() {
        return new byte[] {param};
    }

    /**
     * Parses the {@code String} argument as a {@code AnalogType}.
     * <br>Correct values = "mim..max format" or "mim-max format" for current and voltage,
     * "type" for thermocouple.
     * @param s a {@code String} containing the int representation to be parsed.
     * @return the {@code AnalogType} value represented by the argument.
     * @throws AnalogTypeFormatException if the {@code String} does not contain a parsable {@code AnalogType}.
     */
    public static @NotNull AnalogType parseAnalogType(@NotNull String s) throws AnalogTypeFormatException {
        return switch (s.toLowerCase()
                .replaceAll("[ _+]", "")
                .replaceAll(",", ".")) {
            case "-15..15mv", "-15-15mv" -> M15_P15_mV;
            case "-50..50mv", "-50-50mv" -> M50_P50_mV;
            case "-100..100mv", "-100-100mv" -> M100_P100_mV;
            case "-500..500mv", "-500-500mv" -> M500_P500_mV;
            case "-1..1v", "-1-1v" -> M1_P1_V;
            case "-2.5..2.5v", "-2.5-2.5v" -> M2_5_P2_5_V;
            case "-20..20ma", "-20-20ma" -> M20_P20_mA;
            case "4..20ma", "4-20ma" -> P4_P20_mA;
            case "-10..10v", "-10-10v" -> M10_P10_V;
            case "-5..5v", "-5-5v" -> M5_P5_V;
            case "-150..150mv", "-150-150mv" -> M150_P150_mV;
            case "j", "thermocouplej" -> Thermocouple_J;
            case "k", "thermocouplek" -> Thermocouple_K;
            case "t", "thermocouplet" -> Thermocouple_T;
            case "e", "thermocouplee" -> Thermocouple_E;
            case "r", "thermocoupler" -> Thermocouple_R;
            case "s", "thermocouples" -> Thermocouple_S;
            case "b", "thermocoupleb" -> Thermocouple_B;
            case "n", "thermocouplen" -> Thermocouple_N;
            case "c", "thermocouplec" -> Thermocouple_C;
            case "l", "thermocouplel" -> Thermocouple_L;
            case "m", "thermocouplem" -> Thermocouple_M;
            case "ldin43710", "thermocoupleldin4371" -> Thermocouple_L_DIN43710;
            case "-150..150v", "-150-150v" -> M150_P150_V;
            case "-50..50v", "-50-50v" -> M50_P50_V;
            case "0..20ma", "0-20ma" -> ZERO_P20_mA;
            case "0..10v", "0-10v" -> ZERO_P10_V;
            case "0..5v", "0-5v" -> ZERO_P5_V;
            default -> throw new AnalogTypeFormatException(s + " not parsed to AnalogType");
        };
    }

    /**
     * Parses the {@code int} argument as a {@code AnalogType}.
     * <br>Correct values = [0..9, 12, 14..25, 27..28, 48 ,50, 52].
     * @param i a {@code int} containing the int representation to be parsed.
     * @return the {@code AnalogType} value represented by the argument.
     * @throws AnalogTypeFormatException If the {@code int} does not contain a parsable {@code AnalogType}.
     */
    public static @NotNull AnalogType parseAnalogType(int i) throws AnalogTypeFormatException {
        return switch (i) {
            case 0 -> M15_P15_mV;
            case 1 -> M50_P50_mV;
            case 2 -> M100_P100_mV;
            case 3 -> M500_P500_mV;
            case 4 -> M1_P1_V;
            case 5 -> M2_5_P2_5_V;
            case 6 -> M20_P20_mA;
            case 7 -> P4_P20_mA;
            case 8 -> M10_P10_V;
            case 9 -> M5_P5_V;
            case 12 -> M150_P150_mV;
            case 14 -> Thermocouple_J;
            case 15 -> Thermocouple_K;
            case 16 -> Thermocouple_T;
            case 17 -> Thermocouple_E;
            case 18 -> Thermocouple_R;
            case 19 -> Thermocouple_S;
            case 20 -> Thermocouple_B;
            case 21 -> Thermocouple_N;
            case 22 -> Thermocouple_C;
            case 23 -> Thermocouple_L;
            case 24 -> Thermocouple_M;
            case 25 -> Thermocouple_L_DIN43710;
            case 27 -> M150_P150_V;
            case 28 -> M50_P50_V;
            case 48 -> ZERO_P20_mA;
            case 50 -> ZERO_P10_V;
            case 52 -> ZERO_P5_V;
            default -> throw new AnalogTypeFormatException(i + " not parsed to AnalogType");
        };
    }

    /**
     * Parses the {@code byte[]} argument as a {@code AnalogType}.
     * @param arr array of bytes
     * @param startIndex start index to parsed
     * @return the {@code AnalogType} value represented by the argument.
     * @throws AnalogTypeFormatException if the {@code arr[startIndex]} does not contain a parsable {@code AnalogType}.
     */
    public static @NotNull AnalogType parseAnalogType(byte @NotNull [] arr, int startIndex) throws AnalogTypeFormatException {
        try {
            return parseAnalogType(arr[startIndex]);
        } catch (Exception e) {
            throw new AnalogTypeFormatException(e.getMessage());
        }
    }

    /**
     * Parses the {@code byte[]} argument as a {@code AnalogType}.
     * @param arr array of bytes
     * @return the {@code AnalogType} value represented by the argument.
     * @throws ParityFormatException if the {@code arr[0]} does not contain a parsable {@code AnalogType}.
     */
    public static @NotNull AnalogType parseAnalogType(byte @NotNull [] arr) throws AnalogTypeFormatException  {
        return parseAnalogType(arr, 0);
    }

    /**
     * Return this.
     * @return this {@code AnalogType}.
     * @throws CloneNotSupportedException Error in clone.
     */
    @Override
    public @NotNull AnalogType clone() throws CloneNotSupportedException {
        try {
            return parseAnalogType(param);
        } catch (AnalogTypeFormatException e) {
            throw new CloneNotSupportedException();
        }
    }

    /**
     * @return minimal value in range.
     */
    public double min() {
        return switch (param) {
            case 1, 28 -> -50.0;
            case 2 -> -100.0;
            case 3 -> -500.0;
            case 4 -> -1.0;
            case 5 -> -2.5;
            case 6 -> -20.0;
            case 7 -> +4.0;
            case 8 -> -10.0;
            case 9 -> -5.0;
            case 12, 27 -> -150.0;
            case 14 -> -210.0;
            case 15, 21, 17, 16 -> -270.0;
            case 18, 19, 20, 22, 48, 50, 52 -> +0.0;
            case 23, 24, 25 -> -200.0;
            default -> -15.0;
        };
    }

    /**
     * @return maximal value in range.
     */
    public double max() {
        return switch (param) {
            case 1, 28 -> +50.0;
            case 2, 24 -> +100.0;
            case 3 -> +500.0;
            case 4 -> +1.0;
            case 5 -> +2.5;
            case 6, 7, 48 -> +20.0;
            case 8, 50 -> +10.0;
            case 9, 52 -> +5.0;
            case 12, 27 -> +150.0;
            case 14 -> +760.0;
            case 15 -> +1372.0;
            case 16 -> +400.0;
            case 17 -> +1000.0;
            case 18, 19 -> +1768.0;
            case 20 -> +1820.0;
            case 21 -> +1300.0;
            case 22 -> +2320.0;
            case 23 -> +800.0;
            case 25 -> +900.0;
            default -> +15.0;
        };
    }

    /**
     * @param v {@link String} of value.
     * @param format {@link AnalogFormat} of value.
     * @return value.
     * @throws OutOfRangeException if {@code AnalogType} thermocouple and {@link AnalogFormat} hex and {@code v} = "7FFF".
     * <br> {@link AnalogFormat} Percent and {@code v} = "-999.99" or "+999.99".
     * <br> {@link AnalogFormat} Engineering and {@code v} = "-9999.9" or "+9999.9".
     */
    public double convert(@NotNull String v, @NotNull AnalogFormat format) throws OutOfRangeException {
        if (format.equals(AnalogFormat.Hex)) {
            if (param >= 14 && param <= 25 && v.equals("7FFF")) {
                throw new OutOfRangeException("Exceed range");
            }
            int a = 1;
            for (int i = 0; i < v.length(); i++) {
                a *= 16;
            }
            int read = Integer.parseInt(v, 16);
            double res;
            if (min() >= 0){
                res = read * (max() - min()) / (a-1) - min();
            } else {
                a /= 2;
                if (read >= a){
                    read -= a*2;
                    res = read * (Math.max(Math.abs(min()), max())) / a;
                } else if (read > 0){
                    res = read * (Math.max(Math.abs(min()), max())) / (a-1);
                } else {
                    res = 0;
                }
            }
            if (res < min()) res = min();
            if (res > max()) res = max();
            return res;
        } else if (format.equals(AnalogFormat.Percent)) {
            if (v.equals("-999.99")) {
                throw new OutOfRangeException("< [%f..%f]".formatted(min(), max()));
            } else if (v.equals("+999.99")) {
                throw new OutOfRangeException("> [%f..%f]".formatted(min(), max()));
            }
            double read = Double.parseDouble(v);
            double res = read * (max() - Math.max(min(), 0)) / 100 + Math.max(min(), 0);
            if (res < min()) res = min();
            if (res > max()) res = max();
            return res;
        } else {
            if (v.equals("-9999.9")) {
                throw new OutOfRangeException("< [%f..%f]".formatted(min(), max()));
            } else if (v.equals("+9999.9") || v.equals("9999.9")) {
                throw new OutOfRangeException("> [%f..%f]".formatted(min(), max()));
            }
            return Double.parseDouble(v);
        }
    }
}
