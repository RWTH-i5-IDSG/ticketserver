package de.rwth.idsg.barti.server;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class Constants {
    public static final int MAX_TRIES_PER_JOB = 5;
    public static final int MAX_SIGN_TRIES_PER_SAM = 10;
    public static final int MAX_REINIT_TRIES_PER_SAM = 10;
    public static final int SLEEP_TIME_IN_SECONDS = 1;
    public static final int CONNECTION_RETRIES = 10;

    static {
        if (MAX_TRIES_PER_JOB > MAX_SIGN_TRIES_PER_SAM) {
            throw new Error("MAX_TRIES_PER_JOB has to be smaller than MAX_SIGN_TRIES_PER_SAM, otherwise a broken job "
                    + "is able to blacklist a working sam!");
        }
    }
}
