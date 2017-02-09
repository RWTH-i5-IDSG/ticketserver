package de.rwth.idsg.barti.sam;

import de.rwth.idsg.barti.core.datatypes.pki.CVKey;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.crypto.params.RSAKeyParameters;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import static de.rwth.idsg.barti.core.Util.asHexString;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
@UtilityClass
public class Util {
    public static byte[] fixBigIntTwoComplement(final byte[] array, final int targetByteLength) {
        if (array.length < targetByteLength) {
            final byte[] fix = new byte[targetByteLength];
            System.arraycopy(array, 0, fix, targetByteLength - array.length, array.length);
            return fix;
        }
        if (array.length > targetByteLength) {
            if (array.length > targetByteLength + 1 || ((byte) 0) != array[0]) {
                throw new IllegalStateException("Number too large to fit into " + targetByteLength + " bytes!");
            }
            // ended up with an extra zero byte, remove it
            final byte[] fix = new byte[targetByteLength];
            System.arraycopy(array, 0, fix, 1, array.length);
            return fix;
        }
        return array;
    }

    public static BigInteger toBigInt(final byte[] arrayValue) {
        return new BigInteger(1, arrayValue);
    }

    public static Object lazyAsBigInt(final byte[] arrayValue) {
        return new Object() {
            @Override
            public String toString() {
                return toBigInt(arrayValue).toString();
            }
        };
    }

    public static Object lazyAsHexString(final byte[] array) {
        return new Object() {
            @Override
            public String toString() {
                return asHexString(array);
            }
        };
    }

    public static Object lazyAsHexString(final BigInteger value) {
        return new Object() {
            @Override
            public String toString() {
                return asHexString(value.toByteArray());
            }
        };
    }

    public static RSAPublicKey getRSAPublicKey(final CVKey<?, ?> key) {
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(new RSAPublicKeySpec(
                    Util.toBigInt(key.getModulus()),
                    Util.toBigInt(key.getExponent())
            ));
        } catch (final NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new Error(e);
        }
    }

    public static RSAKeyParameters getRSAPublicKeyParameters(final CVKey<?, ?> key) {
        return new RSAKeyParameters(false, Util.toBigInt(key.getModulus()), Util.toBigInt(key.getExponent()));
    }

    public static RSAPrivateKey getRSAPrivateKey(final CVKey<?, ?> key) {
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(new RSAPrivateKeySpec(
                    Util.toBigInt(key.getModulus()),
                    Util.toBigInt(key.getExponent())
            ));
        } catch (final NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new Error(e);
        }
    }

    public static RSAKeyParameters getRSAPrivateKeyParameters(final CVKey<?, ?> key) {
        return new RSAKeyParameters(true, Util.toBigInt(key.getModulus()), Util.toBigInt(key.getExponent()));
    }
}
