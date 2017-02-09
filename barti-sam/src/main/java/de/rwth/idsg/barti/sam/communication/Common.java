package de.rwth.idsg.barti.sam.communication;

import de.intarsys.security.smartcard.card.CardException;
import de.intarsys.security.smartcard.card.ICardConnection;
import de.intarsys.security.smartcard.card.RequestAPDU;
import de.intarsys.security.smartcard.card.ResponseAPDU;
import de.rwth.idsg.barti.sam.Util;
import de.rwth.idsg.barti.sam.exception.SamException;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
public class Common {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static final int OPERATION_RETRIES = 3;

    static ResponseAPDU transmit(final ICardConnection connection, final RequestAPDU commandAPDU) throws CardException {
        log.debug("CommandAPDU: {}", Util.lazyAsHexString(commandAPDU.getBytes()));
        final ResponseAPDU responseAPDU = connection.transmit(commandAPDU);
        log.debug("ResponseAPDU: {}", Util.lazyAsHexString(responseAPDU.getBytes()));
        return responseAPDU;
    }

    public interface Retryable<T> {
        T get() throws SamException, CardException;
    }

    public static <T> T retry(final Retryable<T> retryable) throws SamException, CardException {
        RuntimeException rte = null;
        SamException se = null;
        for (int i = 0; i < OPERATION_RETRIES; ++i) {
            try {
                return retryable.get();
            } catch (final RuntimeException e) {
                rte = e;
            } catch (final SamException e) {
                se = e;
            }
        }
        if (se != null) {
            throw se;
        }
        throw rte;
    }

//    public interface RetryableVoid {
//        void get() throws SamException, CardException;
//    }
//
//    public static void retry(final RetryableVoid retryable) throws SamException, CardException {
//        RuntimeException rte = null;
//        SamException se = null;
//        for (int i = 0; i < OPERATION_RETRIES; ++i) {
//            try {
//                retryable.get();
//                return;
//            } catch (final RuntimeException e) {
//                rte = e;
//            } catch (final SamException e) {
//                se = e;
//            }
//        }
//        if (se != null) {
//            throw se;
//        }
//        throw rte;
//    }
}
