package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;
import de.codesourcery.lsystems.dsl.execution.MyObject;
import de.codesourcery.lsystems.dsl.parsing.ParseContext;
import de.codesourcery.lsystems.dsl.parsing.ParsedToken;
import de.codesourcery.lsystems.dsl.parsing.ParsedTokenType;
import de.codesourcery.lsystems.dsl.symbols.Identifier;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class IdentifierNode extends AbstractASTNode implements TermNode
{
    public Identifier value;

    @Override
    public ASTNode parse(ParseContext context) {
        final ParsedToken token = mergeRegion(context.next(ParsedTokenType.IDENTIFIER));
        this.value = new Identifier(token.value);
        
        context.getCurrentScope().declareVariable( value  , this );
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
    public TermNode evaluate(ExpressionContext context) throws UnknownIdentifierException
    {
        ASTNode value = context.lookup(this.value, getDefinitionScope() , false);
        if ( value instanceof TermNode) {
            return ((TermNode) value).evaluate( context );
        }
        throw new RuntimeException("Don't know how to evaluate "+value);
    }

    @Override
    public TermNode reduce(ExpressionContext context)
    {
        return this;
    }

    @Override
    public TermType getType(ExpressionContext context)
    {
        TermNode reduced = reduce( context );
        if ( reduced == this ) {
            return TermType.UNKNOWN;
        }
        return reduced.getType( context );
    }
    
	@Override
	protected IdentifierNode cloneThisNodeOnly() {
		final IdentifierNode result = new IdentifierNode();
		result.value = this.value;
		return result;
	}

    @Override
    public boolean isLiteralValue() {
        return true;
    }
}
