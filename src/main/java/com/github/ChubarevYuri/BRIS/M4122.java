package com.github.ChubarevYuri.BRIS;

import com.github.ChubarevYuri.*;
import com.github.ChubarevYuri.DCON.ICP.OutOfRangeException;
import com.github.ChubarevYuri.UByte;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Megoommeter BRIS M4122RS.
 */
public class M4122 extends Device {

    //region CONST bytes

    /**
     * Connection control.
     */
    private static final UByte CONNECT_CONTROL = new UByte(85);

    /**
     * Start measuring resistance.
     */
    private static final UByte RUN_R = new UByte(88);

    /**
     * Measurements completed.
     */
    private static final UByte COMPLETE = new UByte(91);

    /**
     * Out of range exception.
     */
    private static final UByte OUT_OF_RANGE = new UByte(103);

    /**
     * Read results.
     */
    private static final UByte GET = new UByte(92);

    /**
     * First byte of read array.
     */
    private static final UByte RESULT_FIRST = new UByte(93);

    /**
     * Last byte of read array.
     */
    private static final UByte RESULT_LAST = new UByte(94);

    /**
     * Byte of resistance value.
     */
    private static final UByte RESULT_R = new UByte(96);

    /**
     * Byte of voltage value.
     */
    private static final UByte RESULT_U = new UByte(99);

    /**
     * Byte of resistance value.
     */
    private static final UByte RESULT_R1 = new UByte(100);

    //endregion


    /**
     * Megoommeter BRIS M4122RS.
     * <br>WARNING: device not have address, one device on {@link Port}.
     * @param port {@link Port} for device connection.
     */
    public M4122(@NotNull Port port) {
        super(port);
        this.port.setDataBits(DataBits.EIGHT);
        this.port.setStopBits(StopBits.ONE);
        this.port.setParity(Parity.NONE);
        this.port.setBaudRate(BaudRate.BPS9600);
        reconnect();
    }

    /**
     * Send message on {@link LOG}: M4122 {@code message} ({@code error.getMessage()})
     * @param level level of logging.
     * @param message {@link String} with information.
     * @param error {@link Exception} for read message. Maybe null.
     */
    protected void sendLog(@NotNull Level level, @NotNull String message, @Nullable Exception error) {
        String s = "M4122 " + message;
        if (error != null) {
            s += " (" + error.getMessage() + ')';
        }
        LOG.send(Port.LOG_LABEL, level, s);
    }

    //region SendRec

    /**
     * Send byte on device and read answer.
     * @param send byte for send.
     * @param length bytes count in answer.
     * @return answer.
     * @throws PortException {@link Port} connection error or read length != {@code length}.
     */
    public @NotNull UByte @NotNull [] sendRec (@NotNull UByte send, int length) throws PortException {
        PortException exception = null;
        for (int i = 0; i < MAX_SEND; i++) {
            try {
                byte[] arr = this.port.writeRead(new byte [] {send.byteValue()}, length);
                UByte[] result = new UByte[arr.length];
                for (int p = 0; p < arr.length; p++) {
                    result[p] = UByte.parseUByte(arr[p]);
                }
                return result;
            } catch (PortException e) {
                exception = e;
            }
        }
        throw exception;
    }

    /**
     * Send byte on device and check answer.
     * @param send byte for send.
     * @param rec Right answer.
     * @return answer == {@code rec}.
     * @throws PortException {@link Port} connection error or read length != {@code length}.
     */
    public boolean sendRec (@NotNull UByte send, @NotNull UByte @NotNull [] rec) throws PortException {
        for (int i = 0; i < MAX_SEND; i++) {
            UByte[] answer = sendRec(send, rec.length);
            boolean ok = true;
            for (int p = 0; p < rec.length; p++) {
                if (!answer[p].equals(rec[p])) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                return true;
            }
        }
        return false;
    }

    /**
     * Send byte on device and check answer.
     * @param send byte for send.
     * @param rec Right answer.
     * @return answer == {@code rec}.
     * @throws PortException {@link Port} connection error or read length != {@code length}.
     */
    public boolean sendRec (@NotNull UByte send, @NotNull UByte rec) throws PortException {
        return sendRec(send, new UByte[] {rec});
    }

    //endregion

    //region Resistance

    public record ResistanceVoltage(int r, int u) {};

    /**
     * Measure the resistance. Returns the measured resistance and the voltage used.
     * <br>Long-time command. Limit to read 30 sec.
     * @return resistance kOm, voltage V.
     * @throws DeviceInterfaceException device not connected or wrong answer in work.
     * @throws OutOfRangeException if devise return "out of range" message.
     */
    public @NotNull ResistanceVoltage getR() throws DeviceInterfaceException, OutOfRangeException {
        reconnect();
        try {
            synchronized (this) {
                if (!isConnected()) {
                    throw new DeviceInterfaceException("not connected");
                }
                UByte[] rec;
                //send RUN_R and wait answer 20 seconds
                this.port.setReadTimeout(20000);
                try {
                    rec = sendRec(RUN_R, 1);
                } catch (Exception e) {
                    throw new DeviceInterfaceException("no response about the completion of the measurement");
                }
                if (rec[0].equals(OUT_OF_RANGE)) {
                    throw new OutOfRangeException("out of range");
                } else if (!rec[0].equals(COMPLETE)) {
                    throw new DeviceInterfaceException("wrong response about the completion of the measurement");
                }
                this.port.setReadTimeout(300);
                try {
                    rec = sendRec(GET, 11);
                } catch (Exception e) {
                    throw new DeviceInterfaceException("no read values");
                }
                if (!rec[0].equals(RESULT_FIRST) ||
                        !rec[10].equals(RESULT_LAST) ||
                        !rec[1].equals(RESULT_R) ||
                        !rec[2].equals(RESULT_U) ||
                        !rec[5].equals(RESULT_R1)) {
                    throw new DeviceInterfaceException("wrong response in values");
                } else {
                    int u = ByteBuffer.wrap(
                            new byte[]{rec[3].byteValue(), rec[4].byteValue()}
                    ).order(ByteOrder.LITTLE_ENDIAN).getShort();
                    int r = ByteBuffer.wrap(
                            new byte[]{rec[6].byteValue(), rec[7].byteValue(), rec[8].byteValue(), rec[9].byteValue()}
                    ).order(ByteOrder.LITTLE_ENDIAN).getInt();
                    sendLog(Level.INFO, "R -> %d kOm; %d V".formatted(r, u), null);
                    return new ResistanceVoltage(r, u);
                }
            }
        } catch (Exception e) {
            sendLog(Level.WARNING, "getR failed", e);
            throw e;
        }
    }

    //endregion

    @Override
    protected void inspection() throws Exception {
        synchronized (this) {
            this.port.setReadTimeout(300);
            UByte[] rec = sendRec(CONNECT_CONTROL, 1);
            if (!rec[0].equals(CONNECT_CONTROL)) {
                throw new DeviceInterfaceException("not connection");
            }
        }
    }

    @Override
    public boolean reconnect() {
        if (isConnected()) {
            return true;
        }
        try {
            inspection();
        } catch (Exception e) {
            LOG.send(Level.WARNING, "connection failed (%s)".formatted(e.getMessage()));
            return false;
        }
        sendLog(Level.SETTING, "connected", null);
        connected = true;
        onReconnected.raise(this);
        return true;
    }

    @Override
    protected void disconnect() {
        super.disconnect();
        sendLog(Level.WARNING, "disconnected", null);
    }


}
