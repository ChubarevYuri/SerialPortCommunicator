package com.github.ChubarevYuri.Modbus.AET;

import com.github.ChubarevYuri.*;
import com.github.ChubarevYuri.Modbus.Base;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Map;

/**
 * Multifunction measuring transducer.
 */
public class AET421_01C extends Base {

    /**
     * @param port {@link Port} for connection with device.
     * @param address address of device. Correct values: [0..247].
     */
    public AET421_01C(@NotNull Port port, @NotNull UByte address) {
        super(port, address);
        port.setBaudRate(BaudRate.BPS9600);
        if (!port.getParity().equals(Parity.ODD)) {
            port.setParity(Parity.NONE);
        }
        port.setStopBits(port.getParity().equals(Parity.NONE) ? StopBits.TWO : StopBits.ONE);
        port.setReadTimeout(100);
        port.setDataBits(DataBits.EIGHT);
        reconnect();
    }

    @Override
    protected void sendLog(@NotNull Level level, @NotNull String message, @Nullable Exception error) {
        String s =  "AET421-01C " + message;
        if (error != null) {
            s += " (" + error.getMessage() + ')';
        }
        LOG.send(Port.LOG_LABEL, level, s);
    }

    @Override
    public boolean reconnect() {
        synchronized (this) {
            connected = true;
            return true;
        }
    }

    @Override
    protected void inspection() throws Exception {
        final In[] ins = {
                Ua, Ub, Uc, Uo,
                Ia, Ib, Ic, Io,
                Uab, Ubc, Uca,
                Pa, Pb, Pc, P,
                Qa, Qb, Qc, Q,
                Sa, Sb, Sc, S,
                Qfa, Qfb, Qfc,
                F, Uph_av, Iav, Uav};
        Hashtable<In, Double> changes = new Hashtable<>();
        synchronized (this) {
            try {
                Send send = new Send(new UByte(4), (short)0, (short)0x26);
                Rec rec = sendRec(send);
                if (rec.getFunction().equals(new UByte(4))) {
                    for (In in : ins) {
                        int s = ByteBuffer.wrap(UByte.convertToByte(rec.getData()), in.id * 2, 2)
                                .asShortBuffer().get();
                        if (s < 0) {
                            s += 65536;
                        }
                        double result = s / in.k1 * in.k2;
                        if (result != in.value) {
                            in.sendLog(Level.INFO, "-> " + result, null);
                            in.value = result;
                            changes.put(in, result);
                        }
                    }
                } else {
                    throw new DeviceInterfaceException(send, rec);
                }
            } catch (Exception e) {
                sendLog(Level.WARNING, "inspection failed", e);
                throw e;
            }
        }
        for (Map.Entry<In, Double> entry : changes.entrySet()) {
            entry.getKey().onChanged.raise(entry.getValue());
        }
    }

    /**
     * Input channel.
     */
    public class In extends com.github.ChubarevYuri.In<Double> {
        private final int id;
        private final String name;
        private double value = 0.0;
        private final double k2;

        /**
         * @param id address.
         * @param name channel name from logging.
         * @param k1 coefficient from the device settings.
         * @param k2 multiplier.
         */
        private In(int id, @NotNull String name, double k1, double k2) {
            this.id = id;
            this.name = name;
            this.k1 = k1;
            this.k2 = k2;
        }

        protected void sendLog(@NotNull Level level, @NotNull String message, @Nullable Exception e) {
            AET421_01C.this.sendLog(level, name + " " + message, e);
        }

