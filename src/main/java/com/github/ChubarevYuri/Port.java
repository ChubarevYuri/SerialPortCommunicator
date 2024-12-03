package com.github.ChubarevYuri;

import jssc.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Serial port connection.
 * <br>In {@link com.github.ChubarevYuri.LOG} use label "serialPort"
 */
public class Port implements ByteConvertedObject {

    public static final String LOG_LABEL = "serialPort";

    private static final int TICK_CONVERTER = 250;

    private static final @NotNull String PFES = "endLine cannot be \"\"";
    private static final @NotNull String PFEB = "endBytes cannot be []";

    private final SerialPort serialPort;

    private static final ArrayList<SerialPort> ports = new ArrayList<>();

    private Port(@NotNull final SerialPort serialPort) {
        this.serialPort = serialPort;
        ports.add(serialPort);
    }

    /**
     * Create connection with serial port.
     * @param portName serial port name.
     * @throws PortException serial port not found.
     */
    public Port(@NotNull String portName) throws PortException {
        synchronized (ports) {
            //если порт с таким именем уже задействован - использовать его
            for (SerialPort port : ports) {
                if (portName.equalsIgnoreCase(port.getPortName())) {
                    serialPort = port;
                    return;
                }
            }
            //если порта с таким именем нет в системе - выдать ошибку
            {
                boolean havePort = false;
                for (String port : portListNames()) {
                    if (portName.equalsIgnoreCase(port)) {
                        havePort = true;
                        break;
                    }
                }
                if (!havePort) {
                    throw new PortException("Port %s not found".formatted(portName));
                }
            }

            serialPort = new SerialPort(portName);

            ports.add(serialPort);
        }
    }

    /**
     * Create connection with serial port.
     * @param portName serial port name.
     * @param baudRate {@link BaudRate} for connection.
     * @param parity {@link Parity} for connection.
     * @param dataBits {@link DataBits} for connection.
     * @param stopBits {@link StopBits} for connection.
     * @throws PortException serial port not found.
     */
    public Port(@NotNull String portName,
                @NotNull BaudRate baudRate,
                @NotNull Parity parity,
                @NotNull DataBits dataBits,
                @NotNull StopBits stopBits) throws PortException {
        this(portName);
        setBaudRate(baudRate);
        setParity(parity);
        setDataBits(dataBits);
        setStopBits(stopBits);
    }

    private void sendLog(@NotNull Level level, @NotNull String message) {
        LOG.send(LOG_LABEL ,level, serialPort.getPortName() + ": " + message);
    }

    private void sendLog(@Nullable String send, @Nullable String rec, @Nullable String msg) {
        String result;
        if (send != null && rec != null) {
            result = send + " -> " + rec;
        } else if (send != null) {
            result = send + " ->";
        } else if (rec != null) {
            result = "-> " + rec;
        } else {
            return;
        }
        if (msg != null) {
            result += " " + msg;
        }
        sendLog(msg != null ? Level.DEBUG : Level.TRACE, result);
    }

    private void sendLog(byte @Nullable [] send, byte @Nullable [] rec, @Nullable String msg) {
        sendLog(send != null ? UByte.convertToString(send) : null,
                rec != null ? UByte.convertToString(rec) : null,
                msg);
    }

    /**
     * Return all port names.
     * @return array of port names.
     */
    public static @NotNull String @NotNull [] portListNames() {
        return SerialPortList.getPortNames();
    }

    /**
     * Return {@link SerialPort} name.
     * @return {@link SerialPort} name.
     */
    public @NotNull String getName() {
        return serialPort.getPortName();
    }

    /**
     * Return used {@link SerialPort}.
     * @return used {@link SerialPort}.
     */
    public @NotNull SerialPort getSerialPort() {
        return serialPort;
    }

    private static final Charset baseCharset = StandardCharsets.UTF_8;
    private @NotNull Charset charset = baseCharset;

    /**
     * Return selected {@link Charset}.
     * @return selected {@link Charset}.
     */
    public @NotNull Charset getCharset() {
        synchronized (serialPort) {
            return charset;
        }
    }

    /**
     * Set the {@link Charset} for {@code Port}.
     * @param v {@link Charset}.
     */
    public void setCharset(@NotNull Charset v) {
        synchronized (serialPort) {
            this.charset = v;
        }
    }

    private static final BaudRate baseBaudRate = BaudRate.BPS9600;
    private @NotNull BaudRate baudRate = baseBaudRate;

    /**
     * Return selected {@link BaudRate}.
     * @return selected {@link BaudRate}.
     */
    public @NotNull BaudRate getBaudRate() {
        synchronized (serialPort) {
            return baudRate;
        }
    }

    /**
     * Set the {@link BaudRate} for {@code Port}.
     * @param v {@link BaudRate}.
     */
    public void setBaudRate(@NotNull BaudRate v) {
        synchronized (serialPort) {
            this.baudRate = v;
        }
    }

    private static final Parity baseParity = Parity.NONE;
    private @NotNull Parity parity = baseParity;

    /**
     * Return selected {@link Parity}.
     * @return selected {@link Parity}.
     */
    public @NotNull Parity getParity() {
        synchronized (serialPort) {
            return parity;
        }
    }

    /**
     * Set the {@link Parity} for {@code Port}.
     * @param v {@link Parity}.
     */
    public void setParity(@NotNull Parity v) {
        synchronized (serialPort) {
            this.parity = v;
        }
    }

    private static final DataBits baseDataBits = DataBits.EIGHT;
    private @NotNull DataBits dataBits = baseDataBits;

    /**
     * Return selected {@link DataBits}.
     * @return selected {@link DataBits}.
     */
    public @NotNull DataBits getDataBits() {
        synchronized (serialPort) {
            return dataBits;
        }
    }

    /**
     * Set the {@link DataBits} for {@code Port}.
     * @param v {@link DataBits}.
     */
    public void setDataBits(@NotNull DataBits v) {
        synchronized (serialPort) {
            this.dataBits = v;
        }
    }

