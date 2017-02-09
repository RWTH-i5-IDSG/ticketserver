package de.rwth.idsg.barti.sam.exception;

import lombok.Getter;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class SamException extends Exception {
    final int errorCode;

    public SamException(final String message, final int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public SamException(final String message, final Throwable cause, final int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public SamException(final Throwable cause, final int errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    protected SamException(final String message, final Throwable cause, final int errorCode,
                           final boolean enableSuppression,
                           final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }
}
