package de.rwth.idsg.barti.server.exception;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class TooManyFailedAttemptsException extends Exception {
    public TooManyFailedAttemptsException() {
        super();
    }

    public TooManyFailedAttemptsException(final String message) {
        super(message);
    }

    public TooManyFailedAttemptsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TooManyFailedAttemptsException(final Throwable cause) {
        super(cause);
    }

    protected TooManyFailedAttemptsException(final String message, final Throwable cause,
                                             final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
