package de.rwth.idsg.barti.server.exception;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SamIsBlacklistedException extends Exception {
    public SamIsBlacklistedException() {
        super();
    }

    public SamIsBlacklistedException(final String message) {
        super(message);
    }

    public SamIsBlacklistedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SamIsBlacklistedException(final Throwable cause) {
        super(cause);
    }

    protected SamIsBlacklistedException(final String message, final Throwable cause,
                                        final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
