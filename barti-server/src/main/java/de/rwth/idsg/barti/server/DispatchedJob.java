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
@EqualsAndHashCode(callSuper = true)
@ToString
public class DispatchedJob extends ConfiguredJob {
    final long ticketNumber;
    final String ticketIdentifier;

    public DispatchedJob(final IncomingJob originalJob,
                         final ProductConfiguration productConfiguration,
                         final long ticketNumber) {
        super(originalJob, productConfiguration);
        this.ticketNumber = ticketNumber;
        this.ticketIdentifier = productConfiguration.getKvp().getOrgIdAsInt() + "." + ticketNumber;
    }

    @Override
    public DispatchedJob toDispatchedJob(final LongSupplier ticketNumberSupplier) {
        return this;
    }
}
