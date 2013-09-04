package de.codesourcery.lsystems.dsl;

import java.util.ArrayList;
import java.util.List;

import de.codesourcery.lsystems.dsl.nodes.NumberNode;
import de.codesourcery.lsystems.dsl.nodes.OperatorNode;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class DSLLexer {

    private final Scanner scanner;

    private final List<ParsedToken> tokens = new ArrayList<>();

    private final StringBuffer buffer = new StringBuffer();

    public DSLLexer(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean eof() {
        if (tokens.isEmpty()) {
            parseTokens();
        }
        return tokens.isEmpty();
    }

    public ParsedToken peek() {
        if ( eof() ) {
            throw new IllegalStateException("Premature end of input");
        }
        return tokens.get(0);
    }

    public ParsedToken next() {
        if ( eof() ) {
            throw new IllegalStateException("Premature end of input");
        }
        return tokens.remove(0);
    }
    
    private void addToken(ParsedToken tok) {
    	if ( tok == null ) {
			throw new IllegalArgumentException("tok must not be NULL");
		}
    	System.out.println("Parsed: "+tok);
    	this.tokens.add( tok );
    }

    private void parseTokens() {

        if (scanner.eof()) {
            return;
        }

        while (!scanner.eof() && Character.isWhitespace(scanner.peek())) {
            scanner.next();
        }

        if (scanner.eof()) {
            return;
        }

        buffer.setLength(0);
        int offset = scanner.currentOffset();
        char c = scanner.peek();
        while (!scanner.eof()) {
            c = scanner.peek();
            switch (c) {
                case '(':
                    addUnparsed(offset);
                    offset = scanner.currentOffset();
                    addToken(new ParsedToken(ParsedTokenType.PARENS_OPEN, scanner.next(), offset ));
                    return;
                case ')':
                    addUnparsed(offset);
                    offset = scanner.currentOffset();
                    addToken(new ParsedToken(ParsedTokenType.PARENS_CLOSE, scanner.next(), offset ));
                    return;
                case '-':
                    scanner.next();
                    if ( scanner.peek() == '>' ) { // found '->'
                    	scanner.next();
                        addToken(new ParsedToken(ParsedTokenType.ARROW, "->", scanner.currentOffset()-1 ) );
                        return;
                    }
                    scanner.pushBack();
                case '.':
                    addUnparsed(offset);
                    offset = scanner.currentOffset();
                    addToken(new ParsedToken(ParsedTokenType.DOT, scanner.next(), offset ));
                    return;
            }

            if (OperatorNode.isValidOperator(c)) {
                addUnparsed(offset);
                offset = scanner.currentOffset();
                addToken(new ParsedToken(ParsedTokenType.OPERATOR, scanner.next(), offset ));
                return;
            }

            if (Character.isDigit(c)) {
                addUnparsed(offset);
                offset = scanner.currentOffset();
                buffer.append(scanner.next());
                while (!scanner.eof() && NumberNode.isValidNumber(buffer.toString() + scanner.peek())) {
                    buffer.append(scanner.next());
                }
                addToken(new ParsedToken(ParsedTokenType.NUMBER, buffer.toString(), offset));
                return;
            }
            buffer.append(scanner.next());
        }
        addUnparsed(offset);
    }

    private void addUnparsed(int offset)
    {
        if (buffer.length() > 0)
        {
            final String s = buffer.toString();
            if (Identifier.isValidIdentifier(s)) {
                addToken(new ParsedToken(ParsedTokenType.IDENTIFIER, s, offset));
            } else {
                addToken(new ParsedToken(ParsedTokenType.UNPARSED, s, offset));
            }
            buffer.setLength(0);
        }
    }

    public ParsedToken next(ParsedTokenType type) {
        if ( ! peek(type ) ) {
            throw new RuntimeException("Expected "+type+" but got "+peek());
        }
        return next();
    }

    public boolean peek(ParsedTokenType type)
    {
        return peek().type == type;
    }
}