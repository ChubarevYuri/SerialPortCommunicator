package com.github.ChubarevYuri;

import org.jetbrains.annotations.NotNull;

/**
 * Base class for interacting with devices.
 */
public abstract class Device {

    protected static final int MAX_SEND = 3;

    protected final @NotNull Port port;

    /**
     * @param port {@link Port} for connection with device.
     */
    public Device(@NotNull Port port) {
        this.port = port.clone();

        Thread cyclicSurvey = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(0, 1);
                } catch (InterruptedException ignored) {}
                try {
                    if (isCyclicSurvey() && isConnected()) {
                        //события циклического опроса
                        inspection();
                        onInspected.raise(this);
                    }
                } catch (Exception ignored) {}

            }
        });
        cyclicSurvey.setDaemon(true);
        cyclicSurvey.start();

        Thread autoReconnect = new Thread(() -> {
            long timer = System.currentTimeMillis();
            while (true) {
                try {
                    Thread.sleep(0, 1);
                } catch (InterruptedException ignored) {}
                try {
                    if (isConnected()) {
                        timer = System.currentTimeMillis();
                    } else if (getAutoReconnectTimeout() > 0 &&
                            System.currentTimeMillis() - getAutoReconnectTimeout() * 1000L > timer) {
                        timer = System.currentTimeMillis();
                        reconnect();
                    }
                } catch (Exception ignored) {}
            }
        });
        autoReconnect.setDaemon(true);
        autoReconnect.start();
    }

    /**
     * Event occurs when the survey cycle has been completed.
     */
    public final Event<Device> onInspected = new Event<>();

    //region CyclicSurvey

    private static boolean cyclicSurvey = true;

    /**
     * @return work Cyclic Survey Thread or not.
     */
    public static boolean isCyclicSurvey() {
        return cyclicSurvey;
    }

    /**
     * @param v work Cyclic Survey Thread or not.
     */
    public static void setCyclicSurvey(boolean v) {
        cyclicSurvey = v;
    }

    //endregion

    //region Connection

    protected boolean connected = false;

    /**
     * Event occurs when connection is lost.
     */
    public final Event<Device> onDisconnected = new Event<>();

    /**
     * Event occurs when connection is restored.
     */
    public final Event<Device> onReconnected = new Event<>();

    /**
     * Return communication with device.
     * @return communication with device.
     */
    public boolean isConnected() {
        synchronized (this) {
            return connected;
        }
    }

    /**
     * Reconnect with device.
     * @return connection was restored.
     */
    public abstract boolean reconnect();

    /**
     * Disconnect with device.
     */
    protected void disconnect() {
        synchronized (this) {
            if (!isConnected()) {
                return;
            }
            connected = false;
        }
        onDisconnected.raise(this);
    }

    //endregion

    //region Auto reconnect timeout

    private int autoReconnectTimeout = 0;

    /**
     * Event occurs when changed auto reconnect timeout.
     */
    public final Event<Integer> onAutoReconnectTimeoutChanged = new Event<>();

    /**
     * Return frequency of attempts to restore communication with the device when it is disconnected.
     * <br>If {@code v} = 0 then automation is off.
     * @return seconds.
     */
    public int getAutoReconnectTimeout() {
        synchronized (this) {
            return autoReconnectTimeout;
        }
    }

    /**
     * Set the frequency of attempts to restore communication with the device when it is disconnected.
     * <br>If {@code v} <= 0 then automation is off.
     * @param v seconds.
     */
    public void setAutoReconnectTimeout(int v) {
        if (v < 0) {
            v = 0;
        }
        synchronized (this) {
            if (autoReconnectTimeout == v) {
                return;
            }
            autoReconnectTimeout = v;
        }
        onAutoReconnectTimeoutChanged.raise(v);
    }

    //endregion

    /**
     * Cyclic survey method
     */
    protected abstract void inspection() throws Exception;

    public abstract class In<T> extends com.github.ChubarevYuri.In<T> {

        public Device getDevice() {
            return Device.this;
        }

    }
}
