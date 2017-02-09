package de.rwth.idsg.barti.server;

import de.rwth.idsg.barti.core.aggregate.ProductConfiguration;
import de.rwth.idsg.barti.db.PseudoSequence;
import de.rwth.idsg.barti.db.repository.BasicRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
public class Main {

    public static void main(final String... args) {
        final AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext();
        springContext.scan("de.rwth.idsg.barti.db", "de.rwth.idsg.pcsctest");
        springContext.refresh();
        springContext.start();

        final SamServer samServer = (SamServer) springContext.getBean("samServer");

    }
}
