package de.rwth.idsg.barti.sam.communication;

import com.google.common.primitives.UnsignedBytes;
import de.intarsys.security.smartcard.card.CardException;
import de.intarsys.security.smartcard.card.ICardConnection;
import de.intarsys.security.smartcard.card.RequestAPDU;
import de.intarsys.security.smartcard.card.ResponseAPDU;
import de.rwth.idsg.barti.core.datatypes.pki.SamCVKey;
import de.rwth.idsg.barti.sam.Util;
import de.rwth.idsg.barti.sam.exception.ConditionsOfUseNotSatisfiedException;
import de.rwth.idsg.barti.sam.exception.SamException;
import de.rwth.idsg.barti.sam.exception.WrongSamKeyException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import static de.rwth.idsg.barti.core.Util.asHexString;
import static de.rwth.idsg.barti.sam.communication.Common.transmit;
import static de.rwth.idsg.barti.sam.communication.ReadRecord.readCertAuth;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
public class Authenticate {

    @Data
    static class SessionInfo {
        final byte[] kSAM, kExtern;
        final byte[] cSAM, cExtern;
        final byte[] skc, ski;
        byte[] ssc;

        SessionInfo(final byte[] kSAM, final byte[] kExtern, final byte[] cSAM, final byte[] cExtern) {
            this.kSAM = kSAM;
            this.kExtern = kExtern;
            this.cSAM = cSAM;
            this.cExtern = cExtern;
            skc = new byte[16];
            for (int i = 0; i < 16; ++i) {
                skc[i] = (byte) (kSAM[i] ^ kExtern[i]);
            }
            ski = new byte[16];
            for (int i = 0; i < 16; ++i) {
                ski[i] = (byte) (kSAM[i + 16] ^ kExtern[i + 16]);
            }
            ssc = new byte[8];
            for (int i = 0; i < 8; ++i) {
                ssc[i] = (byte) (cSAM[i] ^ cExtern[i]);
            }
        }
    }

    public static SessionInfo authenticate(final ICardConnection connection, final RSAKeyParameters userKey,
                                           final byte[] userKeyCHR) throws CardException, SamException {
        log.debug("reading SAM CERT AUTH...");
        final SamCVKey samAuthKey = readCertAuth(connection).getKey();
        log.debug("SAM CERT AUTH CHR      is {}", samAuthKey.getChr());
        log.debug("SAM CERT AUTH modulus  is {}", Util.lazyAsHexString(samAuthKey.getModulus()));
        log.debug("SAM CERT AUTH exponent is {}", Util.lazyAsHexString(samAuthKey.getExponent()));
        log.debug("getting challenge...");
        final byte[] cSAM = getChallenge(connection);
        log.debug("calling external authenticate...");
        final byte[] kExtern = externalAuthenticate(connection, userKey, samAuthKey, cSAM);
        log.debug("calling internal authenticate...");
        return internalAuthenticate(connection, userKey, userKeyCHR, samAuthKey, cSAM, kExtern);
    }

    private static byte[] getChallenge(final ICardConnection connection) throws CardException, SamException {
        final ResponseAPDU response = transmit(connection, new RequestAPDU(0x00, 0x84, 0x00, 0x00, 0x08));
        final int sw = response.getSw();
        switch (sw) {
            case 0x9000:
                // SUCCESS
                break;
            case 0x6400:
                throw new SamException("GET CHALLENGE: Execution Error, State of non-volatile memory "
                        + "unchanged.", sw);
            case 0x6581:
                throw new SamException("GET CHALLENGE: Memory failure", sw);
            case 0x6700:
                throw new SamException("GET CHALLENGE: Wrong length", sw);
            case 0x6985:
                throw new ConditionsOfUseNotSatisfiedException("GET CHALLENGE: Conditions of use not satisfied");
            case 0x6a86:
                throw new SamException("GET CHALLENGE: Incorrect parameters P1-P2", sw);
            case 0x6e00:
                throw new SamException("GET CHALLENGE: CLA (for INS) not supported", sw);
            default:
                throw new SamException("GET CHALLENGE: " + Integer.toHexString(sw), sw);
        }
        final byte[] data = response.getData();
        if (data.length != 8) {
            throw new IllegalArgumentException();
        }
        return data;
    }

