package de.rwth.idsg.barti.check;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class InvalidTicketException extends Exception {
    public InvalidTicketException() {
        super();
    }

    public InvalidTicketException(final String message) {
        super(message);
    }

    public InvalidTicketException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidTicketException(final Throwable cause) {
        super(cause);
    }

    protected InvalidTicketException(final String message, final Throwable cause, final boolean enableSuppression,
                                     final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
