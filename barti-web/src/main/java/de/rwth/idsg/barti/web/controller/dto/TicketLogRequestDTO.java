package de.rwth.idsg.barti.web.controller.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.YearMonth;

import javax.validation.constraints.NotNull;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@Setter
public class TicketLogRequestDTO {

    @NotNull(message = "api token may not be null")
    private String apiToken;

    @NotNull(message = "yearMonth may not be null")
    private YearMonth yearMonth;
}
