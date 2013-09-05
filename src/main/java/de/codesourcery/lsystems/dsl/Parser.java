package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.nodes.AST;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class Parser {

    public AST parse(String s)
    {
        final DSLLexer lexer = new DSLLexer(new Scanner(s));

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

			@Override
			public boolean isSkipWhitespace() {
				return lexer.isSkipWhitespace();
			}

			@Override
			public void setSkipWhitespace(boolean b) {
				lexer.setSkipWhitespace( b );
			}
        };
        return new AST().parse( context );
    }
}