    private static byte[] externalAuthenticate(final ICardConnection connection, final RSAKeyParameters userKey,
                                               final SamCVKey samAuthKey, final byte[] cSAM)
            throws CardException, SamException {
        final int keyByteLength = samAuthKey.getModulus().length;
        log.debug("SAM AUTH key length is {} bit", keyByteLength * 8);
        final int userKeyByteLength = (userKey.getModulus().bitLength() + 7) / 8;
        log.debug("Betreiber key length is {} bit", userKeyByteLength * 8);
        if (userKeyByteLength != keyByteLength) {
            log.error("User key has different modulus length than SAM key!");
        }
        final byte[] idSAM = samAuthKey.getChr().getIdSam();
        log.debug("ID_SAM is {}", Util.lazyAsHexString(idSAM));
        final Random random = new Random();
        final byte[] kExtern = new byte[32];
        random.nextBytes(kExtern);
        log.debug("k_Extern is {}", Util.lazyAsHexString(kExtern));
        // 20 = hash length
        // 32 = kExtern length
        //  2 = header and trailer
        final int prndLength = keyByteLength - 20 - 32 - 2;
        final byte[] prndExtern = new byte[prndLength];
        random.nextBytes(prndExtern);
        log.debug("PRND_Extern is {}", Util.lazyAsHexString(prndExtern));
        final ByteBuffer m = ByteBuffer.allocate(keyByteLength);
        m.put((byte) 0x6A);
        m.put(prndExtern);
        m.put(kExtern);
        final byte[] hash = sha1Hash(prndExtern, kExtern, cSAM, idSAM);
        log.debug("hash is {}", Util.lazyAsHexString(hash));
        m.put(hash);
        m.put((byte) 0xBC);
        if (m.position() != keyByteLength) {
            throw new IllegalStateException();
        }
        final byte[] sigMinExternM;
        {
            final RSAEngine rsaEngine = new RSAEngine();
            rsaEngine.init(false, userKey);
            final byte[] mBytes = m.array();
            log.debug("M is [{} bytes]: {}", mBytes.length, Util.lazyAsHexString(mBytes));
            final byte[] sigExternMBytes = rsaEngine.processBlock(mBytes, 0, mBytes.length);
            log.debug("Sig_Extern(M) is (byte[]) {}", Util.lazyAsHexString(sigExternMBytes));
            final BigInteger sigExternM = Util.toBigInt(sigExternMBytes);
            log.debug("Sig_Extern(M) is (BigInt) {}", sigExternM);
            final BigInteger sigStarExternM = userKey.getModulus().subtract(sigExternM);
            log.debug("Sig*_Extern(M) is (byte[]) {}", Util.lazyAsHexString(sigStarExternM));
            log.debug("Sig*_Extern(M) is (BigInt) {}", sigStarExternM);
            final BigInteger signedSigExternM = sigExternM.min(sigStarExternM);
            log.debug("SigMin_Extern(M) is Sig{}", signedSigExternM == sigStarExternM ? "*" : "");
            // the toByteArray method returns a two-complement value if the value is negative
            // since its only used modulo the SAM-AUTH modulus, shift it to the positive range
            sigMinExternM = Util.fixBigIntTwoComplement(signedSigExternM.toByteArray(), keyByteLength);
            log.debug("SigMin_Extern(M) is (byte[]) {}", Util.lazyAsHexString(sigMinExternM));
            log.debug("SigMin_Extern(M) is (BigInt) {}", Util.lazyAsBigInt(sigMinExternM));
        }
        final RSAKeyParameters samAuthKeyRSAPublicKeyParameters = Util.getRSAPublicKeyParameters(samAuthKey);
        final byte[] chiffre;
        {
            final RSAEngine rsaEngine = new RSAEngine();
            rsaEngine.init(true, samAuthKeyRSAPublicKeyParameters);
            chiffre = rsaEngine.processBlock(sigMinExternM, 0, sigMinExternM.length);
            log.debug("Enc_KPUB-RSA-SAM-AUTH is (byte[]) {}", Util.lazyAsHexString(chiffre));
            log.debug("Enc_KPUB-RSA-SAM-AUTH is (BigInt) {}", Util.lazyAsBigInt(chiffre));
        }
        if (chiffre.length != keyByteLength) {
            throw new IllegalStateException("Länge des Kryptogramms nicht gleich Moduluslänge");
        }
        log.debug("sending chiffre to SAM...");
        handleExternalAuthenticate(connection, chiffre);
        return kExtern;
    }

