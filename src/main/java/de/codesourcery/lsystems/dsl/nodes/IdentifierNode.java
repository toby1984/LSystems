package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.Identifier;
import de.codesourcery.lsystems.dsl.ParseContext;
import de.codesourcery.lsystems.dsl.ParsedTokenType;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 6:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdentifierNode extends ASTNode
{
    public Identifier value;

    @Override
    public ASTNode parse(ParseContext context)
    {
        this.value = new Identifier( context.next(ParsedTokenType.IDENTIFIER).value );
        return this;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
