package de.rwth.idsg.barti.sam.exception;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class WrappedIOException extends SamException {
    public static final int ERROR_CODE = 0x8000;

    public WrappedIOException(final IOException cause) {
        super(cause, ERROR_CODE);
    }

    protected WrappedIOException(final String message, final Throwable cause, final boolean enableSuppression,
                                 final boolean writableStackTrace) {
        super(message, cause, ERROR_CODE, enableSuppression, writableStackTrace);
    }
}
