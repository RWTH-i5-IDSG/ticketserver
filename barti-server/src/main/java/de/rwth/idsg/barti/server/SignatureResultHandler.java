package de.rwth.idsg.barti.server;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface SignatureResultHandler {
    void onSuccess(final byte[] signature);

    void onFailure(final Throwable throwable);
}
