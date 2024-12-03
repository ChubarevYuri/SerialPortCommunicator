package com.github.ChubarevYuri.Modbus;

import com.github.ChubarevYuri.*;
import com.github.ChubarevYuri.Checksum.CRC16;
import com.github.ChubarevYuri.Checksum.ChkControlException;
import com.github.ChubarevYuri.UByte;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * base class for modbus protocol.
 */
public abstract class Base extends Device {

    /**
     * Checksum.
     */
    protected static final CRC16 CHK = CRC16.ENABLE;

    public Base(@NotNull Port port, @NotNull UByte address) {
        super(port);
        synchronized (this) {
            this.address = address;
        }
    }

    /**
     * Send message on {@link LOG}: {@code name/address} {@code message} ({@code error.getMessage()})
     * @param level level of logging.
     * @param message {@link String} with information.
     * @param error {@link Exception} for read message. Maybe null.
     */
    protected void sendLog(@NotNull Level level, @NotNull String message, @Nullable Exception error) {
        String s =  this.getClass().getName() + " " + message;
        if (error != null) {
            s += " (" + error.getMessage() + ')';
        }
        LOG.send(Port.LOG_LABEL, level, s);
    }

    //region Send & Rec

    /**
     * Send command.
     */
    public class Send {
        private final UByte function;
        private final UByte[] body;

        /**
         * Send command.
         * <br>Format: [address] [function] [body] [checksum]
         * @param function function byte.
         * @param first first address.
         * @param length count registers.
         */
        public Send(@NotNull UByte function, short first, short length) {
            this.function = function;
            byte[] body = new byte[4];
            ByteBuffer.wrap(body).putShort(0, first);
            ByteBuffer.wrap(body).putShort(2, length);
            this.body = UByte.convertToUByte(body);
        }

        /**
         * Send command.
         * <br>Format: [address] [function] [body] [checksum]
         * @param function function byte.
         * @param body parameters array.
         */
        public Send(@NotNull UByte function, @NotNull UByte @Nullable [] body) {
            this.function = function;
            this.body = body;
        }

        /**
         * @return function byte.
         */
        public @NotNull UByte getFunction() {
            return function;
        }

        public byte @NotNull [] toBytesArray() {
            byte[] result = new byte[body.length + 2];
            result[0] = address.byteValue();
            result[1] = function.byteValue();
            for (int i = 0; i < body.length; i++) {
                result[i+2] = body[i].byteValue();
            }
            return CHK.add(result);
        }

        @Override
        public @NotNull String toString() {
            return UByte.convertToString(toBytesArray());
        }
    }

    /**
     * Rec command.
     */
    public class Rec {
        private final UByte function;
        private final UByte[] data;

        /**
         * Rec command.
         * <br>Format: [address] [function] [data length] [data] [checksum]
         * @param v bytes array of serial port.
         * @throws ChkControlException if {@code control()} return false.
         * @throws DeviceInterfaceException if {@code address} incorrect, {@code data length} incorrect.
         */
        public Rec(byte @NotNull [] v) throws ChkControlException, DeviceInterfaceException {
            v = CHK.controlAndRemove(v);
            if (v.length > 0) {
                if (v[0] != address.byteValue()) {
                    throw new DeviceInterfaceException("Incorrect address in %s".formatted(UByte.convertToString(v)));
                }
                int d = UByte.parseUByte(v[2]).intValue(), l = v.length;
                if (d != l - 3) {
                    throw new DeviceInterfaceException("Incorrect data length in %s".formatted(UByte.convertToString(v)));
                }
                function = UByte.parseUByte(v[1]);
                byte[] data = new byte[v.length - 3];
                System.arraycopy(v, 3, data, 0, data.length);
                this.data = UByte.convertToUByte(data);
            }else {
                throw new DeviceInterfaceException("null answer");
            }
        }

        /**
         * @return function byte.
         */
        public @NotNull UByte getFunction() {
            return function;
        }

        /**
         * @return data of command.
         */
        public @NotNull UByte @NotNull [] getData() {
            return data;
        }

        @Override
        public @NotNull String toString() {
            return address.toString() + function + new UByte(data.length) + UByte.convertToString(data);
        }
    }

    /**
     * @param command send to device.
     * @throws PortException {@link Port} connection error.
     */
    public void send(@NotNull Send command) throws PortException {
        PortException exception = null;
        synchronized (this) {
            for (int i = 0; i < MAX_SEND; i++) {
                try {
                    port.write(command.toBytesArray());
                    return;
                } catch (PortException e){
                    exception = e;
                }
            }
        }
        disconnect();
        throw exception;
    }

    /**
     * @param command send to device.
     * @return answer of device.
     * @throws PortException {@link Port} connection error.
     * @throws ChkControlException if control() return false.
     * @throws DeviceInterfaceException if {@code address} incorrect, {@code data length} incorrect.
     */
    public @NotNull Rec sendRec(@NotNull Send command) throws PortException {
        PortException exception = null;
        synchronized (this) {
            for (int i = 0; i < MAX_SEND; i++) {
                try {
                    return new Rec(port.writeRead(command.toBytesArray()));
                } catch (PortException e){
                    try {
                        Thread.sleep(10);
                    } catch (Exception ignored) {}
                    exception = e;
                }
            }
        }
        if (!(exception instanceof ChkControlException) && !(exception instanceof DeviceInterfaceException)) {
            disconnect();
        }
        throw exception;
    }

    //endregion

    //region Address

    /**
     * address of channel
     */
    protected @NotNull UByte address;

    /**
     * Event occurs when the address changed.
     */
    public final Event<UByte> onAddressChanged = new Event<>();

    /**
     * Returns address.
     * @return address.
     */
    public @NotNull UByte getAddress() {
        synchronized (this) {
            return address;
        }
    }

    /**
     * Set address.
     * @param v address.
     */
    public void setAddress(@NotNull UByte v) throws PortException {
        synchronized (this) {
            if (v.equals(address)) {
                return;
            }
            this.address = v;
        }
        onAddressChanged.raise(v);
    }

    //endregion
}
