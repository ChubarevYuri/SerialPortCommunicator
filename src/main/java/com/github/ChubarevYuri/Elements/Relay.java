package com.github.ChubarevYuri.Elements;

import com.github.ChubarevYuri.Out;
import com.github.ChubarevYuri.PortException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Relay extends Indicator implements Out<Boolean> {

    public Relay(@NotNull com.github.ChubarevYuri.In<Boolean> obj, boolean reverse, @Nullable String name) {
        super(obj, reverse, name);
    }

    /**
     * @param v state.
     * @throws PortException
     */
    @Override
    public void set(@NotNull Boolean v) throws PortException {
        if (obj instanceof Out<?>) {
            Out<Boolean> dev;
            try {
                dev = (Out<Boolean>) obj;
            } catch (Exception ignored) {return;}
            dev.set(reverse != v);
        }
    }
}
