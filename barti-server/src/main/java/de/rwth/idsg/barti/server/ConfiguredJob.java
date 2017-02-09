package de.rwth.idsg.barti.server;

import de.rwth.idsg.barti.core.aggregate.ProductConfiguration;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.function.LongSupplier;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@EqualsAndHashCode
@ToString
public class ConfiguredJob {
    final ProductConfiguration productConfiguration;
    final IncomingJob originalJob;
    int failureCounter;

    public ConfiguredJob(final IncomingJob originalJob,
                         final ProductConfiguration productConfiguration) {
        this.originalJob = originalJob;
        this.productConfiguration = productConfiguration;
    }

    public DispatchedJob toDispatchedJob(final LongSupplier ticketNumberSupplier) {
        return new DispatchedJob(this.originalJob, this.productConfiguration, ticketNumberSupplier.getAsLong());
    }
}
