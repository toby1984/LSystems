package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.ParseContext;
import de.codesourcery.lsystems.dsl.ParsedToken;
import de.codesourcery.lsystems.dsl.ParsedTokenType;

import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 6:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class NumberNode extends ASTNode implements TermNode {

    private static final Pattern VALID_NUMBER = Pattern.compile("[0-9]+");

    public double value;

    public NumberNode() {
    }

    public NumberNode(double value) {
        this.value = value;
    }

    public static boolean isValidNumber(String s) {
        return s != null & VALID_NUMBER.matcher(s).matches();
    }

    @Override
    public ASTNode parse(ParseContext context)
    {
        final ParsedToken tok = context.next(ParsedTokenType.NUMBER);
        String value = tok.value;
        if ( ! context.eof() && context.peek(ParsedTokenType.DOT ) ) {
            context.next();
            value += "."+context.next( ParsedTokenType.NUMBER ).value;
        }
        this.value = Double.parseDouble( value );
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
    public String toDebugString() {
        return Double.toString( value );
    }
}