package de.rwth.idsg.barti.web.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class PictureProxy {
    final byte[] signature;
}
