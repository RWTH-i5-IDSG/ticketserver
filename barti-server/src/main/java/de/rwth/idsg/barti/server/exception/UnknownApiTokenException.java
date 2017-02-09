package de.rwth.idsg.barti.server.exception;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class UnknownApiTokenException extends Exception {
    public UnknownApiTokenException() {
        super();
    }

    public UnknownApiTokenException(final String message) {
        super(message);
    }

    public UnknownApiTokenException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnknownApiTokenException(final Throwable cause) {
        super(cause);
    }

    protected UnknownApiTokenException(final String message, final Throwable cause,
                                       final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
