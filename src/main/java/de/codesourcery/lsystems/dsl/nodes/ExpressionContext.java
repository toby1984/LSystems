package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;
import de.codesourcery.lsystems.dsl.symbols.Identifier;
import de.codesourcery.lsystems.dsl.symbols.Scope;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public interface ExpressionContext {

    public ASTNode lookup(Identifier identifier, Scope scope, boolean searchParentScopes) throws UnknownIdentifierException;
}
