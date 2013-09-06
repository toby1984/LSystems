package de.codesourcery.lsystems.dsl.exceptions;

import de.codesourcery.lsystems.dsl.symbols.Identifier;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class UnknownIdentifierException extends RuntimeException {

    private final Identifier identifier;

    public UnknownIdentifierException(Identifier identifier) {
        this("Unknown identifier: "+identifier,identifier);
    }
    public UnknownIdentifierException(String message, Identifier identifier) {
        super( message );
        this.identifier = identifier;
    }
    
    public Identifier getIdentifier() {
		return identifier;
	}
}
