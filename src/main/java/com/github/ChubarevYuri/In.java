package com.github.ChubarevYuri;

import org.jetbrains.annotations.NotNull;

public abstract class In<T> {

    public final Event<T> onChanged = new Event<>();

    public abstract @NotNull T get() throws PortException;

}
