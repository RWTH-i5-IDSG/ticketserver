package de.rwth.idsg.barti.sam;

import de.intarsys.security.smartcard.card.CardException;
import de.intarsys.security.smartcard.card.ICardConnection;
import de.intarsys.security.smartcard.card.ICardTerminal;
import de.intarsys.security.smartcard.card.standard.StandardCardSystem;
import de.intarsys.security.smartcard.pcsc.PCSCContextFactory;
import de.intarsys.security.smartcard.pcsc.PCSCException;
import de.intarsys.security.smartcard.pcsc.nativec._PCSC;
import de.rwth.idsg.barti.core.aggregate.Organisation;
import de.rwth.idsg.barti.core.aggregate.Partner;
import de.rwth.idsg.barti.core.aggregate.ProductConfiguration;
import de.rwth.idsg.barti.core.aggregate.STBParameters;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberOne;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberThree;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberTwo;
import de.rwth.idsg.barti.core.datatypes.enums.TransportmittelKategorieCode;
import de.rwth.idsg.barti.core.datatypes.pki.BetreiberCHR;
import de.rwth.idsg.barti.core.datatypes.pki.KeyInfo;
import de.rwth.idsg.barti.core.datatypes.pki.SamCVKey;
import de.rwth.idsg.barti.sam.communication.SamInfo;
import de.rwth.idsg.barti.sam.communication.SignEntitlement;
import de.rwth.idsg.barti.sam.exception.SamException;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.joda.time.LocalDateTime;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static de.rwth.idsg.barti.sam.communication.Authenticate.authenticate;
import static de.rwth.idsg.barti.sam.communication.ManageSecurityEnvironment.manageSecurityEnvironment;
import static de.rwth.idsg.barti.sam.communication.ReadRecord.*;
import static de.rwth.idsg.barti.sam.communication.SelectApplication.selectApplication;
import static java.util.stream.Collectors.toList;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
public class SamCommunication {
    public static SamInfo setupConnection(final RSAKeyParameters userKey, final ReferenceNumberTwo pvOrgID,
                                          final ReferenceNumberOne pvKeyVersion)
            throws SamException, PCSCException, de.intarsys.security.smartcard.card.CardException {
        log.debug("Setting up connection...");
        final StandardCardSystem cardSystem = new StandardCardSystem(PCSCContextFactory.get());
        final ICardTerminal[] terminals = cardSystem.getCardTerminals();
        ICardConnection connection = null;
        try {
            final List<ICardTerminal> activeTerminals = Arrays
                    .stream(terminals).filter(t -> t.getCard() != null)
                    .collect(toList());
            log.debug("Checking active card terminals...");
            if (activeTerminals.size() == 0) {
                log.error("No card terminals with inserted cards found!");
                return null;
            }
            for (final ICardTerminal activeTerminal : activeTerminals) {
                try {
                    connection = activeTerminal.getCard().connectExclusive(_PCSC.SCARD_PROTOCOL_Tx);
                    break;
                } catch (final CardException e) {
                    log.info(e.getMessage(), e);
                }
            }
            if (null == connection) {
                log.error("None of the active terminals successfully opened a connection to the card contained!");
                return null;
            }

            return initialiseSAM(userKey, pvOrgID, pvKeyVersion, connection);
        } catch (final CardException e) {
            log.error(e);
            return null;
        }
    }

