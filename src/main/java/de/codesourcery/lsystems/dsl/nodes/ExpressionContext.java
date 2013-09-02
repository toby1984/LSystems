package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.Identifier;
import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/2/13
 * Time: 9:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ExpressionContext {

    public ASTNode lookup(Identifier identifier) throws UnknownIdentifierException;
}
