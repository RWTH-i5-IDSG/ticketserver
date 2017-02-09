package de.rwth.idsg.barti.core.datatypes.pki;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class CHA implements Data<CHA> {
    public static final byte[] VDV_KA = new byte[]{
            (byte) 0x56, (byte) 0x44, (byte) 0x56, (byte) 0x5F, (byte) 0x4B, (byte) 0x41
    };
    public static final int CHA_LENGTH = 7;

    public static final ReadDescription<CHA> READ_DESCRIPTION = stream -> {
        final byte[] vdvKa = stream.readBytes(VDV_KA.length);
        if (!Arrays.equals(vdvKa, VDV_KA)) {
            throw new IllegalStateException();
        }
        final byte twoNibbles = stream.read1ByteSigned();
        final Command command = getCommand((byte) ((twoNibbles >> 4) & 0xF));
        final Role role = getRole((byte) (twoNibbles & 0xF));
        return new CHA(command, role);
    };

    @Override public ReadDescription<CHA> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.writeBytes(VDV_KA);
        final byte twoNibbles = (byte) (command.getValue() << 4 | role.getValue());
        stream.writeByte(twoNibbles);
    }

    @Override
    public int getLength() {
        return CHA_LENGTH;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Command {
        VERIFY_KEY(1), VERIFY_DIGITAL_SIGNATURE(2), VERIFY_CERTIFICATE(3), AUTHENTICATE(4), ENCRYPT_KEY(5);
        final int value;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Role {
        CA(0), NM(1), SAM_ALMIGHTY(2), SAM_NO_SALES(4), SAM_NO_SALES_2(8), TAG(9),
        SECURITY_MANAGEMENT_CLIENT(10), SECURITY_MANAGEMENT_KA(15);
        final int value;
    }

    final Command command;
    final Role role;

    private static Command getCommand(final byte commandNibble) {
        for (Command command : Command.values()) {
            if (command.getValue() == commandNibble) {
                return command;
            }
        }
        throw new UnsupportedOperationException();
    }

    private static Role getRole(final byte roleNibble) {
        for (Role role : Role.values()) {
            if (role.getValue() == roleNibble) {
                return role;
            }
        }
        throw new UnsupportedOperationException();
    }
}
