package de.codesourcery.lsystems.dsl.nodes;

import java.util.regex.Pattern;

import de.codesourcery.lsystems.dsl.ParseContext;
import de.codesourcery.lsystems.dsl.ParsedToken;
import de.codesourcery.lsystems.dsl.ParsedTokenType;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class NumberNode extends ASTNode implements TermNode {

    private static final Pattern VALID_NUMBER = Pattern.compile("[0-9]+");

    public double value;
    public boolean hadDecimalPoint;

    public NumberNode() {
    }

    public NumberNode(double value,TermType type) {
        this.value = value;
        if ( type == TermType.INTEGER ) {
            this.hadDecimalPoint = false;
        } else if ( type == TermType.FLOATING_POINT ) {
            this.hadDecimalPoint = true;
        } else {
            throw new IllegalArgumentException("Unhandled numeric type: "+type);
        }
    }

    public static boolean isValidNumber(String s) {
        return s != null & VALID_NUMBER.matcher(s).matches();
    }

    @Override
    public ASTNode parse(ParseContext context)
    {
        final ParsedToken tok = mergeRegion(context.next(ParsedTokenType.NUMBER));

        String value = tok.value;
        if (!context.eof() && context.peek(ParsedTokenType.DOT)) {
            mergeRegion(context.next());
            value += "." + mergeRegion(context.next(ParsedTokenType.NUMBER) ).value;
            this.hadDecimalPoint = true;
        }
        this.value = Double.parseDouble(value);
        return this;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    @Override
    public double evaluate(ExpressionContext context) {
        return value;
    }

    @Override
    public ASTNode reduce(ExpressionContext context) {
        return this;
    }

    @Override
    public TermType getType(ExpressionContext context) {
        return hadDecimalPoint ? TermType.FLOATING_POINT : TermType.INTEGER;
    }

    @Override
    public String toDebugString()
    {
        if ( !hadDecimalPoint) {
            return Double.toString(value).replace(".0","")+" "+getTextRegion()+" / "+getType( null );
        }
        return Double.toString(value)+" "+getTextRegion()+" / "+getType(null);
    }
}