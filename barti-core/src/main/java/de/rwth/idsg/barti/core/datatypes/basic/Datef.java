package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;

import java.io.IOException;
import org.joda.time.LocalDate;

/**
 * Datef ist eine BCD-Darstellung für Kalenderdaten.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class Datef implements Data<Datef> {

    public static final ReadDescription<Datef> READ_DESCRIPTION = stream ->
    {
        final BCDString yearHi = BCDString.READ_DESCRIPTION.read(stream);
        final BCDString yearLo = BCDString.READ_DESCRIPTION.read(stream);
        final BCDString month = BCDString.READ_DESCRIPTION.read(stream);
        final BCDString day = BCDString.READ_DESCRIPTION.read(stream);
        final byte decYearHi = yearHi.getDecimal();
        final byte decYearLo = yearLo.getDecimal();
        final byte decMonth = month.getDecimal();
        final byte decDay = day.getDecimal();
        if (decYearHi == 0 && decYearLo == 0 && decMonth == 0 && decDay == 0) {
            return new Datef(null);
        }
        return new Datef(new LocalDate(decYearHi * 100 + decYearLo, decMonth, decDay));
    };

    final LocalDate date;

    @Override
    public ReadDescription<Datef> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        if (null == date) {
            stream.writeBytes(new byte[getLength()]);
        }
        BCDString.fromDecimal(date.getYear() / 100).write(stream);
        BCDString.fromDecimal(date.getYear() % 100).write(stream);
        BCDString.fromDecimal(date.getMonthOfYear()).write(stream);
        BCDString.fromDecimal(date.getDayOfMonth()).write(stream);
    }

    @Override
    public int getLength() {
        return 4;
    }
}