    private static SamInfo initialiseSAM(RSAKeyParameters userKey, ReferenceNumberTwo pvOrgID, ReferenceNumberOne
            pvKeyVersion, ICardConnection connection) throws CardException, SamException {
        log.debug("selecting application...");
        selectApplication(connection);

        final EfSamID efSamID = readSamID(connection);
        log.error(efSamID.toString());
        final List<KeyInfo> pvMKs = readPVMKs(connection);
        for (final KeyInfo pvmk : pvMKs) {
            log.error("PV MK available: {}", pvmk);
            if (Objects.equals(pvOrgID, pvmk.getOrgID()) && Objects.equals(pvKeyVersion, pvmk.getKeyVersion())) {
                log.error("Found configured PV MK!");
            }
        }

        log.debug("reading Betreiber CHR...");
        final BetreiberCHR betreiberCHR = readBetreiberCHR(connection);
        log.debug("selecting Betreiber key...");
        final byte[] betreiberCHRRaw = betreiberCHR.write().clone();
        log.debug("Betreiber Key CHR        is {}", Util.lazyAsHexString(betreiberCHRRaw));
        log.debug("Betreiber Key modulus    is {}", Util.lazyAsHexString(userKey.getModulus()));
        log.debug("Betreiber Key exponent   is {}", Util.lazyAsHexString(userKey.getExponent()));

        manageSecurityEnvironment(connection, betreiberCHRRaw);

        log.debug("starting authentication...");
        authenticate(connection, userKey, betreiberCHRRaw);

        log.debug("reading SAM CERT SIG...");
        final SamCVKey samSigKey = readCertSig(connection).getKey();
        log.debug("SAM CERT SIG is: {}", samSigKey);
        return new SamInfo(connection, efSamID, betreiberCHR, pvMKs, samSigKey);
    }

    public static void main(final String[] args) throws CardException, SamException, PCSCException {
        final RSAKeyParameters userKey = new RSAKeyParameters(true,
                new BigInteger(
                        "d0e25c29e2a004c4f7f7c62b62f63bae9c872f6d37e742991272cf43c4a4ca5ed74ca2a503da0f15308aec406d0d2b26826a2f0c7ad287e52ce297b80a0b158f45860e02a2a36890964b997031e8dd27da9991eac13ea8ce5832173027ecb2684f8abcd99e581c8258b11ea44673945e76fdff40fa6196168e0d262829ac3e87",
                        16
                ),
                new BigInteger(
                        "2a9aeffa255bf11329d8337db856f81cd02011eeafc01ca82be741d5b28c229224b48828faeb9b22b9e864261028f4d0c77a8ec29931f72bf6a745d3ce04f159180dc33f779dd0658b6542fc965eb0691c7515511f931a668a9b320ef8ed70fe02599334e24fcf7b5f610be4945a160d83f81aefcfbcb4934640f0e3c896f1a1",
                        16
                )
        );
        final Organisation pv = new Organisation(new ReferenceNumberTwo(0x88_2A), "PV");
        final Organisation kvp = new Organisation(new ReferenceNumberTwo(1), "KVP");
        final Organisation transOp = new Organisation(new ReferenceNumberTwo(2), "TransactionOperator");
        final ReferenceNumberTwo pvOrgID = pv.getOrgId();
        final ReferenceNumberOne pvKeyVersion = new ReferenceNumberOne((short) 1);
        final SamInfo samInfo = setupConnection(userKey, pvOrgID, pvKeyVersion);
        final ProductConfiguration productConfiguration =
                ProductConfiguration.builder()
                        .deploymentId(1)
                        .productId(new ReferenceNumberTwo(1))
                        .pv(pv)
                        .kvp(kvp)
                        .transactionOp(transOp)
                        .locationOrg(kvp)
                        .terminalOrg(kvp)
                        .terminalNumber(new ReferenceNumberTwo(1))
                        .locationNumber(new ReferenceNumberThree(3))
                        .meansOfTransportCategory(TransportmittelKategorieCode.NICHT_SPEZIFIZIERT_UNBESTIMMT)
                        .partner(new Partner(1, "Partner1"))
                        .apiToken("ApiToken")
                        .build();
        final STBParameters stbParameters = new STBParameters("", LocalDateTime.now(), LocalDateTime.now().plusHours
                (9));
        final byte[] samSigKeyCHR = samInfo.getSamSigKey().getChr().write();
        SignEntitlement.signEntitlement(samInfo.getConnection(), samInfo.getKeyForPv(pvOrgID),
                samSigKeyCHR, 1,
                stbParameters, productConfiguration);
    }
}
