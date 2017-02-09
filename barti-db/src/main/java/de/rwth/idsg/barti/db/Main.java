package de.rwth.idsg.barti.db;

import de.rwth.idsg.barti.core.aggregate.ProductConfiguration;
import de.rwth.idsg.barti.db.repository.BasicRepository;
import lombok.extern.log4j.Log4j2;
import org.jooq.Sequence;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
public class Main {

    public static void main(final String... args) {
        final AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext();
        springContext.scan("de.rwth.idsg.barti.db");
        springContext.refresh();
        springContext.start();

        final BasicRepository repo = (BasicRepository) springContext.getBean("basicRepositoryImpl");
        final List<ProductConfiguration> allConfigurationsForDeployment = repo
                .getAllConfigurationsForDeployment(1);
        System.out.println("Configured products found: " + allConfigurationsForDeployment.size());

        final List<PseudoSequence> ticketSequenceCounters = repo.getAllTicketSequenceCountersForDeployment(1);

        if (!ticketSequenceCounters.isEmpty()) {
            log.info("There is at least one sequence!");
            final PseudoSequence pseudoSequence = ticketSequenceCounters.get(0);
            final long nextTicketNumber = repo.getNextTicketNumber(pseudoSequence);
            System.out.println(nextTicketNumber);
        }
        // System.out.println(repo.getNextTicketNumber(ticketSequenceCounter));
        // System.out.println(repo.getNextTicketNumber(ticketSequenceCounter));
        // System.out.println(repo.getNextTicketNumber(ticketSequenceCounter));

    }
}
