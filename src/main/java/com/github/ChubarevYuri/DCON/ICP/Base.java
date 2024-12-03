package com.github.ChubarevYuri.DCON.ICP;

import com.github.ChubarevYuri.*;
import com.github.ChubarevYuri.Checksum.LRC8;
import com.github.ChubarevYuri.UByte;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;

public abstract class Base extends com.github.ChubarevYuri.DCON.Base {

    /**
     * @param port {@link Port} for connection with device.
     * @param address address of device.
     * @param checksum use checksum control or not.
     */
    public Base(@NotNull Port port, @NotNull UByte address, @NotNull LRC8 checksum) {
        super(port, address);
        this.checksum = checksum;
        if (!this.port.getCharset().equals(StandardCharsets.UTF_8) ||
                !this.port.getFlowControl().equals(FlowControl.NONE) ||
                !this.port.getEndLine().equals("\r") ||
                this.port.getReadTimeout() != 300) {
            this.port.setCharset(StandardCharsets.UTF_8);
            this.port.setFlowControl(FlowControl.NONE);
            this.port.setEndLine("\r");
            this.port.setReadTimeout(300);
        }
    }

    /**
     * Send message on {@link LOG}: {@code name/address} {@code message} ({@code error.getMessage()})
     * @param level level of logging.
     * @param message {@link String} with information.
     * @param error {@link Exception} for read message. Maybe null.
     */
    protected void sendLog(@NotNull Level level, @NotNull String message, @Nullable Exception error) {
        String s = name.isEmpty() ? address.toString() : name + " " + message;
        if (error != null) {
            s += " (" + error.getMessage() + ')';
        }
        LOG.send(Port.LOG_LABEL, level, s);
    }

    //region INIT

    private boolean init = false;

    /**
     * @return true if can correct port parameters.
     */
    public boolean isInit() {
        synchronized (this) {
            return init;
        }
    }

    /**
     * @param v true if can correct port parameters.
     */
    public void setInit(boolean v) {
        synchronized (this) {
            sendLog(Level.SETTING, "init <- " + (v ? "ON" : "OFF"), null);
            init = v;
        }
        reconnect();
    }

    //endregion



    @Override
    protected InterfacePropertyDCON getInterfacePropertyDCON() {
        if (init) {
            Port port = this.port.clone();
            port.setBaudRate(BaudRate.BPS9600);
            port.setDataBits(DataBits.EIGHT);
            port.setStopBits(StopBits.ONE);
            port.setParity(Parity.NONE);
            return new InterfacePropertyDCON(port, LRC8.DISABLE, new UByte(0));
        } else {
            return new InterfacePropertyDCON(this.port, checksum, getAddress());
        }
    }

    //region Name

    private String name = "";

    /**
     * Event occurs when Name changed.
     */
    public final Event<String> onNameChanged = new Event<>();

    /**
     * Returns name of device. $AAM -> >{@code name}
     * @return name of device.
     */
    public @NotNull String getName() throws DeviceInterfaceException {
        try {
            reconnect();
            if (!this.isConnected()) {
                throw new DeviceInterfaceException("device disconnect");
            }
        } catch (DeviceInterfaceException e) {
            sendLog(Level.WARNING, "getName failed", e);
            throw e;
        }
        synchronized (this) {
            return name;
        }
    }

    public void setName(@NotNull String v) throws PortException {
        try {
            reconnect();
            if (!this.isConnected()) {
                throw new DeviceInterfaceException("device disconnect");
            }
            if (v.length() > 6) {
                throw new DeviceInterfaceException("invalid name: " + v);
            }
            if (v.isEmpty()) {
                throw new DeviceInterfaceException("invalid name: NULL");
            }
            Send send = new Send('~', v);
            Rec rec = sendRec(send);
            if (rec.getSeparator() != '!') {
                throw new DeviceInterfaceException(send, rec);
            }
            synchronized (this) {
                sendLog(Level.SETTING, "name <- " + v, null);
                name = v;
            }
        } catch (PortException e) {
            sendLog(Level.WARNING, "setName failed", e);
            throw e;
        }
        onNameChanged.raise(v);
    }

