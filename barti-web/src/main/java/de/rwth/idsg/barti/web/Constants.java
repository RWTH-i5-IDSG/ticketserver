package de.rwth.idsg.barti.web;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @since 03.03.2016
 */
public final class Constants {
    public static final String SECURITY_API_KEY = "46fd1c14-a985-4053-bc22-708f45b7d971";

    private Constants() { }

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
}
