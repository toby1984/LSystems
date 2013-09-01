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

    public float value;

    public static boolean isValidNumber(String s) {
        return s != null & VALID_NUMBER.matcher(s).matches();
    }

    @Override
    public ASTNode parse(ParseContext context)
    {
        final ParsedToken tok = context.next(ParsedTokenType.NUMBER);
        String value = tok.value;
        if ( ! context.eof() && context.peek(ParsedTokenType.DOT ) ) {
            value += "."+context.next( ParsedTokenType.NUMBER );
        }
        this.value = Float.parseFloat( value );
        return this;
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }

    @Override
    public double evaluate() {
        return value;
    }
}