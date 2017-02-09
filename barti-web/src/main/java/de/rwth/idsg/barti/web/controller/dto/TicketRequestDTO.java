package de.rwth.idsg.barti.web.controller.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@Setter
public class TicketRequestDTO {

    @NotNull(message = "api token may not be null")
    private String apiToken;

    @NotNull(message = "begin date may not be null")
    private LocalDateTime begin;

    @NotNull(message = "end date may not be null")
    private LocalDateTime end;

    @NotEmpty(message = "iata code may not be null or empty")
    private String iata;

    @NotEmpty(message = "name may not be null or empty")
    private String name;

    // -------------------------------------------------------------------------
    // @AssertTrue annotated method names must start with "isValid"! Otherwise,
    // the method is not called. Welcome to the funny world of web programming
    // with Spring, Hibernate validator and co.
    //
    // WTF?!
    // -------------------------------------------------------------------------

    @AssertTrue(message = "end date has to be after begin")
    public boolean isValidDatesInOrder() {
        return end != null && begin != null && end.isAfter(begin);
    }

    @AssertTrue(message = "end date has to be in the future")
    public boolean isValidEndInFuture() {
        return end != null && end.isAfter(LocalDateTime.now());
    }
}
