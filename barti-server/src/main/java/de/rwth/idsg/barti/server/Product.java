package de.rwth.idsg.barti.server;

import lombok.Value;

/**
 * An API token uniquely identifies the tuple of partner and product.
 * The concept of a partner is only relevant for logging and billing.
 * The product currently only determines the product ID, but may in the future as well determine whether the free text
 * version or the structured version is used and therefore identifies the parameter list of the interface and their
 * types.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Value
public class Product {
    final int productID;
}
