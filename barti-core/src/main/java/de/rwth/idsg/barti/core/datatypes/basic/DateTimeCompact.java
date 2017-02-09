package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import org.joda.time.LocalDateTime;

import java.io.IOException;

/**
 * DateTimeCompact enthält ein Kalenderdatum und eine Uhrzeit an diesem Kalenderdatum.
 * <br/>
 * Zur Codierung wird eine Kombination aus einem DateCompact und einem TimeCompact verwendet.
 * <br/>
 * Bei der Konvertierung in bzw. aus der XML-Darstellung entspricht der Wert ‘0000 ... 0000’B dem Wert
 * "1990-01-01T00:00:00"XML und ist von den und ist von den Anwendungssystemen als "keine Angabe" zu interpretieren.
 * <br/>
 * Ein DateTimeCompact kann als BitString(32) dargestellt werden, wobei die vorderen 16 Bits dem enthaltenen
 * DateCompact
 * entsprechen und die hinteren 16 Bits dem enthaltenen TimeCompact.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class DateTimeCompact implements Data<DateTimeCompact> {

    static final int YEAR_MASK = 0b1111_1110_0000_0000_0000_0000_0000_0000;
    static final int MNTH_MASK = 0b0000_0001_1110_0000_0000_0000_0000_0000;
    static final int DAYM_MASK = 0b0000_0000_0001_1111_0000_0000_0000_0000;
    static final int HOUR_MASK = 0b0000_0000_0000_0000_1111_1000_0000_0000;
    static final int MINT_MASK = 0b0000_0000_0000_0000_0000_0111_1110_0000;
    static final int SECN_MASK = 0b0000_0000_0000_0000_0000_0000_0001_1111;

    public static final ReadDescription<DateTimeCompact> READ_DESCRIPTION = stream -> {
        final int read = stream.read4ByteSigned();
        if (read == 0) {
            return new DateTimeCompact(null);
        }
        // 7 bits year
        final int year = ((read & YEAR_MASK) >> (16 + 4 + 5)) + 1990;
        // 4 bits month
        final int month = (read & MNTH_MASK) >> (16 + 5);
        // 5 bits day
        final int dayOM = (read & DAYM_MASK) >> 16;
        // 5 bits hour
        final int hour = (read & HOUR_MASK) >> (5 + 6);
        // 6 bits minute
        final int minute = (read & MINT_MASK) >> 5;
        // 5 bits (double)seconds
        final int second = (read & SECN_MASK) * 2;
        return new DateTimeCompact(new LocalDateTime(year, month, dayOM, hour, minute, second));
    };

    final LocalDateTime value;

    @Override public ReadDescription<DateTimeCompact> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        if (null == value) {
            stream.write4ByteSigned(0);
            return;
        }
        int result = 0;
        // 7 bits year
        final long year = value.getYear() - 1990;
        result |= YEAR_MASK & (year << (16 + 4 + 5));
        // 4 bits month
        final long month = value.getMonthOfYear();
        result |= MNTH_MASK & (month << (16 + 5));
        // 5 bits day
        final long day = value.getDayOfMonth();
        result |= DAYM_MASK & (day << 16);
        // 5 bits hour
        final long hour = value.getHourOfDay();
        result |= HOUR_MASK & (hour << (5 + 6));
        // 6 bits minute
        final long minute = value.getMinuteOfHour();
        result |= MINT_MASK & (minute << 5);
        // 5 bits (double)seconds
        final long second = value.getSecondOfMinute() / 2;
        result |= SECN_MASK & (second);

        stream.write4ByteSigned(result);
    }

    @Override
    public int getLength() {
        return 4;
    }
}
