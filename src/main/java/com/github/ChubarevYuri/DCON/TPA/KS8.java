package com.github.ChubarevYuri.DCON.TPA;

import com.github.ChubarevYuri.*;
import com.github.ChubarevYuri.DCON.ICP.I7017R;
import com.github.ChubarevYuri.UByte;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Discrete input-output device.
 */
public class KS8 extends Base {

    /**
     * @param port {@link Port} for connection with device.
     * @param address address of device.
     */
    public KS8(@NotNull Port port, @NotNull UByte address) {
        super(port, address);
        reconnect();
    }

    protected void sendLog(@NotNull Level level, @NotNull String message, @Nullable Exception error) {
        String s =  "KS8 " + address + " " + message;
        if (error != null) {
            s += " (" + error.getMessage() + ')';
        }
        LOG.send(Port.LOG_LABEL, level, s);
    }

    @Override
    public boolean reconnect() {
        if (isConnected()) {
            return true;
        }
        if (super.reconnect()) {
            try {
                inspection();
            } catch (Exception e) {
                sendLog(Level.WARNING, "connection failed", e);
                connected = false;
                return false;
            }
            sendLog(Level.SETTING, "connected", null);
            connected = true;
            onReconnected.raise(this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void inspection() throws Exception {
        boolean [] states = new boolean[16];
        send(new Send('~', "**", false));
        final Out[] outsArr = {out0, out1, out2, out3, out4, out5, out6, out7};
        final In[] insArr = {in0, in1, in2, in3, in4, in5, in6, in7};
        ArrayList<Out> outs = new ArrayList<>();
        ArrayList<In> ins = new ArrayList<>();
        try {
            Send send = new Send('@');
            Rec rec = sendRec(send, false);
            if (rec.getSeparator() == '>' && rec.getCommand().length() == 4) {
                int a = Integer.parseInt(rec.getCommand(), 16);
                for (int i = 0; i < states.length; i++) {
                    states[i] = a % 2 == 1;
                    a /= 2;
                }
            } else {
                throw new DeviceInterfaceException(send, rec);
            }
            synchronized (this) {
                for (Out obj : outsArr) {
                    if (obj.state != states[obj.id]) {
                        outs.add(obj);
                        obj.state = states[obj.id];
                        obj.sendLog(Level.INFO, "-> " + (obj.state ? "ON" : "OFF"), null);
                    }
                }
                for (In obj : insArr) {
                    if (obj.state != states[obj.id]) {
                        ins.add(obj);
                        obj.state = states[obj.id];
                        obj.sendLog(Level.INFO, "-> " + (obj.state ? "ON" : "OFF"), null);
                    }
                }
            }
        } catch (Exception e) {
            sendLog(Level.WARNING, "inspection failed", e);
            throw e;
        }
        for (Out out : outs) {
            out.onChanged.raise(states[out.id]);
        }
        for (In in : ins) {
            in.onChanged.raise(states[in.id]);
        }
    }

    public class In extends com.github.ChubarevYuri.In<Boolean> {

        private final int id;
        private boolean state = false;

        private In(int id) {
            this.id = id;
        }

        protected void sendLog(@NotNull Level level, @NotNull String message, @Nullable Exception e) {
            KS8.this.sendLog(level, "in" + (id-8) + " " + message, e);
        }

        @Override
        public @NotNull Boolean get() throws PortException {
            boolean result;
            synchronized (KS8.this) {
                if (isCyclicSurvey() && isConnected()) {
                    return state;
                }
                try {
                    Send send = new Send('@');
                    Rec rec = sendRec(send, false);
                    if (rec.getSeparator() == '>' && rec.getCommand().length() == 4) {
                        int a = Integer.parseInt(rec.getCommand(), 16);
                        for (int i = 0; i < id; i++) {
                            a /= 2;
                        }
                        result = a % 2 == 1;
                        if (result == state) {
                            return state;
                        }
                        state = result;
                        sendLog(Level.INFO, "-> " + (state ? "ON" : "OFF"), null);
                    } else {
                        throw new DeviceInterfaceException(send, rec);
                    }
                } catch (Exception e) {
                    sendLog(Level.WARNING, "read failed", e);
                    throw e;
                }
            }
            onChanged.raise(result);
            return result;
        }
    }

    public class Out extends com.github.ChubarevYuri.In<Boolean>
            implements com.github.ChubarevYuri.Out<Boolean> {

        private final int id;
        private boolean state = false;

        private Out(int id) {
            this.id = id;
        }

        protected void sendLog(@NotNull Level level, @NotNull String message, @Nullable Exception e) {
            KS8.this.sendLog(level, "out" + id + " " + message, e);
        }

        @Override
        public @NotNull Boolean get() throws PortException {
            boolean result;
            synchronized (KS8.this) {
                if (isCyclicSurvey() && isConnected()) {
                    return state;
                }
                try {
                    Send send = new Send('@');
                    Rec rec = sendRec(send, false);
                    if (rec.getSeparator() == '>' && rec.getCommand().length() == 4) {
                        int a = Integer.parseInt(rec.getCommand(), 16);
                        for (int i = 0; i < id; i++) {
                            a /= 2;
                        }
                        result = a % 2 == 1;
                        if (result == state) {
                            return state;
                        }
                        state = result;
                        sendLog(Level.INFO, "-> " + (state ? "ON" : "OFF"), null);
                    } else {
                        throw new DeviceInterfaceException(send, rec);
                    }
                } catch (Exception e) {
                    sendLog(Level.WARNING, "read failed", e);
                    throw e;
                }
            }
            onChanged.raise(result);
            return result;
        }

        @Override
        public void set(@NotNull Boolean v) throws PortException {
            synchronized (KS8.this) {
                if (state == v) {
                    return;
                }
                try {
                    Send send = new Send('#', "A%01X0%s".formatted(id, v ? "1" : "0"));
                    Rec rec = sendRec(send, false);
                    if (rec.getSeparator() == '>' && rec.getCommand().isEmpty()) {
                        state = v;
                        sendLog(Level.INFO, "<- " + (state ? "ON" : "OFF"), null);
                    } else {
                        throw new DeviceInterfaceException(send, rec);
                    }
                } catch (Exception e) {
                    sendLog(Level.WARNING, "set failed", e);
                    throw e;
                }
            }
            onChanged.raise(v);
        }
    }

    public final Out out0 = new Out(0);
    public final Out out1 = new Out(1);
    public final Out out2 = new Out(2);
    public final Out out3 = new Out(3);
    public final Out out4 = new Out(4);
    public final Out out5 = new Out(5);
    public final Out out6 = new Out(6);
    public final Out out7 = new Out(7);

    public final In in0 = new In(8);
    public final In in1 = new In(9);
    public final In in2 = new In(10);
    public final In in3 = new In(11);
    public final In in4 = new In(12);
    public final In in5 = new In(13);
    public final In in6 = new In(14);
    public final In in7 = new In(15);
}
