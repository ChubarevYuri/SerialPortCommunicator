package com.github.ChubarevYuri.DCON.TPA;

import com.github.ChubarevYuri.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class BFU_GB106v1 extends Base {

    /**
     * @param port {@link Port} for connection with device.
     * @param address address of device.
     */
    public BFU_GB106v1(@NotNull Port port, @NotNull UByte address) {
        super(port, address);
        reconnect();
    }

    protected void sendLog(@NotNull Level level, @NotNull String message, @Nullable Exception error) {
        String s =  "BFU_GB106v1 " + address + " " + message;
        if (error != null) {
            s += " (" + error.getMessage() + ')';
        }
        LOG.send(Port.LOG_LABEL, level, s);
    }

    @Override
    protected void inspection() throws Exception {
        int result;
        synchronized (BFU_GB106v1.this) {
            try {
                Send send = new Send('$', "G");
                Rec rec = sendRec(send, false);
                if (rec.getSeparator() == '>' && rec.getCommand().length() == 4) {
                    int a = Integer.parseInt(rec.getCommand());
                    if (out.value == a) {
                        return;
                    }
                    result = a;
                    out.value = a;
                } else {
                    throw new DeviceInterfaceException(send, rec);
                }
            } catch (Exception e) {
                sendLog(Level.WARNING, "inspection failed", e);
                throw e;
            }
            sendLog(Level.INFO, "-> " + result, null);
        }
        out.onChanged.raise(result);
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

    public class Out extends com.github.ChubarevYuri.In<Integer>
            implements com.github.ChubarevYuri.Out<Integer> {

        private int value = 0;

        private Out() {

        }

        @Override
        public @NotNull Integer get() throws PortException {
            int result;
            synchronized (BFU_GB106v1.this) {
                try {
                    if (isCyclicSurvey() && isConnected()) {
                        return value;
                    }
                    Send send = new Send('$', "G");
                    Rec rec = sendRec(send, false);
                    if (rec.getSeparator() == '>' && rec.getCommand().length() == 4) {
                        int a = Integer.parseInt(rec.getCommand());
                        if (a == value) {
                            return value;
                        }
                        result = a;
                    } else {
                        throw new DeviceInterfaceException(send, rec);
                    }
                } catch (Exception e) {
                    sendLog(Level.WARNING, "read failed", e);
                    throw e;
                }
                sendLog(Level.INFO, "-> " + result, null);
            }
            onChanged.raise(result);
            return result;
        }

        @Override
        public void set(@NotNull Integer v) throws PortException {
            synchronized (BFU_GB106v1.this) {
                try {
                    if (v < 100) {
                        v = 0;
                    }
                    if (v > 1200) {
                        v = 1200;
                    }
                    if (value == v) {
                        return;
                    }
                    Send send = new Send('$', "A%04d".formatted(v));
                    Rec rec = sendRec(send);
                    if (rec.getSeparator() == '!' && rec.getCommand().equals("00")) {
                        value = v;
                        sendLog(Level.INFO, "<- " + v, null);
                    } else if (rec.getSeparator() == '!' && rec.getCommand().equals("01")) {
                        throw new DeviceInterfaceException("synchronized error");
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

    public final Out out = new Out();
}
