package de.rwth.idsg.barti.server.exception;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class NoMatchingSamException extends Exception {
    public NoMatchingSamException() {
        super();
    }

    public NoMatchingSamException(final String message) {
        super(message);
    }

    public NoMatchingSamException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NoMatchingSamException(final Throwable cause) {
        super(cause);
    }

    protected NoMatchingSamException(final String message, final Throwable cause,
                                     final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
