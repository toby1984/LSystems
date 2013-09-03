package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.Identifier;
import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public interface ExpressionContext {

    public ASTNode lookup(Identifier identifier) throws UnknownIdentifierException;
}
