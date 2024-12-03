package com.github.ChubarevYuri.DCON.ICP;

import com.github.ChubarevYuri.Checksum.CHK;
import com.github.ChubarevYuri.DCON.Base;
import com.github.ChubarevYuri.DeviceInterfaceException;
import org.jetbrains.annotations.NotNull;

/**
 * Thrown to indicate that the value out of correct range.
 */
public class OutOfRangeException extends DeviceInterfaceException {

    /**
     * Constructs {@code OutOfRangeException} with no detail message.
     */
    public OutOfRangeException() { super(); }

    /**
     * Constructs {@code OutOfRangeException} with the specified detail message.
     * @param s the detail message.
     */
    public OutOfRangeException(String s) { super(s); }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated in this
     * exception's detail message.
     * @param s the detail message (which is saved for later retrieval by the {@link Throwable#getMessage()}
     *                method).
     * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method).
     *              (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public OutOfRangeException(String s, Throwable cause) { super(s, cause); }

    /**
     * Constructs a new exception with the specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())} (which typically contains the class and detail message of
     * {@code cause}). This constructor is useful for exceptions that are little more than wrappers for other
     * throwables (for example, {@link java.security.PrivilegedActionException}).
     *
     * @param  cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method).
     *               (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public OutOfRangeException(Throwable cause) { super(cause); }
}
