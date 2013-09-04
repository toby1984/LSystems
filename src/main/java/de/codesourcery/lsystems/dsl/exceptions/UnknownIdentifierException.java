package de.codesourcery.lsystems.dsl.exceptions;

import de.codesourcery.lsystems.dsl.Identifier;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class UnknownIdentifierException extends RuntimeException {

    private final Identifier identifier;

    public UnknownIdentifierException(Identifier identifier) {
        super("Unknown identifier: " + identifier);
        this.identifier = identifier;
    }
}
