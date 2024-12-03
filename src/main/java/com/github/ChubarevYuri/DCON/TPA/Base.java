package com.github.ChubarevYuri.DCON.TPA;

import com.github.ChubarevYuri.*;
import com.github.ChubarevYuri.Checksum.LRC8;
import com.github.ChubarevYuri.UByte;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public abstract class Base extends com.github.ChubarevYuri.DCON.Base {

    /**
     * @param port {@link Port} for connection with device.
     * @param address address of device.
     */
    public Base(@NotNull Port port, @NotNull UByte address) {
        super(port, address);
        if (!this.port.getBaudRate().equals(BaudRate.BPS9600) ||
            !this.port.getCharset().equals(StandardCharsets.UTF_8) ||
            !this.port.getDataBits().equals(DataBits.EIGHT) ||
            !this.port.getStopBits().equals(StopBits.ONE) ||
            !this.port.getParity().equals(Parity.NONE) ||
            !this.port.getFlowControl().equals(FlowControl.NONE) ||
            !this.port.getEndLine().equals("\r") ||
            this.port.getReadTimeout() != 300) {
            this.port.setBaudRate(BaudRate.BPS9600);
            this.port.setCharset(StandardCharsets.UTF_8);
            this.port.setDataBits(DataBits.EIGHT);
            this.port.setStopBits(StopBits.ONE);
            this.port.setParity(Parity.NONE);
            this.port.setFlowControl(FlowControl.NONE);
            this.port.setEndLine("\r");
            this.port.setReadTimeout(300);
        }
    }

    @Override
    protected InterfacePropertyDCON getInterfacePropertyDCON() {
        return new InterfacePropertyDCON(this.port, LRC8.ENABLE, getAddress());
    }

    //region Name

    private String name = "";

    /**
     * Returns name of device. $AAM -> >{@code name}
     * @return name of device.
     */
    public @NotNull String getName() {
        synchronized (this) {
            return name;
        }
    }

    //endregion


    @Override
    public void setAddress(@NotNull UByte v) throws PortException {
        try {
            Send send = new Send('%', v.toString());
            Rec rec = sendRec(send);
            if (rec.getSeparator() != '>' || rec.getSeparator() != '!') {
                throw new DeviceInterfaceException(send, rec);
            }
        } catch (Exception e) {
            throw new PortException(e);
        }
        super.setAddress(v);
    }

    @Override
    public boolean reconnect() {
        if (isConnected()) {
            return true;
        }
        try {
            synchronized (this) {
                Send send = new Send('$', "M");
                Rec rec = sendRec(send);
                if (rec.getSeparator() == '>' || rec.getSeparator() == '!') {
                    name = rec.getCommand();
                } else {
                    throw new DeviceInterfaceException();
                }
            }
        } catch (Exception e) {
            LOG.send(Level.WARNING, "connection failed (%s)".formatted(e.getMessage()));
            return false;
        }
        return true;
    }
}
