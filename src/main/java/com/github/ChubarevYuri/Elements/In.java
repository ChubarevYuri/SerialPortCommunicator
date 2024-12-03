package com.github.ChubarevYuri.Elements;

import com.github.ChubarevYuri.Event;
import com.github.ChubarevYuri.LOG;
import com.github.ChubarevYuri.Level;
import com.github.ChubarevYuri.Port;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class In<T> extends com.github.ChubarevYuri.In<T> {

    public static final String LOG_LABEL = "elements";

    protected final String name;

    protected In(@Nullable String name) {
        this.name = name;
    }

    protected void sendLog(@NotNull Level level, @NotNull String message, @Nullable Exception error) {
        if (name != null) {
            String s =  name + " " + message;
            if (error != null) {
                s += " (" + error.getMessage() + ')';
            }
            LOG.send(Port.LOG_LABEL, level, s);
        }

    }

    /**
     * Event occurs when the survey cycle has been completed.
     */
    public final Event<In<T>> onInspected = new Event<>();

    //region Connection

    protected boolean connected = false;

    /**
     * Event occurs when connection is lost.
     */
    public final Event<In<T>> onDisconnected = new Event<>();

    /**
     * Event occurs when connection is restored.
     */
    public final Event<In<T>> onReconnected = new Event<>();

    /**
     * Return communication with device.
     * @return communication with device.
     */
    public abstract boolean isConnected();

    /**
     * Reconnect with device.
     * @return connection was restored.
     */
    public abstract boolean reconnect();

    //endregion


}
