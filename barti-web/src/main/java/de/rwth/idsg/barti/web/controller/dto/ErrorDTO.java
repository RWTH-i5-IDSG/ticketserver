package de.rwth.idsg.barti.web.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 04.03.2016
 */
@Getter
@Setter
public class ErrorDTO {

    private List<String> errorMessages = new ArrayList<>();

    public void addErrorMessage(final String msg) {
        this.errorMessages.add(msg);
    }
}
