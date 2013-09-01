package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.nodes.AST;
import de.codesourcery.lsystems.dsl.nodes.ExpressionNode;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class Parser {

    public AST parse(String s) {
        DSLLexer lexer = new DSLLexer(new Scanner(s));

        final ParseContext context = new ParseContext()
        {
            @Override
            public boolean eof() {
                return lexer.eof();
            }

            @Override
            public ParsedToken peek() {
                return lexer.peek();
            }

            @Override
            public ParsedToken next() {
                return lexer.next();
            }

            @Override
            public void fail(String msg) throws RuntimeException {
                throw new RuntimeException( msg );
            }

            @Override
            public ParsedToken next(ParsedTokenType type) throws RuntimeException {
                return lexer.next(type);
            }

            @Override
            public boolean peek(ParsedTokenType type) {
                return lexer.peek(type);
            }
        };
        return new AST().parse( context );
    }
}
