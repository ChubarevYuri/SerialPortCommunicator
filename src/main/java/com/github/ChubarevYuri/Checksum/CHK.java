package com.github.ChubarevYuri.Checksum;

import com.github.ChubarevYuri.ByteConvertedObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Checksum control.
 */
public abstract class CHK<T> implements ByteConvertedObject {

    private final boolean enable;

    protected CHK(boolean value) {
        this.enable = value;
    };

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
        if (obj instanceof CHK) {
            return enable == ((CHK) obj).enable;
        }
        return false;
    }

    /**
     * Returns a {@code String} representation of a {@code CHK}.
     * @return {@code String} "Disable" or "Enable".
     */
    @Override
    public @NotNull String toString() {
        return enable ? "Enable" : "Disable";
    }

    /**
     * Returns a {@code byte[]} representation of a {@code CHK}.
     * @return {@code byte[]} representation of a {@code CHK}.
     */
    @Override
    public byte @NotNull [] toBytes() {
        return new byte[] {enable ? (byte)1 : (byte)0};
    }

    /**
     * Returns a {@code boolean} representation of a {@code CHK}.
     * @return {@code boolean} representation of a {@code CHK}.
     */
    public boolean toBoolean() {
        return enable;
    }

    /**
     * Generate checksum.
     * @param v data.
     * @return checksum for {@code v}.
     */
    protected abstract @NotNull T calculate(@NotNull T v);

    /**
     * Control checksum in data.
     * @param v data with checksum.
     * @return true if CRC is correct.
     */
    public abstract boolean control(@NotNull T v);

    /**
     * Add checksum in data.
     * @param v data without checksum.
     * @return data with checksum.
     */
    public abstract @NotNull T add(@NotNull T v);

    /**
     * Control checksum in data and remove checksum.
     * @param v data with checksum.
     * @return data without checksum.
     * @throws ChkControlException if {@code control()} return false.
     */
    public abstract @NotNull T controlAndRemove(@NotNull T v) throws ChkControlException;

}