    private static final StopBits baseStopBits = StopBits.ONE;
    private @NotNull StopBits stopBits = baseStopBits;

    /**
     * Return selected {@link StopBits}.
     * @return selected {@link StopBits}.
     */
    public @NotNull StopBits getStopBits() {
        synchronized (serialPort) {
            return stopBits;
        }
    }

    /**
     * Set the {@link StopBits} for {@code Port}.
     * @param v {@link StopBits}.
     */
    public void setStopBits(@NotNull StopBits v) {
        synchronized (serialPort) {
            this.stopBits = v;
        }
    }

    private static final FlowControl baseFlowControl = FlowControl.NONE;
    private @NotNull FlowControl flowControl = baseFlowControl;

    /**
     * Return selected {@link FlowControl}.
     * @return selected {@link FlowControl}.
     */
    public @NotNull FlowControl getFlowControl() {
        synchronized (serialPort) {
            return flowControl;
        }
    }

    /**
     * Set the {@link FlowControl} for {@code Port}.
     * @param v {@link FlowControl}.
     */
    public void setFlowControl(@NotNull FlowControl v) {
        synchronized (serialPort) {
            this.flowControl = v;
        }
    }

    private int readTimeout = 0;

    /**
     * Return max timeout (milliseconds) to read data in {@code Port}.
     * <br>If timeout is 0 then execution time control is not performed.
     * @return timeout.
     */
    public int getReadTimeout() {
        synchronized (serialPort) {
            return readTimeout;
        }
    }

    /**
     * Set max timeout (milliseconds) to read data in {@code Port}.
     * <br>If timeout is 0 then execution time control is not performed.
     * @param v timeout [0..maxInteger].
     */
    public void setReadTimeout(int v) {
        if (v < 0) v = 0;
        synchronized (serialPort) {
            this.readTimeout = v;
        }
    }

    private static final @NotNull String baseEndLine = "\r";
    private @NotNull String endLine = baseEndLine;

    /**
     * Return the value representing the end of the string.
     * @return {@code String}.
     */
    public @NotNull String getEndLine() {
        synchronized (serialPort) {
            return endLine;
        }
    }

    /**
     * Return the value representing the end of the data.
     * @return {@code byte[]}.
     */
    public byte @NotNull [] getEndBytes() {
        synchronized (serialPort) {
            return endLine.getBytes(charset);
        }
    }

    /**
     * Set the value representing the end of the string.
     * @param v {@code String}.
     * @throws PortFormatException if {@code v} = "".
     */
    public void setEndLine(@NotNull String v) throws PortFormatException {
        if (v.isEmpty()) {
            throw new PortFormatException(PFES);
        }
        synchronized (serialPort) {
            this.endLine = v;
        }
    }

    /**
     * Set the value representing the end of the data.
     * @param v {@code byte[]}.
     * @throws PortFormatException if {@code v} = [].
     */
    public void setEndBytes(byte @NotNull [] v) throws PortFormatException {
        if (v.length == 0) {
            throw new PortFormatException(PFEB);
        }
        synchronized (serialPort) {
            this.endLine = new String(v, charset);
        }
    }

    protected abstract class SendRec {
        public void run(byte @Nullable [] data, @Nullable SerialPortEventListener listener) throws PortException {
            synchronized (serialPort) {
                try {
                    try {
                        if (!serialPort.isOpened()) {
                            if (!serialPort.openPort()) {
                                throw new PortException("Port %s open failed".formatted(serialPort.getPortName()));
                            }
                        }
                    } catch (SerialPortException e) {
                        throw new PortException("Port %s open failed".formatted(serialPort.getPortName()));
                    }
                    try {
                        serialPort.setFlowControlMode(getFlowControl().intValue());
                        serialPort.setParams(
                                getBaudRate().intValue(),
                                getDataBits().intValue(),
                                getStopBits().intValue(),
                                getParity().intValue());
                    } catch (SerialPortException e) {
                        throw new PortException("Port %s setParams failed".formatted(serialPort.getPortName()));
                    }
                    if (listener != null) {
                        try {
                            serialPort.readBytes();
                        } catch (SerialPortException e) {}
                        try {
                            serialPort.addEventListener(listener);
                        } catch (SerialPortException e) {
                            throw new PortException("Port %s addEventListener failed".formatted(serialPort.getPortName()));
                        }
                    }
                    if (data != null) {
                        try {
                            serialPort.writeBytes(data);
                        } catch (SerialPortException e) {
                            throw new PortException("Port %s write failed".formatted(serialPort.getPortName()));
                        }
                    }
                    if (listener != null) {
                        long timeout = System.currentTimeMillis() + getReadTimeout();
                        while (!checkEndRead() && (getReadTimeout() <= 0 || System.currentTimeMillis() <= timeout)) {
                            try {
                                Thread.sleep(0,1);
                            } catch (Exception ignored) {
                            }
                        }
                        if (!checkEndRead()) {
                            throw new PortException("Port %s read failed (Timeout occurred)".formatted(serialPort.getPortName()));
                        }
                    } else if (data != null) {
                        //пауза после отправки длительностью в 1 байт
                        int nanos = baudRate.nanosecondsPerByte() * 2;
                        try {
                            Thread.sleep(nanos / 1000, nanos % 1000);
                        } catch (Exception ignored) { }
                    }
                } finally {
                    try {
                        serialPort.removeEventListener();
                    } catch (Exception ignored) {}
                    try {
                        serialPort.closePort();
                    } catch (Exception ignored) {}
                }
            }
        }

        public abstract boolean checkEndRead();
    }

