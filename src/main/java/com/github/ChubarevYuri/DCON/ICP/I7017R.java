package com.github.ChubarevYuri.DCON.ICP;

import com.github.ChubarevYuri.*;
import com.github.ChubarevYuri.Checksum.LRC8;
import com.github.ChubarevYuri.InspectedControl;
import com.github.ChubarevYuri.UByte;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Map;

public class I7017R extends Base {

    /**
     * @param port {@link Port} for connection with device.
     * @param address address of device.
     * @param checksum use checksum control or not.
     */
    public I7017R(@NotNull Port port, @NotNull UByte address, @NotNull LRC8 checksum) {
        super(port, address, checksum);
        reconnect();
    }

    //region AnalogFormat

    private AnalogFormat format;

    /**
     * Event occurs when AnalogFormat changed.
     */
    public final Event<AnalogFormat> onFormatChanged = new Event<>();

    /**
     * @return {@link AnalogFormat}.
     * @throws DeviceInterfaceException device disconnect.
     */
    public @NotNull AnalogFormat getFormat() throws DeviceInterfaceException {
        try {
            reconnect();
            if (!this.isConnected()) {
                throw new DeviceInterfaceException("device disconnect");
            }
        } catch (DeviceInterfaceException e) {
            sendLog(Level.WARNING, "getFormat failed", e);
            throw e;
        }
        synchronized (this) {
            return format;
        }
    }

    /**
     * @param v {@link AnalogFormat}.
     * @throws DeviceInterfaceException device disconnect, uncorrect send.
     * @throws PortException {@link Port} connection error.
     * @throws com.github.ChubarevYuri.Checksum.ChkControlException checksum control failed.
     */
    public void setFormat(@NotNull AnalogFormat v) throws PortException {
        try {
            reconnect();
            if (!this.isConnected()) {
                throw new DeviceInterfaceException("device disconnect");
            }
            UByte nn, tt, cc, ff;
            {
                Send send = new Send('$', "2");
                Rec rec = sendRec(send, false);
                if (rec.getSeparator() == '!' && rec.getCommand().length() == 8) {
                    try {
                        nn = UByte.parseUByte(rec.getCommand().substring(0, 2));
                        tt = UByte.parseUByte(rec.getCommand().substring(2, 4));
                        cc = UByte.parseUByte(rec.getCommand().substring(4, 6));
                        ff = UByte.parseUByte(rec.getCommand().substring(6, 8));
                    } catch (Exception e) {
                        throw new DeviceInterfaceException(send, rec);
                    }
                } else {
                    throw new DeviceInterfaceException(send, rec);
                }
            }
            UByte corr = ff.setBits(0, 2, v.intValue());
            if (corr.equals(ff)) {
                return;
            }
            {
                Send send = new Send('%', "%s%s%s%s".formatted(nn, tt, cc, corr));
                Rec rec = sendRec(send);
                if (rec.getSeparator() != '!') {
                    throw new DeviceInterfaceException(send, rec);
                }
            }
            synchronized (this) {
                sendLog(Level.SETTING, "format <- " + v, null);
                format = v;
            }
        } catch (PortException e) {
            sendLog(Level.WARNING, "setFormat failed", e);
            throw e;
        }
        onFormatChanged.raise(v);
    }

    //endregion

    //region Filter

    private Filter filter;

    /**
     * Event occurs when Filter changed.
     */
    public final Event<Filter> onFilterChanged = new Event<>();

    /**
     * @return {@link Filter}.
     * @throws DeviceInterfaceException device disconnect.
     */
    public @NotNull Filter getFilter() throws DeviceInterfaceException {
        try {
            reconnect();
            if (!this.isConnected()) {
                throw new DeviceInterfaceException("device disconnect");
            }
        } catch (DeviceInterfaceException e) {
            sendLog(Level.WARNING, "getFilter failed", e);
            throw e;
        }
        synchronized (this) {
            return filter;
        }
    }

