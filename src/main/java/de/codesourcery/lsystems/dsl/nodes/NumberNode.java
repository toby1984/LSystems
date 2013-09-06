package de.codesourcery.lsystems.dsl.nodes;

import java.util.regex.Pattern;

import de.codesourcery.lsystems.dsl.parsing.ParseContext;
import de.codesourcery.lsystems.dsl.parsing.ParsedToken;
import de.codesourcery.lsystems.dsl.parsing.ParsedTokenType;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class NumberNode extends AbstractASTNode implements TermNode {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumberNode that = (NumberNode) o;

        if (hadDecimalPoint != that.hadDecimalPoint) return false;
        if (Double.compare(that.value, value) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (hadDecimalPoint ? 1 : 0);
        return result;
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
    public TermNode evaluate(ExpressionContext context) {
        return this;
    }

    @Override
    public TermNode reduce(ExpressionContext context) {
        return this;
    }

    @Override
    public TermType getType(ExpressionContext context) {
        return hadDecimalPoint ? TermType.FLOATING_POINT : TermType.INTEGER;
    }

    @Override
    public String toDebugString()
    {
        final String region = getTextRegion() == null ? "" : " "+getTextRegion()+" ";
        if ( !hadDecimalPoint) {
            return Double.toString(value).replace(".0","")+region+"/ "+getType( null );
        }
        return Double.toString(value)+region+"/ "+getType(null);
    }
    
	@Override
	protected NumberNode cloneThisNodeOnly() {
		final NumberNode result = new NumberNode();
		result.value = this.value;
		result.hadDecimalPoint = this.hadDecimalPoint;
		return result;
	}

    @Override
    public boolean isLiteralValue() {
        return true;
    }
}