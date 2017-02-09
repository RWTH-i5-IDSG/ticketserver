package de.rwth.idsg.barti.sam.exception;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class WrongSamKeyException extends SamException {
    public static final int ERROR_CODE = 0x6988;

    public WrongSamKeyException(final String message) {
        super(message, ERROR_CODE);
    }

    public WrongSamKeyException(final String message, final Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

    public WrongSamKeyException(final Throwable cause) {
        super(cause, ERROR_CODE);
    }

    protected WrongSamKeyException(final String message, final Throwable cause, final boolean enableSuppression,
                                   final boolean writableStackTrace) {
        super(message, cause, ERROR_CODE, enableSuppression, writableStackTrace);
    }
}
