package com.github.ChubarevYuri;

import com.github.ChubarevYuri.Checksum.CHK;
import com.github.ChubarevYuri.DCON.Base;
import org.jetbrains.annotations.NotNull;

/**
 * Thrown to indicate that the {@link Device} error work.
 */
public class DeviceInterfaceException extends PortException {

    /**
     * Constructs {@code DeviceInterfaceException} with no detail message.
     */
    public DeviceInterfaceException() { super(); }

    /**
     * Constructs {@code DeviceInterfaceException} with the specified detail message.
     * @param s the detail message.
     */
    public DeviceInterfaceException(String s) { super(s); }

    /**
     * Constructs {@code DeviceInterfaceException} with the specified detail message.
     * @param send send command.
     * @param rec rec command.
     */
    public DeviceInterfaceException(@NotNull Base.Send send, @NotNull Base.Rec rec) {
        super(send + " -> " + rec + "(uncorrected answer)");
    }

    /**
     * Constructs {@code DeviceInterfaceException} with the specified detail message.
     * @param send send command.
     * @param rec rec command.
     */
    public DeviceInterfaceException(@NotNull com.github.ChubarevYuri.Modbus.Base.Send send,
                                    @NotNull com.github.ChubarevYuri.Modbus.Base.Rec rec) {
        super(send + " -> " + rec + "(uncorrected answer)");
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated in this
     * exception's detail message.
     * @param s the detail message (which is saved for later retrieval by the {@link Throwable#getMessage()}
     *                method).
     * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method).
     *              (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public DeviceInterfaceException(String s, Throwable cause) { super(s, cause); }

    /**
     * Constructs a new exception with the specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())} (which typically contains the class and detail message of
     * {@code cause}). This constructor is useful for exceptions that are little more than wrappers for other
     * throwables (for example, {@link java.security.PrivilegedActionException}).
     *
     * @param  cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method).
     *               (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public DeviceInterfaceException(Throwable cause) { super(cause); }
}
