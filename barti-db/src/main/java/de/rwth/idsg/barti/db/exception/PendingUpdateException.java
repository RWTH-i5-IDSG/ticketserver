package de.rwth.idsg.barti.db.exception;

import lombok.Getter;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class PendingUpdateException extends Exception {
    public PendingUpdateException() {
    }

    public PendingUpdateException(final String message) {
        super(message);
    }

    public PendingUpdateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PendingUpdateException(final Throwable cause) {
        super(cause);
    }

    public PendingUpdateException(final String message, final Throwable cause, final boolean enableSuppression,
                                  final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