        @Override
        public @NotNull Double get() throws PortException {
            double result;
            synchronized (AET421_01C.this) {
                if (isCyclicSurvey() && isConnected()) {
                    return value;
                }
                try {
                    Send send = new Send(new UByte(4), (short)id, (short)0x01);
                    Rec rec = sendRec(send);
                    if (rec.getFunction().equals(new UByte(4))) {
                        int s = ByteBuffer.wrap( UByte.convertToByte(rec.getData()), 0,2)
                                .asShortBuffer().get();
                        if (s < 0) {
                            s += 65536;
                        }
                        result = s / getK1() * k2;
                        if (result == value) {
                            return value;
                        }
                        sendLog(Level.INFO, "<- " + result, null);
                        value = result;
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

        private double k1;

        /**
         * @return coefficient from the device settings.
         */
        public double getK1() {
            synchronized (AET421_01C.this) {
                return k1;
            }
        }

        /**
         * @param v coefficient from the device settings.
         */
        public void setK1(double v) {
            synchronized (AET421_01C.this) {
                k1 = v;
            }
        }
    }

    /**
     * Line-to-neutral voltage A.
     */
    public final In Ua = new In(0x00, "Ua", 5000, 220);

    /**
     * Line-to-neutral voltage B.
     */
    public final In Ub = new In(0x01, "Ub", 5000, 220);

    /**
     * Line-to-neutral voltage C.
     */
    public final In Uc = new In(0x02, "Uc", 5000, 220);

    /**
     * Zero-sequence voltage.
     */
    public final In Uo = new In(0x03, "Uo", 5000, 220);

    /**
     * Phase current A.
     */
    public final In Ia = new In(0x04, "Ia", 5000, 5);

    /**
     * Phase current B.
     */
    public final In Ib = new In(0x05, "Ib", 5000, 5);

    /**
     * Phase current C.
     */
    public final In Ic = new In(0x06, "Ic", 5000, 5);

    /**
     * Zero-sequence current.
     */
    public final In Io = new In(0x07, "Io", 5000, 5);

    /**
     * Line-to-line voltage.
     */
    public final In Uab = new In(0x08, "Uab", 5000, 380);

    /**
     * Line-to-line voltage.
     */
    public final In Ubc = new In(0x09, "Ubc", 5000, 380);

    /**
     * Line-to-line voltage.
     */
    public final In Uca = new In(0x0A, "Uca", 5000, 380);

    /**
     * Active power of phase A.
     */
    public final In Pa = new In(0x0B, "Pa", 5000, 1000);

    /**
     * Active power of phase B.
     */
    public final In Pb = new In(0x0C, "Pb", 5000, 1000);

    /**
     * Active power of phase C.
     */
    public final In Pc = new In(0x0D, "Pc", 5000, 1000);

    /**
     * Active power of three-phase system.
     */
    public final In P = new In(0x0E, "P", 5000, 1000);

    /**
     * Reactive power of phase A.
     */
    public final In Qa = new In(0x0F, "Qa", 5000, 1000);

    /**
     * Reactive power of phase B.
     */
    public final In Qb = new In(0x10, "Qb", 5000, 1000);

    /**
     * Reactive power of phase C.
     */
    public final In Qc = new In(0x11, "Qc", 5000, 1000);

    /**
     * Reactive power of three-phase system.
     */
    public final In Q = new In(0x12, "Q", 5000, 1000);

    /**
     * Apparent power of phase A.
     */
    public final In Sa = new In(0x13, "Sa", 5000, 1000);

    /**
     * Apparent power of phase B.
     */
    public final In Sb = new In(0x14, "Sb", 5000, 1000);

    /**
     * Apparent power of phase C.
     */
    public final In Sc = new In(0x15, "Sc", 5000, 1000);

    /**
     * Apparent power of three-phase system.
     */
    public final In S = new In(0x16, "S", 5000, 1000);

    /**
     * Reactive power of phase A.
     */
    public final In Qfa = new In(0x17, "Qfa", 5000, 1000);

    /**
     * Reactive power of phase B.
     */
    public final In Qfb = new In(0x18, "Qfb", 5000, 1000);

    /**
     * Reactive power of phase C.
     */
    public final In Qfc = new In(0x19, "Qfc", 5000, 1000);

    /**
     * Frequency.
     */
    public final In F = new In(0x1A, "F", 50000, 50);

    /**
     * Average value of line-to-neutral voltage.
     */
    public final In Uph_av = new In(0x1B, "Uph.av", 5000, 220);

    /**
     * Average value of phase current.
     */
    public final In Iav = new In(0x1C, "Iav", 5000, 5);

    /**
     * Average value of line-to-line voltage.
     */
    public final In Uav = new In(0x1D, "Uav", 5000, 380);
}