    private static byte[] sha1Hash(final byte[]... inputs) {
        final byte[] hash = new byte[20];
        final SHA1Digest digest = new SHA1Digest();
        for (byte[] input : inputs) {
            digest.update(input, 0, input.length);
        }
        digest.doFinal(hash, 0);
        return hash;
    }

    private static void handleExternalAuthenticate(final ICardConnection connection, final byte[] commandData)
            throws CardException, SamException {
        final ResponseAPDU response = transmit(connection, new RequestAPDU(0x00, 0x82, 0x00, 0x00, commandData));
        final int sw = response.getSw();
        switch (sw) {
            case 0x9000:
                // SUCCESS
                break;
            case 0x6400:
                throw new SamException("EXTERNAL AUTHENTICATE: Execution error, state of non-volatile memory "
                        + "unchanged", sw);
            case 0x6581:
                throw new SamException("EXTERNAL AUTHENTICATE: Memory failure", sw);
            case 0x6700:
                throw new SamException("EXTERNAL AUTHENTICATE: Wrong length", sw);
            case 0x6985:
                throw new
                        ConditionsOfUseNotSatisfiedException("EXTERNAL AUTHENTICATE: Conditions of use not satisfied");
            case 0x6988:
                throw new WrongSamKeyException("EXTERNAL AUTHENTICATE: SM data objects incorrect");
            case 0x6a80:
                throw new SamException("EXTERNAL AUTHENTICATE: Incorrect parameters in the data field", sw);
            case 0x6a86:
                throw new SamException("EXTERNAL AUTHENTICATE: Incorrect parameters P1-P2", sw);
            case 0x6a87:
                throw new SamException("EXTERNAL AUTHENTICATE: Falsche Länge der Authentisierungsdaten", sw);
            case 0x6a88:
                throw new SamException("EXTERNAL AUTHENTICATE: Referenced data not found", sw);
            case 0x6e00:
                throw new SamException("EXTERNAL AUTHENTICATE: CLA (for INS) not supported", sw);
            default:
                throw new SamException("EXTERNAL AUTHENTICATE: "
                        + Integer.toHexString(sw), sw);
        }
        // no data contained
    }

    private static SessionInfo internalAuthenticate(final ICardConnection connection, final RSAKeyParameters userKey,
                                                    final byte[] userKeyCHR, final SamCVKey samAuthKey,
                                                    final byte[] cSAM, final byte[] kExtern)
            throws CardException, SamException {
        final Random random = new Random();
        final ByteBuffer commandData = ByteBuffer.allocate(16);
        final byte[] cExtern = new byte[8];
        random.nextBytes(cExtern);
        commandData.put(cExtern);
        final byte[] idExtern = Arrays.copyOfRange(userKeyCHR, 4, 12);
        commandData.put(idExtern);
        final int keyLengthInBytes = userKey.getModulus().bitLength() / 8;
        final byte[] responseData = handleInternalAuthenticate(
                connection,
                commandData.array(),
                keyLengthInBytes
        );
        final byte[] sigMinSAM;
        {
            final RSAEngine rsaEngine = new RSAEngine();
            rsaEngine.init(false, userKey);
            sigMinSAM = rsaEngine.processBlock(responseData, 0, responseData.length);
        }
        log.debug("SigMin_SAM is (byte[]) {}", Util.lazyAsHexString(sigMinSAM));
        final byte[] m;
        {
            final RSAEngine rsaEngine = new RSAEngine();
            rsaEngine.init(false, Util.getRSAPublicKeyParameters(samAuthKey));
            byte[] jstar = rsaEngine.processBlock(sigMinSAM, 0, sigMinSAM.length);
            if ((jstar[jstar.length - 1] & 0x0F) != 0x0C) {
                log.debug("Sig_SAM not ending in C, trying Sig*_SAM");
                jstar = Util.toBigInt(samAuthKey.getModulus()).subtract(Util.toBigInt(jstar)).toByteArray();
            }
            if ((jstar[jstar.length - 1] & 0x0F) != 0x0C) {
                log.debug(asHexString(jstar));
                throw new IllegalArgumentException("data still not ending with 'C'!");
            }
            m = jstar;
        }

        final int prndLength = keyLengthInBytes - 20 - 32 - 2;
        final byte[] kSAM = checkInternalAuthenticateResponseAndReturnKSAM(m, prndLength, cExtern, idExtern);
        if (null == kSAM) {
            throw new IllegalArgumentException("Invalid Signature!");
        }
        return new SessionInfo(kSAM, kExtern, cSAM, cExtern);
    }

