package com.github.ChubarevYuri;

import org.jetbrains.annotations.NotNull;

public interface Out<T> {
    void set(@NotNull T v) throws PortException;
}
