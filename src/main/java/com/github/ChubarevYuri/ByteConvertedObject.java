package com.github.ChubarevYuri;

import org.jetbrains.annotations.NotNull;

public interface ByteConvertedObject {

    /**
     * Returns a {@code byte[]} representation of a {@code Object}.
     * @return {@code byte[]} representation of a {@code Object}.
     */
    public byte @NotNull [] toBytes();

}