    //endregion

    //region BaudRate

    /**
     * Event occurs when BaudRate changed.
     */
    public final Event<BaudRate> onBaudRateChanged = new Event<>();

    /**
     * @return {@link BaudRate}.
     * @throws DeviceInterfaceException device disconnect.
     */
    public @NotNull BaudRate getBaudRate() throws DeviceInterfaceException {
        try {
            reconnect();
            if (!this.isConnected()) {
                throw new DeviceInterfaceException("device disconnect");
            }
        } catch (DeviceInterfaceException e) {
            sendLog(Level.WARNING, "getBaudRate failed", e);
            throw e;
        }
        synchronized (this) {
            return port.getBaudRate();
        }
    }

    /**
     * @param v {@link BaudRate} [1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200].
     * @throws DeviceInterfaceException device disconnect, invalid baudrate, uncorrect send, not init mode.
     * @throws PortException {@link Port} connection error.
     * @throws com.github.ChubarevYuri.Checksum.ChkControlException checksum control failed.
     */
    public void setBaudRate(@NotNull BaudRate v) throws PortException {
        try {
            reconnect();
            if (!this.isConnected()) {
                throw new DeviceInterfaceException("device disconnect");
            }
            if (!this.isInit()) {
                throw new DeviceInterfaceException("need init mode");
            }
            int baud;
            if (v.equals(BaudRate.BPS1200)) {
                baud = 3;
            } else if (v.equals(BaudRate.BPS2400)) {
                baud = 4;
            } else if (v.equals(BaudRate.BPS4800)) {
                baud = 5;
            } else if (v.equals(BaudRate.BPS9600)) {
                baud = 6;
            } else if (v.equals(BaudRate.BPS19200)) {
                baud = 7;
            } else if (v.equals(BaudRate.BPS38400)) {
                baud = 8;
            } else if (v.equals(BaudRate.BPS57600)) {
                baud = 9;
            } else if (v.equals(BaudRate.BPS115200)) {
                baud = 10;
            } else {
                throw new DeviceInterfaceException("invalid baudrate: " + v);
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
            UByte corr = cc.setBits(0,6, baud);
            if (corr.equals(cc)) {
                return;
            }
            {
                Send send = new Send('%', "%s%s%s%s".formatted(nn, tt, corr, ff));
                Rec rec = sendRec(send);
                if (rec.getSeparator() != '!') {
                    throw new DeviceInterfaceException(send, rec);
                }
            }
            synchronized (this) {
                sendLog(Level.SETTING, "baudRate <- " + v, null);
                port.setBaudRate(v);
            }
        } catch (PortException e) {
            sendLog(Level.WARNING, "setBaudRate failed", e);
            throw e;
        }
        onBaudRateChanged.raise(v);
    }

    //endregion

    //region Checksum

    private LRC8 checksum;

    /**
     * Event occurs when Checksum changed.
     */
    public final Event<LRC8> onChecksumChanged = new Event<>();

    /**
     * @return {@link LRC8}.
     * @throws DeviceInterfaceException device disconnect.
     */
    public @NotNull LRC8 getChecksum() throws DeviceInterfaceException {
        try {
            reconnect();
            if (!this.isConnected()) {
                throw new DeviceInterfaceException("device disconnect");
            }
        } catch (DeviceInterfaceException e) {
            sendLog(Level.WARNING, "getChecksum failed", e);
            throw e;
        }
        synchronized (this) {
            return checksum;
        }
    }

    /**
     * @param v {@link LRC8}.
     * @throws DeviceInterfaceException device disconnect, uncorrect send, not init mode.
     * @throws PortException {@link Port} connection error.
     * @throws com.github.ChubarevYuri.Checksum.ChkControlException checksum control failed.
     */
    public void setChecksum(@NotNull LRC8 v) throws PortException {
        try {
            reconnect();
            if (!this.isConnected()) {
                throw new DeviceInterfaceException("device disconnect");
            }
            if (!this.isInit()) {
                throw new DeviceInterfaceException("need init mode");
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
            UByte corr = ff.setBit(6, v.toBoolean());
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
                sendLog(Level.SETTING, "checksum <- " + v, null);
                checksum = v;
            }
        } catch (PortException e) {
            sendLog(Level.WARNING, "setChecksum failed", e);
            throw e;
        }
        onChecksumChanged.raise(v);
    }

    //endregion

    @Override
    public boolean reconnect() {
        if (isConnected()) {
            return true;
        }
        String nameValue = null;
        BaudRate baudRateValue = null;
        LRC8 chkValue = null;
        try {
            synchronized (this) {
                {
                    //имя устройства
                    Send send = new Send('$', "M");
                    Rec rec = sendRec(send);
                    if (rec.getSeparator() == '!') {
                        if (name.isEmpty()) {
                            name = rec.getCommand();
                            nameValue = name;
                        } else {
                            if (!name.equals(rec.getCommand())) {
                                throw new DeviceInterfaceException(name + " != " + rec.getCommand());
                            }
                        }
                    } else {
                        throw new DeviceInterfaceException(send, rec);
                    }
                }
                {
                    //Параметры устройства
                    if ((
                            port.getBaudRate().equals(BaudRate.BPS9600)
                            && port.getParity().equals(Parity.NONE)
                            && port.getStopBits().equals(StopBits.ONE)
                            && port.getDataBits().equals(DataBits.EIGHT)
                            && address.equals(new UByte(0))
                    ) || isInit()) {
                        Send send = new Send('$', "2");
                        Rec rec = sendRec(send, false);
                        if (rec.getSeparator() == '!' && rec.getCommand().length() == 8) {
                            try {
                                UByte cc = UByte.parseUByte(rec.getCommand().substring(4, 6));
                                BaudRate b = switch (cc.getBits(0, 6)) {
                                    case 3 -> BaudRate.BPS1200;
                                    case 4 -> BaudRate.BPS2400;
                                    case 5 -> BaudRate.BPS4800;
                                    case 6 -> BaudRate.BPS9600;
                                    case 7 -> BaudRate.BPS19200;
                                    case 8 -> BaudRate.BPS38400;
                                    case 9 -> BaudRate.BPS57600;
                                    case 10 -> BaudRate.BPS115200;
                                    default -> throw new DeviceInterfaceException("uncorrect baudrate");
                                };
                                if (!b.equals(port.getBaudRate())) {
                                    port.setBaudRate(b);
                                    baudRateValue = b;
                                }
                                super.setAddress(UByte.parseUByte(rec.getCommand().substring(0, 2)));
                                UByte ff = UByte.parseUByte(rec.getCommand().substring(6, 8));
                                LRC8 chk = LRC8.parseLRC8(ff.getBit(6));
                                if (!chk.equals(checksum)) {
                                    checksum = chk;
                                    chkValue = chk;
                                }
                            } catch (Exception e) {
                                throw new DeviceInterfaceException(send, rec);
                            }
                        } else {
                            throw new DeviceInterfaceException(send, rec);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.send(Level.WARNING, "connection failed (%s)".formatted(e.getMessage()));
            return false;
        }
        if (nameValue != null) {
            sendLog(Level.SETTING, "name -> " + nameValue, null);
            onNameChanged.raise(nameValue);
        }
        if (baudRateValue != null) {
            sendLog(Level.SETTING, "baudRate -> " + baudRateValue, null);
            onBaudRateChanged.raise(baudRateValue);
        }
        if (chkValue != null) {
            sendLog(Level.SETTING, "checksum -> " + chkValue, null);
            onChecksumChanged.raise(chkValue);
        }
        return true;
    }

    @Override
    protected void disconnect() {
        super.disconnect();
        sendLog(Level.WARNING, "disconnected", null);
    }
}
