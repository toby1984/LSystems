package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.Identifier;
import de.codesourcery.lsystems.dsl.ParseContext;
import de.codesourcery.lsystems.dsl.ParsedTokenType;
import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 6:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdentifierNode extends ASTNode implements TermNode
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

    @Override
    public String toDebugString() {
        return value.toString();
    }

    @Override
    public double evaluate(ExpressionContext context) throws UnknownIdentifierException
    {
        ASTNode value = context.lookup(this.value);
        if ( value instanceof TermNode) {
            return ((TermNode) value).evaluate( context );
        }
        throw new RuntimeException("Don't know how to evaluate "+value);
    }
}
