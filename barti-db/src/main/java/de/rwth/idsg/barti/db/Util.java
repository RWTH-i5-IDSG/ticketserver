package de.rwth.idsg.barti.db;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTimeZone;

import java.sql.Timestamp;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@UtilityClass
@Log4j2
public class Util {
    public static java.sql.Timestamp convertToUTC(final org.joda.time.LocalDateTime timestamp) {
        return new Timestamp(timestamp.toDateTime(DateTimeZone.UTC).getMillis());
    }

    public static final int DEPLOYMENT = determineDeployment();

    private static int determineDeployment() {
        final String deploymentString = System.getProperty("deployment");
        if (null != deploymentString) {
            try {
                return Integer.parseInt(deploymentString);
            } catch (final NumberFormatException ignored) {
            }
        }
        log.error("Deployment not specified, assuming deployment id 1");
        return 1;
    }
}
