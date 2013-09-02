package de.codesourcery.lsystems.dsl.exceptions;

import de.codesourcery.lsystems.dsl.Identifier;
import de.codesourcery.lsystems.dsl.nodes.ASTNode;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/2/13
 * Time: 9:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnknownIdentifierException extends RuntimeException {

    private final Identifier identifier;

    public UnknownIdentifierException(Identifier identifier) {
        super("Unknown identifier: " + identifier);
        this.identifier = identifier;
    }
}