    private static byte[] checkInternalAuthenticateResponseAndReturnKSAM(final byte[] m, final int prndLength,
                                                                         final byte[] cExtern, final byte[] idExtern) {
        final ByteBuffer buffer = ByteBuffer.wrap(m);
        final byte header = buffer.get();
        if (header != (byte) 0x6A) {
            log.debug(asHexString(m));
            throw new IllegalArgumentException("Header 0x6A not found, but "
                    + UnsignedBytes.toString(header, 16)
                    + " instead!");
        }
        final byte[] prnd = new byte[prndLength];
        buffer.get(prnd);
        final byte[] kSAM = new byte[32];
        buffer.get(kSAM);
        final byte[] hashExpected = sha1Hash(prnd, kSAM, cExtern, idExtern);
        final byte[] hashRead = new byte[hashExpected.length];
        buffer.get(hashRead);
        if (!Arrays.equals(hashExpected, hashRead)) {
            throw new IllegalArgumentException("Hash did not match! ("
                    + asHexString(hashExpected) + " != "
                    + asHexString(hashRead) + ")");
        }
        final byte trailer = buffer.get();
        if (trailer != (byte) 0xBC) {
            throw new IllegalArgumentException("Trailer 0xBC not found, but "
                    + UnsignedBytes.toString(trailer, 16)
                    + " instead!");
        }
        return kSAM;
    }

    private static byte[] handleInternalAuthenticate(final ICardConnection connection, final byte[] commandData,
                                                     final int le)
            throws CardException, SamException {
        final ResponseAPDU response = transmit(connection, new RequestAPDU(0x00, 0x88, 0x00, 0x00, commandData, le));
        final int sw = response.getSw();
        switch (sw) {
            case 0x9000:
                // SUCCESS
                break;
            case 0x6400:
                throw new SamException("INTERNAL AUTHENTICATE: Execution error, state of non-volatile memory "
                        + "unchanged", sw);
            case 0x6581:
                throw new SamException("INTERNAL AUTHENTICATE: Memory failure", sw);
            case 0x6700:
                throw new SamException("INTERNAL AUTHENTICATE: Wrong length", sw);
            case 0x6985:
                throw new
                        ConditionsOfUseNotSatisfiedException("INTERNAL AUTHENTICATE: Conditions of use not satisfied");
            case 0x6a80:
                throw new SamException("INTERNAL AUTHENTICATE: Incorrect parameters in the data field", sw);
            case 0x6a86:
                throw new SamException("INTERNAL AUTHENTICATE: Incorrect parameters P1-P2", sw);
            case 0x6a87:
                throw new SamException("INTERNAL AUTHENTICATE: Falsche Länge der Authentisierungsdaten", sw);
            case 0x6a88:
                throw new SamException("INTERNAL AUTHENTICATE: Referenced data not found", sw);
            case 0x6e00:
                throw new SamException("INTERNAL AUTHENTICATE: CLA (for INS) not supported", sw);
            default:
                throw new SamException("INTERNAL AUTHENTICATE: " + Integer.toHexString(sw), sw);
        }
        return response.getData();
    }
}
