package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import org.joda.time.LocalDate;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class JJMM implements Data<JJMM> {

    public static final ReadDescription<JJMM> READ_DESCRIPTION = stream ->
    {
        final byte year = BCDString.READ_DESCRIPTION.read(stream).getDecimal();
        final byte month = BCDString.READ_DESCRIPTION.read(stream).getDecimal();
        if (year == 0 && month == 0) {
            return new JJMM(null);
        }
        return new JJMM(new LocalDate(2000 + year, month, 1));
    };

    final LocalDate date;

    @Override public ReadDescription<JJMM> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        if (null == date) {
            stream.writeBytes(new byte[getLength()]);
        }
        BCDString.fromDecimal(date.getYear() - 2000).write(stream);
        BCDString.fromDecimal(date.getMonthOfYear()).write(stream);
    }

    @Override
    public int getLength() {
        return 2;
    }
}
