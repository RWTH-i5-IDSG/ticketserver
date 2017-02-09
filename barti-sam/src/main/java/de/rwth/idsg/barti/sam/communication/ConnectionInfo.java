package de.rwth.idsg.barti.sam.communication;

import de.intarsys.security.smartcard.card.ICardConnection;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberOne;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberTwo;
import lombok.Value;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Value
public class ConnectionInfo {
    ICardConnection connection;
    ReferenceNumberTwo pvOrgID;
    ReferenceNumberOne pvKeyVersion;
    final byte[] samSigKeyCHR;
}
