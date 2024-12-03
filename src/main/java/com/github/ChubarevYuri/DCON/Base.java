package com.github.ChubarevYuri.DCON;

import com.github.ChubarevYuri.*;
import com.github.ChubarevYuri.Checksum.ChkControlException;
import com.github.ChubarevYuri.Checksum.LRC8;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Base extends Device {

    /**
     * @param port {@link Port} for connection with device.
     * @param address address of device.
     */
    public Base(@NotNull Port port, @NotNull UByte address) {
        super(port);
        synchronized (this) {
            this.address = address;
        }
    }

    /**
     * Parameters for communication with device.
     * @param port serial port parameters.
     * @param lrc8 checksum.
     * @param address address.
     */
    public record InterfacePropertyDCON(@NotNull Port port, @NotNull LRC8 lrc8, UByte address) { }

    /**
     * @return parameters for communication with device.
     */
    protected abstract InterfacePropertyDCON getInterfacePropertyDCON();

    //region Send & Rec

    /**
     * Send command.
     */
    public class Send {
        private final char separator;
        private final @Nullable String command;
        private final boolean useAddress;

        /**
         * Send command.
         * <br>Format: [separator] [?Address] [command] [?CRC] [endLine]
         * @param separator first char.
         * @param command body of command.
         * @param useAddress true if Address used in command.
         */
        public Send(char separator, @Nullable String command, boolean useAddress) {
            this.separator = separator;
            this.command = command;
            this.useAddress = useAddress;
        }

        /**
         * Send command.
         * <br>Format: [separator] [Address] [command] [?CRC] [endLine]
         * @param separator first char.
         * @param command body of command.
         */
        public Send(char separator, @Nullable String command) {
            this(separator, command, true);
        }

        /**
         * Send command.
         * <br>Format: [separator] [Address] [?CRC] [endLine]
         * @param separator first char.
         */
        public Send(char separator) {
            this(separator, null, true);
        }

        /**
         * Send command.
         * <br>Format: [separator] [?Address] [?CRC] [endLine]
         * @param separator first char.
         * @param useAddress true if Address used in command.
         */
        public Send(char separator, boolean useAddress) {
            this(separator, null, useAddress);
        }


        /**
         * Returns first char.
         * @return first char.
         */
        public char getSeparator() {
            return separator;
        }


        /**
         * Returns body of command.
         * @return body of command.
         */
        public @Nullable String getCommand() {
            return command;
        }


        /**
         * Returns true if Address used in command.
         * @return true if Address used in command.
         */
        public boolean isUseAddress() {
            return useAddress;
        }

        @Override
        public @NotNull String toString() {
            String value = "" + separator;
            if (isUseAddress()) {
                value += Base.this.getInterfacePropertyDCON().address();
            }
            if (command != null) {
                value += command;
            }
            return Base.this.getInterfacePropertyDCON().lrc8().add(value);
        }
    }

    /**
     * Rec command.
     */
    public class Rec {
        private final char separator;
        private final String command;
        private final boolean useAddress;

        /**
         * Rec command.
         * <br>Format: [separator] [?Address] [command] [?CRC] [endLine]
         * @param v rec {@link String} of serial port.
         * @param useAddress true if {@code v} contains address.
         * @throws ChkControlException if {@code control()} return false.
         * @throws DeviceInterfaceException if {@code first char}!= !?>~, {@code v}.length() is low.
         */
        public Rec(@NotNull String v, boolean useAddress) throws ChkControlException, DeviceInterfaceException {
            this.useAddress = useAddress;
            v = Base.this.getInterfacePropertyDCON().lrc8().controlAndRemove(v);
            if (!v.isEmpty()) {
                char s = v.charAt(0);
                if (s != '!' && s != '?' && s != '>' && s != '~') {
                    throw new DeviceInterfaceException("Incorrect first char in %s".formatted(v));
                }
                separator = s;
                if (useAddress) {
                    if (v.length() < 3) {
                        throw new DeviceInterfaceException("Incorrect command in %s".formatted(v));
                    } else if (!v.substring(1, 3).equals( getInterfacePropertyDCON().address.toString())) {
                        throw new DeviceInterfaceException("%s not Address %s".formatted(
                                v.substring(1, 3),
                                getInterfacePropertyDCON().address));
                    } else {
                        command = v.substring(3);
                    }
                } else {
                    command = v.substring(1);
                }
            }else {
                throw new DeviceInterfaceException("null answer");
            }
        }

        /**
         * Rec command.
         * <br>Format: [separator] [Address] [command] [?CRC] [endLine]
         * @param v rec {@link String} of serial port.
         * @throws ChkControlException if {@code control()} return false.
         * @throws DeviceInterfaceException if {@code first char}!= !?>~, {@code v}.length() is low.
         */
        public Rec(@NotNull String v) throws ChkControlException, DeviceInterfaceException {
            this(v, true);
        }

        /**
         * @return first char.
         */
        public char getSeparator() {
            return separator;
        }

        /**
         * @return body of command.
         */
        public @NotNull String getCommand() {
            return command;
        }

        @Override
        public @NotNull String toString() {
            return useAddress
                    ? "%c%s%s".formatted(separator, Base.this.getAddress(), command)
                    : "%c%s".formatted(separator, command);
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
                    getInterfacePropertyDCON().port().writeLine(command.toString());
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
     * @param useAddress true if answer contains address.
     * @return answer of device.
     * @throws PortException {@link Port} connection error.
     * @throws ChkControlException if control() return false.
     * @throws DeviceInterfaceException if {@code first char}!= !?>~, v.length() is low.
     */
    public @NotNull Rec sendRec(@NotNull Send command, boolean useAddress) throws PortException {
        PortException exception = null;
        synchronized (this) {
            for (int i = 0; i < MAX_SEND; i++) {
                try {
                    return new Rec(getInterfacePropertyDCON().port().writeReadLine(command.toString()), useAddress);
                } catch (PortException e){
                    try {
                        send(new Send('~', "**", false));
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

    /**
     * @param command send to device.
     * @return answer of device.
     * @throws PortException {@link Port} connection error.
     * @throws ChkControlException if control() return false.
     * @throws DeviceInterfaceException if {@code first char}!= !?>~, v.length() is low.
     */
    public @NotNull Rec sendRec(@NotNull Send command) throws PortException {
        return sendRec(command, true);
    }

    //endregion

    //region Address

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