    protected class ReadListener implements SerialPortEventListener {
        final boolean[] send = {false};
        final ArrayList<java.lang.Byte> rec = new ArrayList<>();
        final long[] timer = {System.currentTimeMillis()};


        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            try {
                if (!rec.isEmpty()) {
                    long now = System.currentTimeMillis();
                    long last = timer[0];
                    long check = baudRate.nanosecondsPerByte() / 250 + 1;
                    long ex = last + check;
                    boolean end = now > ex;
                    if (end) {
                        send[0] = true;
                        return;
                    }
                }
                byte[] s = serialPort.readBytes();
                if (s != null && s.length > 0) {
                    timer[0] = System.currentTimeMillis();
                    for (byte c : s) {
                        rec.add(c);
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    protected class ReadToListener implements SerialPortEventListener {
        final boolean[] send = {false};
        final ArrayList<java.lang.Byte> rec = new ArrayList<>();
        final long[] timer = {System.currentTimeMillis()};
        final boolean[] timerTick = {false};
        final byte @NotNull [] end;

        public ReadToListener(byte @NotNull [] end) {
            this.end = end;
        }

        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            try {
                if (!rec.isEmpty()) {
                    if (System.currentTimeMillis() > timer[0] + (baudRate.nanosecondsPerByte() / 500 + 1)) {
                        timerTick[0] = true;
                        return;
                    }
                }
                byte[] s = serialPort.readBytes();
                if (s == null) {
                    return;
                } else {
                    timer[0] = System.currentTimeMillis();
                }
                for (byte c : s) {
                    rec.add(c);
                    if (end.length > 0) {
                        if (rec.size() >= end.length) {
                            boolean e = true;
                            for (int i = 0; i < end.length; i++) {
                                if (rec.get(rec.size() - end.length + i) != end[i]) {
                                    e = false;
                                    break;
                                }
                            }
                            if (e) {
                                for (int i = rec.size() - end.length; i < rec.size(); i++) {
                                    rec.remove(i);
                                }
                                send[0] = true;
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    protected class ReadSizeListener implements SerialPortEventListener {
        byte[] buffer;
        final int[] bytesRead = {0};
        final long[] timer = {System.currentTimeMillis()};
        final boolean[] timerTick = {false};

        public ReadSizeListener(int size) {
            buffer = new byte[size];
        }

        public void serialEvent(SerialPortEvent event) {
            try {
                if (bytesRead[0] != 0) {
                    if (System.currentTimeMillis() > timer[0] + (baudRate.nanosecondsPerByte() / 500 + 1)) {
                        timerTick[0] = true;
                        return;
                    }
                }
                byte[] s = serialPort.readBytes();
                if (s == null) {
                    return;
                } else {
                    timer[0] = System.currentTimeMillis();
                }
                for (byte c : s) {
                    if (bytesRead[0] < buffer.length) {
                        buffer[bytesRead[0]++] = c;
                    }
                }
            } catch (SerialPortException ignored) {}
        }
    }

    private byte @NotNull [] listenerConverter(@NotNull ArrayList<Byte> rec, byte @Nullable [] end) {
        byte[] result = new byte[end == null ? rec.size() : rec.size() + end.length];
        for (int i = 0; i < rec.size(); i++) {
            result[i] = rec.get(i);
        }
        if (end != null) {
            System.arraycopy(end, 0, result, rec.size(), end.length);
        }
        return result;
    }

    /**
     * Send message on {@code Port} and read answer.
     * @param data send data.
     * @return answer data.
     * @throws PortException {@code Port} connection error.
     */
    public byte @NotNull [] writeRead(byte @NotNull [] data) throws PortException {
        synchronized (serialPort) {
            ReadListener listener = new ReadListener();
            String msg = null;
            byte[] result;
            try {
                new SendRec() {
                    @Override
                    public boolean checkEndRead() {
                        return listener.send[0] ||
                                (!listener.rec.isEmpty() &&
                                        listener.timer[0] + (baudRate.nanosecondsPerByte() / TICK_CONVERTER + 1) <
                                                System.currentTimeMillis());
                    }
                }.run(data, listener);
            } catch (Exception e) {
                if (listener.rec.isEmpty()) {
                    msg = e.getMessage();
                    throw e;
                }
            } finally {
                result = listenerConverter(listener.rec, null);
                sendLog(data, result, msg);
            }
            return result;
        }
    }

    /**
     * Send message on {@code Port} and read answer.
     * @param data send message.
     * @return answer message.
     * @throws PortException {@code Port} connection error.
     */
    public @NotNull String writeRead(@NotNull String data) throws PortException {
        synchronized (serialPort) {
            ReadListener listener = new ReadListener();
            String msg = null;
            String result;
            try {
                new SendRec() {
                    @Override
                    public boolean checkEndRead() {
                        return listener.send[0] ||
                                (!listener.rec.isEmpty() &&
                                        listener.timer[0] + (baudRate.nanosecondsPerByte() / TICK_CONVERTER + 1) <
                                                System.currentTimeMillis());
                    }
                }.run(data.getBytes(), listener);
            } catch (Exception e) {
                if (listener.rec.isEmpty()) {
                    msg = e.getMessage();
                    throw e;
                }
            } finally {
                result = new String(listenerConverter(listener.rec, null));
                sendLog(data, result, msg);
            }
            return result;
        }
    }

    /**
     * Send message on {@code Port} and read answer of a certain length.
     * @param data send data.
     * @param size answer length.
     * @return answer data.
     * @throws PortException {@code Port} connection error. Answer length != {@code size}.
     */
    public byte @NotNull [] writeRead(byte @NotNull [] data, int size) throws PortException {
        synchronized (serialPort) {
            ReadSizeListener listener = new ReadSizeListener(size);
            String msg = null;
            try {
                new SendRec() {
                    public boolean checkEndRead() {
                        return listener.timerTick[0] || listener.bytesRead[0] == size ||
                                (listener.bytesRead[0] > 0 &&
                                        listener.timer[0] + (baudRate.nanosecondsPerByte() / TICK_CONVERTER + 1) <
                                                System.currentTimeMillis());
                    }
                }.run(data, listener);
                if (listener.bytesRead[0] != size) {
                    throw new PortException("Port %s read failed".formatted(serialPort.getPortName()));
                }
            } catch (Exception e) {
                msg = e.getMessage();
                throw e;
            } finally {
                sendLog(data, listener.buffer, msg);
            }
            return listener.buffer;
        }
    }

    /**
     * Send message on {@code Port} and read answer of a certain length.
     * @param data send message.
     * @param size answer length.
     * @return answer message.
     * @throws PortException {@code Port} connection error. Answer length != {@code size}.
     */
    public @NotNull String writeRead(@NotNull String data, int size) throws PortException {
        synchronized (serialPort) {
            ReadSizeListener listener = new ReadSizeListener(size);
            String msg = null;
            try {
                new SendRec() {
                    public boolean checkEndRead() {
                        return listener.timerTick[0] || listener.bytesRead[0] == size ||
                                (listener.bytesRead[0] > 0 &&
                                        listener.timer[0] + (baudRate.nanosecondsPerByte() / TICK_CONVERTER + 1) <
                                                System.currentTimeMillis());
                    }
                }.run(data.getBytes(), listener);
                if (listener.bytesRead[0] != size) {
                    throw new PortException("Port %s read failed".formatted(serialPort.getPortName()));
                }
            } catch (Exception e) {
                msg = e.getMessage();
                throw e;
            } finally {
                sendLog(data, new String(listener.buffer), msg);
            }
            return new String(listener.buffer);
        }
    }

    /**
     * Send message on {@code Port} and read answer to {@code b}.
     * @param data send data.
     * @param b byte to end answer.
     * @return answer data.
     * @throws PortException {@code Port} connection error. Answer don`t contain {@code b}.
     * @throws PortFormatException {@code b} is [].
     */
    public byte @NotNull [] writeRead(byte @NotNull [] data, byte @NotNull [] b) throws PortException,
            PortFormatException {
        if (b.length == 0) {
            throw new PortFormatException(PFEB);
        }
        synchronized (serialPort) {
            ReadToListener listener = new ReadToListener(b);
            String msg = null;
            byte[] result;
            try {
                new SendRec() {
                    @Override
                    public boolean checkEndRead() {
                        return listener.send[0] || listener.timerTick[0] ||
                                (!listener.rec.isEmpty() &&
                                        listener.timer[0] + (baudRate.nanosecondsPerByte() / TICK_CONVERTER + 1) <
                                                System.currentTimeMillis());
                    }
                }.run(data, listener);
                if (!listener.send[0]) {
                    throw new PortException("Port %s read failed".formatted(serialPort.getPortName()));
                }
            } catch (Exception e) {
                msg = e.getMessage();
                throw e;
            } finally {
                sendLog(data, listenerConverter(listener.rec, msg == null ? b : null), msg);
                result = listenerConverter(listener.rec, null);
            }
            return result;
        }
    }

    /**
     * Send message on {@code Port} and read answer to {@code c}.
     * @param data send message.
     * @param c char to end answer.
     * @return answer message.
     * @throws PortException {@code Port} connection error. Answer don`t contain {@code c}.
     * @throws PortFormatException {@code c} is "".
     */
    public @NotNull String writeRead(@NotNull String data, @NotNull String c) throws PortException,
            PortFormatException {
        if (c.isEmpty()) {
            throw new PortFormatException(PFES);
        }
        synchronized (serialPort) {
            ReadToListener listener = new ReadToListener(c.getBytes(charset));
            String msg = null;
            String result;
            try {
                new SendRec() {
                    @Override
                    public boolean checkEndRead() {
                        return listener.send[0] || listener.timerTick[0] ||
                                (!listener.rec.isEmpty() &&
                                        listener.timer[0] + (baudRate.nanosecondsPerByte() / TICK_CONVERTER + 1) <
                                                System.currentTimeMillis());
                    }
                }.run(data.getBytes(charset), listener);
                if (!listener.send[0]) {
                    throw new PortException("Port %s read failed".formatted(serialPort.getPortName()));
                }
            } catch (Exception e) {
                msg = e.getMessage();
                throw e;
            } finally {
                result = new String(listenerConverter(listener.rec, null));
                sendLog(data, msg == null ? result + c : result, msg);
            }
            return result;
        }
    }

    /**
     * Send message with {@code getEndLine()} on {@code Port} and read answer to {@code getEndLine()}.
     * @param data send data.
     * @return answer data.
     * @throws PortException {@code Port} connection error. Answer don`t contain {@code getEndLine()}.
     */
    public byte @NotNull [] writeReadLine(byte @NotNull [] data) throws PortException {
        synchronized (serialPort) {
            byte[] send = new byte[data.length + getEndBytes().length];
            System.arraycopy(data, 0, send, 0, data.length);
            System.arraycopy(getEndBytes(), 0, send, data.length, getEndBytes().length);
            return writeRead(send, getEndBytes());
        }
    }

    /**
     * Send message with {@code getEndLine()} on {@code Port} and read answer to {@code getEndLine()}.
     * @param data send message.
     * @return answer message.
     * @throws PortException {@code Port} connection error. Answer don`t contain {@code getEndLine()}.
     */
    public @NotNull String writeReadLine(@NotNull String data) throws PortException {
        synchronized (serialPort) {
            return writeRead(data + endLine, endLine);
        }
    }

    /**
     * Send message on {@code Port}.
     * @param data send data.
     * @throws PortException {@code Port} connection error.
     */
    public void write(byte @NotNull [] data) throws PortException {
        synchronized (serialPort) {
            String msg = null;
            try {
                new SendRec() {
                    @Override
                    public boolean checkEndRead() {
                        return true;
                    }
                }.run(data, null);
            } catch (Exception e) {
                msg = e.getMessage();
                throw e;
            } finally {
                sendLog(data, null, msg);
            }
        }
    }

    /**
     * Send message on {@code Port}.
     * @param data send message.
     * @throws PortException {@code Port} connection error.
     */
    public void write(@NotNull String data) throws PortException {
        synchronized (serialPort) {
            String msg = null;
            try {
                new SendRec() {
                    @Override
                    public boolean checkEndRead() {
                        return true;
                    }
                }.run(data.getBytes(), null);
            } catch (Exception e) {
                msg = e.getMessage();
                throw e;
            } finally {
                sendLog(data, null, msg);
            }
        }
    }

    /**
     * Send message with {@code getEndLine()} on {@code Port}.
     * @param data send data.
     * @throws PortException {@code Port} connection error.
     */
    public void writeLine(byte @NotNull [] data) throws PortException {
        synchronized (serialPort) {
            byte[] result = new byte[data.length + getEndBytes().length];
            System.arraycopy(data, 0, result, 0, data.length);
            System.arraycopy(getEndBytes(), 0, result, data.length, getEndBytes().length);
            write(result);
        }
    }

    /**
     * Send message with {@code getEndLine()} on {@code Port}.
     * @param data send message.
     * @throws PortException {@code Port} connection error.
     */
    public void writeLine(@NotNull String data) throws PortException {
        synchronized (serialPort) {
            write(data + endLine);
        }
    }

    /**
     * Read message from {@code Port}.
     * @return data.
     * @throws PortException {@code Port} connection error.
     */
    public byte @NotNull [] read() throws PortException {
        synchronized (serialPort) {
            ReadListener listener = new ReadListener();
            String msg = null;
            byte[] result;
            try {
                new SendRec() {
                    @Override
                    public boolean checkEndRead() {
                        return listener.send[0] ||
                                (!listener.rec.isEmpty() &&
                                        listener.timer[0] + (baudRate.nanosecondsPerByte() / TICK_CONVERTER + 1) <
                                                System.currentTimeMillis());
                    }
                }.run(null, listener);
            } catch (Exception e) {
                if (listener.rec.isEmpty()) {
                    msg = e.getMessage();
                    throw e;
                }
            } finally {
                result = listenerConverter(listener.rec, null);
                sendLog(null, result, msg);
            }
            return result;
        }
    }

    /**
     * Read message of a certain length from {@code Port}.
     * @param size answer length.
     * @return data.
     * @throws PortException {@code Port} connection error. Message length != {@code size}.
     */
    public byte @NotNull [] read(int size) throws PortException {
        synchronized (serialPort) {
            ReadSizeListener listener = new ReadSizeListener(size);
            String msg = null;
            try {
                new SendRec() {
                    public boolean checkEndRead() {
                        return listener.timerTick[0] || listener.bytesRead[0] == size ||
                                (listener.bytesRead[0] > 0 &&
                                        listener.timer[0] + (baudRate.nanosecondsPerByte() / TICK_CONVERTER + 1) <
                                                System.currentTimeMillis());
                    }
                }.run(null, listener);
                if (listener.bytesRead[0] != size) {
                    throw new PortException("Port %s read failed".formatted(serialPort.getPortName()));
                }
            } catch (Exception e) {
                msg = e.getMessage();
                throw e;
            } finally {
                sendLog(null, listener.buffer, msg);
            }
            return listener.buffer;
        }
    }

    /**
     * Read message to {@code b} from {@code Port}.
     * @param b byte to end message.
     * @return data.
     * @throws PortException {@code Port} connection error. Message don`t contain {@code b}.
     * @throws PortFormatException {@code b} is [].
     */
    public byte @NotNull [] read(byte @NotNull [] b) throws PortException, PortFormatException {
        if (b.length == 0) {
            throw new PortFormatException(PFEB);
        }
        synchronized (serialPort) {
            ReadToListener listener = new ReadToListener(b);
            String msg = null;
            byte[] result;
            try {
                new SendRec() {
                    @Override
                    public boolean checkEndRead() {
                        return listener.send[0] || listener.timerTick[0] ||
                                (!listener.rec.isEmpty() &&
                                        listener.timer[0] + (baudRate.nanosecondsPerByte() / TICK_CONVERTER + 1) <
                                                System.currentTimeMillis());
                    }
                }.run(null, listener);
                if (!listener.send[0]) {
                    throw new PortException("Port %s read failed".formatted(serialPort.getPortName()));
                }
            } catch (Exception e) {
                msg = e.getMessage();
                throw e;
            } finally {
                sendLog(null, listenerConverter(listener.rec, msg == null ? b : null), msg);
                result = listenerConverter(listener.rec, null);
            }
            return result;
        }
    }

    /**
     * Read message to {@code getEndLine()} from {@code Port}.
     * @return data.
     * @throws PortException {@code Port} connection error. Message don`t contain {@code getEndLine()}.
     */
    public byte @NotNull [] readLine() throws PortException {
        synchronized (serialPort) {
            return read(getEndBytes());
        }
    }

    /**
     * Read message from {@code Port}.
     * @return message.
     * @throws PortException {@code Port} connection error.
     */
    public @NotNull String readString() throws PortException {
        synchronized (serialPort) {
            ReadListener listener = new ReadListener();
            String msg = null;
            String result;
            try {
                new SendRec() {
                    @Override
                    public boolean checkEndRead() {
                        return listener.send[0] ||
                                (!listener.rec.isEmpty() &&
                                        listener.timer[0] + (baudRate.nanosecondsPerByte() / TICK_CONVERTER + 1) <
                                                System.currentTimeMillis());
                    }
                }.run(null, listener);
            } catch (Exception e) {
                if (listener.rec.isEmpty()) {
                    msg = e.getMessage();
                    throw e;
                }
            } finally {
                result = new String(listenerConverter(listener.rec, null));
                sendLog(null, result, msg);
            }
            return result;
        }
    }

    /**
     * Read message of a certain length from {@code Port}.
     * @param size answer length.
     * @return message.
     * @throws PortException {@code Port} connection error. Message length != {@code size}.
     */
    public @NotNull String readString(int size) throws PortException {
        synchronized (serialPort) {
            ReadSizeListener listener = new ReadSizeListener(size);
            String msg = null;
            try {
                new SendRec() {
                    public boolean checkEndRead() {
                        return listener.timerTick[0] || listener.bytesRead[0] == size ||
                                (listener.bytesRead[0] > 0 &&
                                        listener.timer[0] + (baudRate.nanosecondsPerByte() / TICK_CONVERTER + 1) <
                                                System.currentTimeMillis());
                    }
                }.run(null, listener);
                if (listener.bytesRead[0] != size) {
                    throw new PortException("Port %s read failed".formatted(serialPort.getPortName()));
                }
            } catch (Exception e) {
                msg = e.getMessage();
                throw e;
            } finally {
                sendLog(null, new String(listener.buffer), msg);
            }
            return new String (listener.buffer);
        }
    }

    /**
     * Read message to {@code c} from {@code Port}.
     * @param c end message.
     * @return message.
     * @throws PortException {@code Port} connection error. Message don`t contain {@code c}.
     * @throws PortFormatException {@code c} is "".
     */
    public @NotNull String readString(@NotNull String c) throws PortException, PortFormatException {
        if (c.isEmpty()) {
            throw new PortFormatException(PFES);
        }
        synchronized (serialPort) {
            ReadToListener listener = new ReadToListener(c.getBytes(charset));
            String msg = null;
            String result;
            try {
                new SendRec() {
                    @Override
                    public boolean checkEndRead() {
                        return listener.send[0] || listener.timerTick[0] ||
                                (!listener.rec.isEmpty() &&
                                        listener.timer[0] + (baudRate.nanosecondsPerByte() / TICK_CONVERTER + 1) <
                                                System.currentTimeMillis());
                    }
                }.run(null, listener);
                if (!listener.send[0]) {
                    throw new PortException("Port %s read failed".formatted(serialPort.getPortName()));
                }
            } catch (Exception e) {
                msg = e.getMessage();
                throw e;
            } finally {
                result = new String(listenerConverter(listener.rec, null));
                sendLog(null, msg == null ? result + c : result, msg);
            }
            return result;
        }
    }

    /**
     * Read message to {@code getEndLine()} from {@code Port}.
     * @return message.
     * @throws PortException {@code Port} connection error. Message don`t contain {@code getEndLine()}.
     */
    public @NotNull String readLineString() throws PortException {
        return readString(endLine);
    }

    /**
     * Compares this object with the specified object for order. Returns a negative integer, zero, or a positive
     * integer as this object is less than, equal to, or greater than the specified object.
     * @param obj the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     * the specified object.
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (obj instanceof Port) {
            return serialPort.getPortName().equals(((Port) obj).serialPort.getPortName()) &&
                    baudRate.equals(((Port) obj).baudRate) &&
                    parity.equals(((Port) obj).parity) &&
                    dataBits.equals(((Port) obj).dataBits) &&
                    stopBits.equals(((Port) obj).stopBits) &&
                    flowControl.equals(((Port) obj).flowControl) &&
                    readTimeout == ((Port) obj).readTimeout &&
                    endLine == ((Port) obj).endLine;
        }
        return false;
    }

    /**
     * Returns a {@code String} representation of a {@code Port}.
     * <br>
     * <br>Struct:
     * <ul>
     *     <li>NAME=portName</li>
     *     <li>{@code ?}BAUDRATE={@link BaudRate}</li>
     *     <li>{@code ?}PARITY={@link Parity}</li>
     *     <li>{@code ?}DATABITS={@link DataBits}</li>
     *     <li>{@code ?}STOPBITS={@link StopBits}</li>
     *     <li>{@code ?}FLOWCONTROL={@link FlowControl}</li>
     *     <li>{@code ?}READTIMEOUT=readTimeout</li>
     *     <li>{@code ?}ENDLINE=endLine</li>
     * </ul>
     * @return {@code String} formatted.
     */
    @Override
    public @NotNull String toString() {
        synchronized (serialPort) {
            String result = "NAME=" + serialPort.getPortName();
            if (!baudRate.equals(baseBaudRate)) {
                result += "\nBAUDRATE=" + baudRate;
            }
            if (!parity.equals(baseParity)) {
                result += "\nPARITY=" + parity;
            }
            if (!dataBits.equals(baseDataBits)) {
                result += "\nDATABITS=" + dataBits;
            }
            if (!stopBits.equals(baseStopBits)) {
                result += "\nSTOPBITS=" + stopBits;
            }
            if (!flowControl.equals(baseFlowControl)) {
                result += "\nFLOWCONTROL=" + flowControl;
            }
            if (readTimeout > 0) {
                result += "\nREADTIMEOUT=" + readTimeout;
            }
            if (!charset.equals(baseCharset)) {
                result += "\nCHARSET=" + charset.name();
            }
            if (!endLine.equals(baseEndLine)) {
                StringBuilder endChar = new StringBuilder();
                for (char c : endLine.toCharArray()) {
                    endChar.append(switch (c) {
                        case '\\' -> "\\\\";
                        case '\b' -> "\\b";
                        case '\n' -> "\\n";
                        case '\r' -> "\\r";
                        case '\t' -> "\\t";
                        case '\f' -> "\\f";
                        default -> String.valueOf(c);
                    });
                }
                result += "\nENDLINE=" + endChar;
            }
            return result;
        }
    }

    /**
     * Returns a {@code byte[]} representation of a {@code Port}.
     * <br>
     * <br>Struct:
     * <ul>
     *     <li>{@code 4} bytes - portName length</li>
     *     <li>{@code portName length} bytes - portName</li>
     *     <li>{@code 1} bytes - baudRate</li>
     *     <li>{@code 1} bytes - parity</li>
     *     <li>{@code 1} bytes - dataBits</li>
     *     <li>{@code 1} bytes - stopBits</li>
     *     <li>{@code 1} bytes - flowControl</li>
     *     <li>{@code 4} bytes - readTimeout</li>
     *     <li>{@code 4} bytes - charset name length</li>
     *     <li>{@code charset name length} bytes - charset name</li>
     *     <li>{@code 4} bytes - endLine length</li>
     *     <li>{@code endLine length} bytes - endLine</li>
     * </ul>
     * @return {@code byte[]} representation of a {@code Port}.
     */
    @Override
    public byte @NotNull [] toBytes() {
        synchronized (serialPort) {
            ArrayList<byte @NotNull []> list = new ArrayList<>();
            //количество байт в имени порта
            list.add(ByteBuffer.allocate(4).putInt(serialPort.getPortName().getBytes().length).array());
            //имя порта
            list.add(serialPort.getPortName().getBytes());
            //baudRate
            list.add(this.baudRate.toBytes());
            //parity
            list.add(this.parity.toBytes());
            //dataBits
            list.add(this.dataBits.toBytes());
            //stopBits
            list.add(this.stopBits.toBytes());
            //flowControl
            list.add(this.flowControl.toBytes());
            //readTimeout
            list.add(ByteBuffer.allocate(4).putInt(this.readTimeout).array());
            //количество байт в имени charset
            list.add(ByteBuffer.allocate(4).putInt(this.charset.name().getBytes().length).array());
            //имя charset
            list.add(this.charset.name().getBytes());
            //количество байт в конце строки
            list.add(ByteBuffer.allocate(4).putInt(getEndBytes().length).array());
            //конец строки
            list.add(getEndBytes());

            int arrLength = 0;
            for (byte @NotNull [] b : list) arrLength += b.length;
            byte[] arr = new byte[arrLength];
            int destPos = 0;
            for (byte @NotNull [] b : list) {
                System.arraycopy(b, 0, arr, destPos, b.length);
                destPos += b.length;
            }
            return arr;
        }
    }

    /**
     * Parses the {@code String} argument as a {@code Port}.
     * <br>
     * <br>Struct:
     * <ul>
     *     <li>NAME=portName</li>
     *     <li>{@code ?}BAUDRATE={@link BaudRate}</li>
     *     <li>{@code ?}PARITY={@link Parity}</li>
     *     <li>{@code ?}DATABITS={@link DataBits}</li>
     *     <li>{@code ?}STOPBITS={@link StopBits}</li>
     *     <li>{@code ?}FLOWCONTROL={@link FlowControl}</li>
     *     <li>{@code ?}READTIMEOUT=readTimeout</li>
     *     <li>{@code ?}ENDLINE=endLine</li>
     * </ul>
     * @param s a {@code String} containing the int representation to be parsed.
     * @return the {@code Port} value represented by the argument.
     * @throws PortException if the serial port not found.
     * @throws PortFormatException if the {@code String} does not contain <b>NAME=portName</b>,
     *                             <b>CHARSET=value</b>does not contain a parsable charset,
     *                             <b>ENDLINE=value</b> does not contain a parsable char.
     * @throws BaudRateFormatException if <b>BAUDRATE=value</b> does not contain a parsable {@link BaudRate}.
     * @throws ParityFormatException if <b>PARITY=value</b> does not contain a parsable {@link Parity}.
     * @throws DataBitsFormatException if <b>DATABITS=value</b> does not contain a parsable {@link DataBits}.
     * @throws StopBitsFormatException if <b>STOPBITS=value</b> does not contain a parsable {@link StopBits}.
     * @throws FlowControlFormatException if <b>FLOWCONTROL=value</b> does not contain a parsable {@link FlowControl}.
     * @throws NumberFormatException if <b>READTIMEOUT=value</b> does not contain a parsable {@link Integer}.
     */
    public static @NotNull Port parsePort(@NotNull String s) throws BaudRateFormatException, PortFormatException,
            PortException, ParityFormatException, DataBitsFormatException, StopBitsFormatException,
            FlowControlFormatException, NumberFormatException {
        String[] split = s.split("\n");
        Dictionary<String,String> dict = new Hashtable<>();
        for (String str : split) {
            String[] keyValue = str.split("=", 2);
            if (keyValue.length == 2) {
                if (!keyValue[1].isEmpty()) {
                    dict.put(keyValue[0], keyValue[1]);
                }
            }
        }
        if (dict.get("NAME") == null) {
            throw new PortFormatException(s + " not parsed to Port");
        }
        Port result = new Port(dict.get("NAME"));
        if (dict.get("BAUDRATE") != null) {
            result.setBaudRate(BaudRate.parseBaudRate(dict.get("BAUDRATE")));
        }
        if (dict.get("PARITY") != null) {
            result.setParity(Parity.parseParity(dict.get("PARITY")));
        }
        if (dict.get("DATABITS") != null) {
            result.setDataBits(DataBits.parseDataBits(dict.get("DATABITS")));
        }
        if (dict.get("STOPBITS") != null) {
            result.setStopBits(StopBits.parseStopBits(dict.get("STOPBITS")));
        }
        if (dict.get("FLOWCONTROL") != null) {
            result.setFlowControl(FlowControl.parseFlowControl(dict.get("FLOWCONTROL")));
        }
        if (dict.get("READTIMEOUT") != null) {
            result.setReadTimeout(Integer.parseInt(dict.get("READTIMEOUT")));
        }
        if (dict.get("CHARSET") != null) {
            try {
                result.setCharset(Charset.forName(dict.get("CHARSET")));
            } catch (Exception e) {
                throw new PortFormatException(dict.get("CHARSET") + " not parsed to Charset");
            }
        }
        if (dict.get("ENDLINE") != null) {
            String value = dict.get("ENDLINE");
            StringBuilder res = new StringBuilder();
            boolean charSlash = false;
            for (char c : value.toCharArray()) {
                if (charSlash){
                    res.append(switch (c) {
                        case 'b' -> '\b';
                        case 't' -> '\t';
                        case 'r' -> '\r';
                        case 'n' -> '\n';
                        case 'f' -> '\f';
                        case '\\' -> '\\';
                        default -> throw new PortFormatException(value + " not parsed to EndLine");
                    });
                    charSlash = false;
                } else if (c == '\\') {
                    charSlash = true;
                } else {
                    res.append(c);
                }
            }
            result.setEndLine(res.toString());
        }
        return result;
    }

    /**
     * Parses the {@code byte[]} argument as a {@code Port}.
     * <br>
     * <br>Struct:
     * <ul>
     *     <li>{@code 4} bytes - portName length</li>
     *     <li>{@code portName length} bytes - portName</li>
     *     <li>{@code 1} bytes - baudRate</li>
     *     <li>{@code 1} bytes - parity</li>
     *     <li>{@code 1} bytes - dataBits</li>
     *     <li>{@code 1} bytes - stopBits</li>
     *     <li>{@code 1} bytes - flowControl</li>
     *     <li>{@code 4} bytes - readTimeout</li>
     *     <li>{@code 4} bytes - charset name length</li>
     *     <li>{@code charset name length} bytes - charset name</li>
     *     <li>{@code 4} bytes - endLine length</li>
     *     <li>{@code endLine length} bytes - endLine</li>
     * </ul>
     * @param arr array of bytes
     * @param startIndex start index to parsed
     * @return the {@code Port} value represented by the argument.
     * @throws PortException if the serial port not found.
     * @throws PortFormatException if the {@code arr} length <= {@code startIndex} + needBytes, charset err.
     * @throws BaudRateFormatException if <b>byte</b> does not contain a parsable {@link BaudRate}.
     * @throws ParityFormatException if <b>byte</b> does not contain a parsable {@link Parity}.
     * @throws DataBitsFormatException if <b>byte</b> does not contain a parsable {@link DataBits}.
     * @throws StopBitsFormatException if <b>byte</b> does not contain a parsable {@link StopBits}.
     * @throws FlowControlFormatException if <b>byte</b> does not contain a parsable {@link FlowControl}.
     */
    public static @NotNull Port parsePort(byte @NotNull [] arr, int startIndex) throws BaudRateFormatException,
            PortFormatException, PortException, ParityFormatException, DataBitsFormatException, StopBitsFormatException,
            FlowControlFormatException {
        if (startIndex + 4 > arr.length) {
            throw new PortFormatException("Not parsed to Port");
        }
        int nameSize = ByteBuffer.wrap(arr).getInt(startIndex); //длина имени порта
        startIndex += 4;

        if (startIndex + nameSize > arr.length) {
            throw new PortFormatException("Not parsed to Port");
        }
        byte[] nameArr = new byte[nameSize]; //байты имени порта
        System.arraycopy(arr, startIndex, nameArr, 0, nameSize);
        String portName = new String(nameArr); //имя порта
        startIndex += nameSize;

        if (startIndex + 9 > arr.length) {
            throw new PortFormatException("Not parsed to Port");
        }

        BaudRate baudRate = BaudRate.parseBaudRate(arr[startIndex++]);
        Parity parity = Parity.parseParity(arr[startIndex++]);
        DataBits dataBits = DataBits.parseDataBits(arr[startIndex++]);
        StopBits stopBits = StopBits.parseStopBits(arr[startIndex++]);
        FlowControl flowControl = FlowControl.parseFlowControl(arr[startIndex++]);

        if (startIndex + 4 > arr.length) {
            throw new PortFormatException("Not parsed to Port");
        }
        int timeout = ByteBuffer.wrap(arr).getInt(startIndex); //лимит времени чтения
        startIndex += 4;

        if (startIndex + 4 > arr.length) {
            throw new PortFormatException("Not parsed to Port");
        }
        int charsetSize = ByteBuffer.wrap(arr).getInt(startIndex); //длина имени Charset
        startIndex += 4;

        if (startIndex + charsetSize > arr.length) {
            throw new PortFormatException("Not parsed to Port");
        }
        byte[] charsetArr = new byte[charsetSize]; //байты имени Charset
        System.arraycopy(arr, startIndex, charsetArr, 0, charsetSize);
        Charset charset;
        try {
            charset = Charset.forName(new String(charsetArr)); //имя Charset
        } catch (Exception e) {
            throw new PortFormatException("Not parsed to Port");
        }
        startIndex += charsetSize;

        if (startIndex + 4 > arr.length) {
            throw new PortFormatException("Not parsed to Port");
        }
        int endLineSize = ByteBuffer.wrap(arr).getInt(startIndex); //длина имени Charset
        startIndex += 4;

        if (startIndex + endLineSize > arr.length) {
            throw new PortFormatException("Not parsed to Port");
        }
        byte[] endLineArr = new byte[endLineSize]; //байты endLine
        System.arraycopy(arr, startIndex, endLineArr, 0, endLineSize);
        startIndex += endLineSize;

        Port result = new Port(portName);//создание порта

        //задать параметры
        result.setBaudRate(baudRate);
        result.setParity(parity);
        result.setDataBits(dataBits);
        result.setStopBits(stopBits);
        result.setFlowControl(flowControl);
        result.setReadTimeout(timeout);
        result.setCharset(charset);
        result.setEndBytes(endLineArr);

        return result;
    }

    /**
     * Parses the {@code byte[]} argument as a {@code BaudRate}.
     * @param arr array of bytes
     * @return the {@code BaudRate} value represented by the argument.
     * @throws BaudRateFormatException if the {@code arr[0]} does not contain a parsable {@code BaudRate}.
     */
    public static @NotNull Port parsePort(byte @NotNull [] arr) throws BaudRateFormatException, PortException,
            ParityFormatException, DataBitsFormatException, StopBitsFormatException, FlowControlFormatException,
            NumberFormatException {
        return parsePort(arr, 0);
    }

    @Override
    public Port clone() {
        Port clone = new Port(serialPort);
        clone.setBaudRate(baudRate);
        clone.setParity(parity);
        clone.setDataBits(dataBits);
        clone.setStopBits(stopBits);
        clone.setFlowControl(flowControl);
        clone.setReadTimeout(readTimeout);
        clone.setCharset(charset);
        clone.setFlowControl(flowControl);
        return clone;
    }

}