    /**
     * @param v {@link Filter}.
     * @throws DeviceInterfaceException device disconnect, uncorrect send.
     * @throws PortException {@link Port} connection error.
     * @throws com.github.ChubarevYuri.Checksum.ChkControlException checksum control failed.
     */
    public void setFilter(@NotNull Filter v) throws PortException {
        try {
            reconnect();
            if (!this.isConnected()) {
                throw new DeviceInterfaceException("device disconnect");
            }
            UByte nn, tt, cc, ff;
            {
                Send send = new Send('$', "2");
                Rec rec = sendRec(send, false);
                if (rec.getSeparator() == '!' && rec.getCommand().length() == 8) {
                    try {
                        nn = UByte.parseUByte(rec.getCommand().substring(0, 2));
                        tt = UByte.parseUByte(rec.getCommand().substring(2, 4));
                        cc = UByte.parseUByte(rec.getCommand().substring(4, 6));
                        ff = UByte.parseUByte(rec.getCommand().substring(6, 8));
                    } catch (Exception e) {
                        throw new DeviceInterfaceException(send, rec);
                    }
                } else {
                    throw new DeviceInterfaceException(send, rec);
                }
            }
            UByte corr = ff.setBit(7, v.toBoolean());
            if (corr.equals(ff)) {
                return;
            }
            {
                Send send = new Send('%', "%s%s%s%s".formatted(nn, tt, cc, corr));
                Rec rec = sendRec(send);
                if (rec.getSeparator() != '!') {
                    throw new DeviceInterfaceException(send, rec);
                }
            }
            synchronized (this) {
                sendLog(Level.SETTING, "filter <- " + v, null);
                filter = v;
            }
        } catch (PortException e) {
            sendLog(Level.WARNING, "setFilter failed", e);
            throw e;
        }
        onFilterChanged.raise(v);
    }

    //endregion

    //region SampleMode

    private SampleMode sampleMode;

    /**
     * Event occurs when SampleMode changed.
     */
    public final Event<SampleMode> onSampleModeChanged = new Event<>();

    /**
     * @return {@link SampleMode}.
     * @throws DeviceInterfaceException device disconnect.
     */
    public @NotNull SampleMode getSampleMode() throws DeviceInterfaceException {
        try {
            reconnect();
            if (!this.isConnected()) {
                throw new DeviceInterfaceException("device disconnect");
            }
        } catch (DeviceInterfaceException e) {
            sendLog(Level.WARNING, "getSampleMode failed", e);
            throw e;
        }
        synchronized (this) {
            return sampleMode;
        }
    }

    /**
     * @param v {@link SampleMode}.
     * @throws DeviceInterfaceException device disconnect, uncorrect send.
     * @throws PortException {@link Port} connection error.
     * @throws com.github.ChubarevYuri.Checksum.ChkControlException checksum control failed.
     */
    public void setSampleMode(@NotNull SampleMode v) throws PortException {
        try {
            reconnect();
            if (!this.isConnected()) {
                throw new DeviceInterfaceException("device disconnect");
            }
            UByte nn, tt, cc, ff;
            {
                Send send = new Send('$', "2");
                Rec rec = sendRec(send, false);
                if (rec.getSeparator() == '!' && rec.getCommand().length() == 8) {
                    try {
                        nn = UByte.parseUByte(rec.getCommand().substring(0, 2));
                        tt = UByte.parseUByte(rec.getCommand().substring(2, 4));
                        cc = UByte.parseUByte(rec.getCommand().substring(4, 6));
                        ff = UByte.parseUByte(rec.getCommand().substring(6, 8));
                    } catch (Exception e) {
                        throw new DeviceInterfaceException(send, rec);
                    }
                } else {
                    throw new DeviceInterfaceException(send, rec);
                }
            }
            UByte corr = ff.setBit(5, v.toBoolean());
            if (corr.equals(ff)) {
                return;
            }
            {
                Send send = new Send('%', "%s%s%s%s".formatted(nn, tt, cc, corr));
                Rec rec = sendRec(send);
                if (rec.getSeparator() != '!') {
                    throw new DeviceInterfaceException(send, rec);
                }
            }
            synchronized (this) {
                sendLog(Level.SETTING, "sample mode <- " + v, null);
                sampleMode = v;
            }
        } catch (PortException e) {
            sendLog(Level.WARNING, "setSampleMode failed", e);
            throw e;
        }
        onSampleModeChanged.raise(v);
    }

