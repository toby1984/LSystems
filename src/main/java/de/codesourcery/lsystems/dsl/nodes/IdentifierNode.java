package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.Identifier;
import de.codesourcery.lsystems.dsl.ParseContext;
import de.codesourcery.lsystems.dsl.ParsedToken;
import de.codesourcery.lsystems.dsl.ParsedTokenType;
import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class IdentifierNode extends ASTNode implements TermNode
{
    public Identifier value;

    @Override
    public ASTNode parse(ParseContext context) {
        final ParsedToken token = mergeRegion(context.next(ParsedTokenType.IDENTIFIER));
        this.value = new Identifier(token.value);
        return this;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public String toDebugString() {
        return value.toString() + " " + getTextRegion();
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

    @Override
    public ASTNode reduce(ExpressionContext context) {
        ASTNode value = context.lookup(this.value);
        if ( value instanceof TermNode) {
            return ((TermNode) value).reduce( context );
        }
        return this;
    }

    @Override
    public TermType getType(ExpressionContext context)
    {
        ASTNode reduced = reduce( context );
        if ( reduced instanceof TermNode) {
            return ((TermNode) reduced).getType( context );
        }
        return TermType.UNKNOWN;
    }
}
