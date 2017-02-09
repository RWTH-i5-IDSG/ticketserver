package de.rwth.idsg.barti.sam;

import de.rwth.idsg.barti.core.datatypes.basic.*;
import de.rwth.idsg.barti.core.datatypes.composite.*;
import de.rwth.idsg.barti.core.datatypes.enums.OrtsTypCode;
import de.rwth.idsg.barti.core.datatypes.enums.TerminalTypCode;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDateTime;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class STBCreator {
    private static final ReferenceNumberFour BER_PROD_LOG_SAM_SEQ_NUMMER = new ReferenceNumberFour(0L);
    private static final OctetStringOne VERSION_K_PV = new OctetStringOne((byte) 0);
    private static final SequenceNumberFour SAM_SEQUENZNUMMER = new SequenceNumberFour(0L);
    private static final ReferenceNumberThree SAM_ID_SAM_NUMMER = new ReferenceNumberThree(0);

    public static STB createSTB(
            final long ticketSequenceNumber,
            final de.rwth.idsg.barti.core.aggregate.STBParameters parameters,
            final de.rwth.idsg.barti.core.aggregate.ProductConfiguration productConfiguration) {
        return new STB(
                new BerechtigungID(
                        new ReferenceNumberFour(ticketSequenceNumber),
                        productConfiguration.getKvp().getOrgId()
                ),
                new EFMProduktID(
                        productConfiguration.getProductId(),
                        productConfiguration.getPv().getOrgId()),
                parameters.getBerGueltigkeitsbeginn(),
                parameters.getBerGueltigkeitsende(),
                new SeparateDatenBerechtigungEFSStatischerProduktspezifischerTeil(
                        new PrintableStringWithPrecedingOneByteSize(parameters.getFreitext().toCharArray())),
                productConfiguration.getTransactionOp().getOrgId(),
                new TerminalID(
                        TerminalTypCode.EONLINE_TICKETSERVER,
                        productConfiguration.getTerminalNumber(),
                        productConfiguration.getTerminalOrg().getOrgId()),
                new DateTimeCompact(LocalDateTime.now()),
                new OrtID(
                        OrtsTypCode.TICKETSERVER,
                        productConfiguration.getLocationNumber(),
                        productConfiguration.getLocationOrg().getOrgId()),
                new TransaktionProduktspezifischerTeil(productConfiguration.getMeansOfTransportCategory()),
                BER_PROD_LOG_SAM_SEQ_NUMMER,
                VERSION_K_PV,
                SAM_SEQUENZNUMMER,
                SAM_ID_SAM_NUMMER
        );
    }
}
