package com.github.ChubarevYuri.Elements;

import com.github.ChubarevYuri.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Indicator of state
 */
public class Indicator extends In<Boolean> implements InspectedControl {

    protected final com.github.ChubarevYuri.In<Boolean> obj;
    protected final boolean reverse;

    public Indicator(@NotNull com.github.ChubarevYuri.In<Boolean> obj, boolean reverse, @Nullable String name) {
        super(name);
        this.obj = obj;
        this.reverse = reverse;
        this.obj.onChanged.add(e -> onChanged.raise(e != reverse));
        if (this.obj instanceof In<Boolean> dev) {
            dev.onInspected.add(e -> {
                try {
                    if (isInspection() && isConnected()) {
                        Indicator.this.onInspected.raise(Indicator.this);
                    }
                } catch (Exception ignored) {}
            });
            dev.onDisconnected.add(e -> Indicator.this.onDisconnected.raise(Indicator.this));
            dev.onReconnected.add(e -> Indicator.this.onReconnected.raise(Indicator.this));
        } else if (this.obj instanceof Device.In<Boolean> dev) {
            dev.getDevice().onInspected.add(e -> {
                try {
                    if (isInspection()) {
                        Indicator.this.onInspected.raise(Indicator.this);
                    }
                } catch (Exception ignored) {}
            });
            dev.getDevice().onDisconnected.add(e -> Indicator.this.onDisconnected.raise(Indicator.this));
            dev.getDevice().onReconnected.add(e -> Indicator.this.onReconnected.raise(Indicator.this));
        }
    }

    /**
     * Reconnect with device.
     *
     * @return connection was restored.
     */
    @Override
    public boolean reconnect() {
        if (obj instanceof In<Boolean> dev) {
            return dev.reconnect();
        } else if (obj instanceof Device.In<Boolean> dev) {
            return dev.getDevice().reconnect();
        }
        return true;
    }

    @Override
    public boolean isConnected() {
        if (obj instanceof In<Boolean> dev) {
            return dev.isConnected();
        } else if (obj instanceof Device.In<Boolean> dev) {
            return dev.getDevice().isConnected();
        }
        return true;
    }

    /**
     * @return state.
     * @throws PortException
     */
    @Override
    public @NotNull Boolean get() throws PortException {
        return reverse != obj.get();
    }

    /**
     * @return used object in inspection.
     */
    @Override
    public boolean isInspection() throws DeviceInterfaceException {
        if (obj instanceof InspectedControl ic) {
            return ic.isInspection();
        } else {
            return false;
        }
    }

    /**
     * @param v used object in inspection.
     */
    @Override
    public void setInspection(boolean v) throws PortException {
        if (obj instanceof InspectedControl ic) {
            ic.setInspection(v);
        }
    }
}