    //endregion

    //region AnalogType

    private AnalogType type;

    /**
     * Event occurs when AnalogType changed.
     */
    public final Event<AnalogType> onTypeChanged = new Event<>();

    /**
     * @return {@link AnalogType}.
     * @throws DeviceInterfaceException device disconnect.
     */
    public @NotNull AnalogType getType() throws DeviceInterfaceException {
        try {
            reconnect();
            if (!this.isConnected()) {
                throw new DeviceInterfaceException("device disconnect");
            }
        } catch (DeviceInterfaceException e) {
            sendLog(Level.WARNING, "getType failed", e);
            throw e;
        }
        synchronized (this) {
            return type;
        }
    }

    /**
     * @param v {@link AnalogType}.
     * @throws DeviceInterfaceException device disconnect, uncorrect send.
     * @throws PortException {@link Port} connection error.
     * @throws com.github.ChubarevYuri.Checksum.ChkControlException checksum control failed.
     */
    public void setType(@NotNull AnalogType v) throws PortException {
        try {
            reconnect();
            if (!this.isConnected()) {
                throw new DeviceInterfaceException("device disconnect");
            }
            UByte nn, tt, cc, ff;
            {
                Send send = new Send('$', "2");
                Rec rec = sendRec(send, false);
                if (rec.getSeparator() == '!' && rec.getCommand().length() == 8) {
                    try {
                        nn = UByte.parseUByte(rec.getCommand().substring(0, 2));
                        tt = UByte.parseUByte(rec.getCommand().substring(2, 4));
                        cc = UByte.parseUByte(rec.getCommand().substring(4, 6));
                        ff = UByte.parseUByte(rec.getCommand().substring(6, 8));
                    } catch (Exception e) {
                        throw new DeviceInterfaceException(send, rec);
                    }
                } else {
                    throw new DeviceInterfaceException(send, rec);
                }
            }
            UByte corr = UByte.parseUByte(v.intValue());
            if (corr.equals(tt)) {
                return;
            }
            {
                Send send = new Send('%', "%s%s%s%s".formatted(nn, corr, cc, ff));
                Rec rec = sendRec(send);
                if (rec.getSeparator() == '?') {
                    throw new DeviceInterfaceException("uncorrect type: " + v);
                } else if (rec.getSeparator() != '!') {
                    throw new DeviceInterfaceException(send, rec);
                }
            }
            synchronized (this) {
                sendLog(Level.SETTING, "type <- " + v, null);
                type = v;
            }
        } catch (PortException e) {
            sendLog(Level.WARNING, "setType failed", e);
            throw e;
        }
        onTypeChanged.raise(v);
    }

    //endregion

