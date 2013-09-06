package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.parsing.ParseContext;
import de.codesourcery.lsystems.dsl.parsing.ParsedToken;
import de.codesourcery.lsystems.dsl.parsing.ParsedTokenType;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class StringNode extends AbstractASTNode implements TermNode
{
    public String value;

    public StringNode() {
    }

    public StringNode(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be NULL");
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringNode that = (StringNode) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public ASTNode parse(ParseContext context)
    {
        mergeRegion( context.next(ParsedTokenType.QUOTE) );

        final boolean oldState = context.isSkipWhitespace();
        try
        {
            context.setSkipWhitespace( false );
            value = "";
            do {
                ParsedToken tok = context.peek();
                if ( tok.hasType(ParsedTokenType.EOL)) {
                    context.next(); // do not add EOL to strings
                    continue;
                }
                if ( tok.hasType( ParsedTokenType.QUOTE ) ) {
                    break;
                }
                value += mergeRegion(context.next()).value;
            } while ( true );
        } finally {
            context.setSkipWhitespace( oldState );
        }

        mergeRegion( context.next(ParsedTokenType.QUOTE) );
        return this;
    }

    @Override
    public String toDebugString()
    {
        if ( value == null ) {
            return "<null string>";
        }
        return '"'+value+'"'+( getTextRegion() == null ? "" : " " + getTextRegion().toString() );
    }

    @Override
    protected StringNode cloneThisNodeOnly()
    {
        final StringNode result = new StringNode();
        result.value = this.value;
        return result;
    }

    @Override
    public TermNode evaluate(ExpressionContext context) {
        return this;
    }

    @Override
    public TermNode reduce(ExpressionContext context) {
        return this;
    }

    @Override
    public TermType getType(ExpressionContext context)
    {
        return TermType.STRING_LITERAL;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isLiteralValue() {
        return true;
    }
}
