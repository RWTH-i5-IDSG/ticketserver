package de.rwth.idsg.barti.web;

import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.time.ZoneOffset;
import java.util.TimeZone;


/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
public class Application {

    public static void main(final String... args) throws Exception {
        // For Hibernate validator
        System.setProperty("org.jboss.logging.provider", "log4j2");

        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
        DateTimeZone.setDefault(DateTimeZone.UTC);
        log.info("Date/time zone of the application is set to UTC. Current date/time: {}", DateTime.now());
        final JettyServer js = new JettyServer();
        js.prepare();
        js.start();
        js.join();
    }
}