    @Override
    public boolean reconnect() {
        if (isConnected()) {
            return true;
        }
        if (super.reconnect()) {
            try {
                //настройки с команды $AA2
                {
                    AnalogFormat formatValue = null;
                    Filter filterValue = null;
                    SampleMode modeValue = null;
                    AnalogType typeValue = null;
                    synchronized (this) {
                        Send send = new Send('$', "2");
                        Rec rec = sendRec(send, false);
                        if (rec.getSeparator() == '!' && rec.getCommand().length() == 8) {
                            try {
                                UByte ff = UByte.parseUByte(rec.getCommand().substring(6, 8));
                                UByte tt = UByte.parseUByte(rec.getCommand().substring(2, 4));
                                AnalogFormat format = AnalogFormat.parseAnalogFormat(ff.getBits(0, 2));
                                if (!format.equals(this.format)) {
                                    formatValue = format;
                                    this.format = format;
                                    sendLog(Level.SETTING, "format -> " + format, null);
                                }
                                Filter filter = Filter.parseFilter(ff.getBit(7));
                                if (!filter.equals(this.filter)) {
                                    filterValue = filter;
                                    this.filter = filter;
                                    sendLog(Level.SETTING, "filter -> " + filter, null);
                                }
                                SampleMode mode = SampleMode.parseSampleMode(ff.getBit(5));
                                if (!mode.equals(this.sampleMode)) {
                                    modeValue = mode;
                                    this.sampleMode = mode;
                                    sendLog(Level.SETTING, "sample mode -> " + mode, null);
                                }
                                AnalogType type = AnalogType.parseAnalogType(tt.intValue());
                                if (!type.equals(this.type)) {
                                    typeValue = type;
                                    this.type = type;
                                    sendLog(Level.SETTING, "type -> " + type, null);
                                }
                            } catch (Exception e) {
                                throw new DeviceInterfaceException(send, rec);
                            }
                        } else {
                            throw new DeviceInterfaceException(send, rec);
                        }
                    }
                    if (formatValue != null) {
                        onFormatChanged.raise(formatValue);
                    }
                    if (filterValue != null) {
                        onFilterChanged.raise(filterValue);
                    }
                    if (modeValue != null) {
                        onSampleModeChanged.raise(modeValue);
                    }
                    if (typeValue != null) {
                        onTypeChanged.raise(typeValue);
                    }
                }
                //состояние каналов
                {
                    Send send = new Send('$', "6");
                    Rec rec = sendRec(send);
                    if (rec.getSeparator() == '!' && rec.getCommand().length() == 2) {
                        try {
                            UByte vv = UByte.parseUByte(rec.getCommand());
                            synchronized (this) {
                                if (in0.inspect != vv.getBit(in0.id)) {
                                    in0.inspect = vv.getBit(in0.id);
                                    in0.sendLog(Level.SETTING, "inspection -> " + (in0.inspect ? "ON" : "OFF"), null);
                                }
                                if (in1.inspect != vv.getBit(in1.id)) {
                                    in1.inspect = vv.getBit(in1.id);
                                    in1.sendLog(Level.SETTING, "inspection -> " + (in1.inspect ? "ON" : "OFF"), null);
                                }
                                if (in2.inspect != vv.getBit(in2.id)) {
                                    in2.inspect = vv.getBit(in2.id);
                                    in2.sendLog(Level.SETTING, "inspection -> " + (in2.inspect ? "ON" : "OFF"), null);
                                }
                                if (in3.inspect != vv.getBit(in3.id)) {
                                    in3.inspect = vv.getBit(in3.id);
                                    in3.sendLog(Level.SETTING, "inspection -> " + (in3.inspect ? "ON" : "OFF"), null);
                                }
                                if (in4.inspect != vv.getBit(in4.id)) {
                                    in4.inspect = vv.getBit(in4.id);
                                    in4.sendLog(Level.SETTING, "inspection -> " + (in4.inspect ? "ON" : "OFF"), null);
                                }
                                if (in5.inspect != vv.getBit(in5.id)) {
                                    in5.inspect = vv.getBit(in5.id);
                                    in5.sendLog(Level.SETTING, "inspection -> " + (in5.inspect ? "ON" : "OFF"), null);
                                }
                                if (in6.inspect != vv.getBit(in6.id)) {
                                    in6.inspect = vv.getBit(in6.id);
                                    in6.sendLog(Level.SETTING, "inspection -> " + (in6.inspect ? "ON" : "OFF"), null);
                                }
                                if (in7.inspect != vv.getBit(in7.id)) {
                                    in7.inspect = vv.getBit(in7.id);
                                    in7.sendLog(Level.SETTING, "inspection -> " + (in7.inspect ? "ON" : "OFF"), null);
                                }
                            }
                        } catch (Exception e) {
                            throw new DeviceInterfaceException(send, rec);
                        }
                    } else {
                        throw new DeviceInterfaceException(send, rec);
                    }
                }
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
        if (!(in0.isInspection() || in1.isInspection() || in2.isInspection() || in3.isInspection()
                || in4.isInspection() || in5.isInspection() || in6.isInspection() || in7.isInspection())) {
            throw new Exception();
        }
        Hashtable<In, Double> changes = new Hashtable<>();
        synchronized (this) {
            Send send = new Send('#', "");
            Rec rec = sendRec(send, false);
            if ((rec.getSeparator() == '>') && (rec.getCommand().length() == (format.equals(AnalogFormat.Hex) ? 32 : 56))) {
                final In[] ins = {in0, in1, in2, in3, in4, in5, in6, in7};
                for (int i = 0; i < 8; i++) {
                    if (ins[i].isInspection()) {
                        try {
                            int beginIndex = i * (format.equals(AnalogFormat.Hex) ? 4 : 7);
                            int endIndex = beginIndex + (format.equals(AnalogFormat.Hex) ? 4 : 7);
                            double result = type.convert(rec.getCommand().substring(beginIndex, endIndex), format);
                            if (result != ins[i].value) {
                                ins[i].sendLog(Level.INFO, "-> " + result, null);
                                ins[i].value = result;
                                changes.put(ins[i], result);
                            }
                        } catch (Exception ignored) {}
                    }
                }
            } else {
                throw new DeviceInterfaceException(send, rec);
            }
        }
        for (Map.Entry<In, Double> entry : changes.entrySet()) {
            entry.getKey().onChanged.raise(entry.getValue());
        }
    }

    public class In extends com.github.ChubarevYuri.In<Double> implements InspectedControl {

        private final int id;
        private double value = 0.0;

        private In(int id) {
            this.id = id;
        }

        protected void sendLog(@NotNull Level level, @NotNull String message, @Nullable Exception e) {
            I7017R.this.sendLog(level, "in" + id + " " + message, e);
        }

        @Override
        public @NotNull Double get() throws PortException {
            double result;
            synchronized (I7017R.this) {
                if (isCyclicSurvey() && isConnected() && isInspection()) {
                    return value;
                }
                Send send = new Send('#', "%d".formatted(id));
                Rec rec = sendRec(send, false);
                if (rec.getSeparator() == '>') {
                    if (rec.getCommand().charAt(0) != ' ') {
                        result = type.convert(rec.getCommand(), format);
                        if (result == value) {
                            return value;
                        }
                        sendLog(Level.INFO, "<- " + result, null);
                        value = result;
                    } else {
                        throw new DeviceInterfaceException("channel not in inspection");
                    }
                } else {
                    throw new DeviceInterfaceException(send, rec);
                }
            }
            onChanged.raise(result);
            return result;
        }

        private boolean inspect = true;

        @Override
        public boolean isInspection() throws DeviceInterfaceException {
            try {
                reconnect();
                if (!I7017R.this.isConnected()) {
                    throw new DeviceInterfaceException("device disconnect");
                }
            } catch (PortException e) {
                sendLog(Level.WARNING, "isInspection failed", e);
                throw e;
            }
            synchronized (I7017R.this) {
                return inspect;
            }
        }

        @Override
        public void setInspection(boolean v) throws PortException {
            try {
                reconnect();
                if (!I7017R.this.isConnected()) {
                    throw new DeviceInterfaceException("device disconnect");
                }
                UByte vv;
                {
                    Send send = new Send('$', "6");
                    Rec rec = sendRec(send);
                    if (rec.getSeparator() == '!' && rec.getCommand().length() == 2) {
                        try {
                            vv = UByte.parseUByte(rec.getCommand());
                        } catch (Exception e) {
                            throw new DeviceInterfaceException(send, rec);
                        }
                    } else {
                        throw new DeviceInterfaceException(send, rec);
                    }
                }
                UByte corr = vv.setBit(id, v);
                if (corr.equals(vv)) {
                    return;
                }
                {
                    Send send = new Send('$', "5%s".formatted(corr));
                    Rec rec = sendRec(send);
                    if (rec.getSeparator() != '!') {
                        throw new DeviceInterfaceException(send, rec);
                    }
                }
                synchronized (I7017R.this) {
                    sendLog(Level.SETTING, "inspection <- " + (v ? "ON" : "OFF"), null);
                    inspect = v;
                }
            } catch (PortException e) {
                sendLog(Level.WARNING, "setInspection failed", e);
                throw e;
            }
        }
    }

    public final In in0 = new In(0);
    public final In in1 = new In(1);
    public final In in2 = new In(2);
    public final In in3 = new In(3);
    public final In in4 = new In(4);
    public final In in5 = new In(5);
    public final In in6 = new In(6);
    public final In in7 = new In(7);

}
