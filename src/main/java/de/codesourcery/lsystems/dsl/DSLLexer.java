package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.nodes.AST;
import de.codesourcery.lsystems.dsl.nodes.NumberNode;
import de.codesourcery.lsystems.dsl.nodes.OperatorNode;
import de.codesourcery.lsystems.dsl.nodes.TermNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
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
            switch (c) {
                case '(':
                    addUnparsed(offset);
                    tokens.add(new ParsedToken(ParsedTokenType.PARENS_OPEN, scanner.next(), scanner.currentOffset()));
                    return;
                case ')':
                    addUnparsed(offset);
                    tokens.add(new ParsedToken(ParsedTokenType.PARENS_CLOSE, scanner.next(), scanner.currentOffset()));
                    return;
                case '.':
                    addUnparsed(offset);
                    tokens.add(new ParsedToken(ParsedTokenType.DOT, scanner.next(), scanner.currentOffset()));
                    return;
            }

            if (OperatorNode.isValidOperator(c)) {
                addUnparsed(offset);
                tokens.add(new ParsedToken(ParsedTokenType.OPERATOR, scanner.next(), scanner.currentOffset()));
                return;
            }

            if (Character.isDigit(c)) {
                addUnparsed(offset);
                offset = scanner.currentOffset();
                buffer.append(scanner.next());
                while (!scanner.eof() && NumberNode.isValidNumber(buffer.toString() + scanner.peek())) {
                    buffer.append(scanner.next());
                }
                tokens.add(new ParsedToken(ParsedTokenType.NUMBER, buffer.toString(), offset));
                return;
            }
            buffer.append(scanner.next());
        }
        addUnparsed(offset);
    }

    private void addUnparsed(int offset) {
        if (buffer.length() > 0) {
            final String s = buffer.toString();
            if (Identifier.isValidIdentifier(s)) {
                tokens.add(new ParsedToken(ParsedTokenType.IDENTIFIER, s, offset));
            } else {
                tokens.add(new ParsedToken(ParsedTokenType.UNPARSED, s, offset));
            }
            buffer.setLength(0);
        }
    }

    public static void main(String[] args) {
        AST ast = new Parser().parse("((1+2)*4)/3");
        System.out.println( "PARSED: "+ast.toString() );
        System.out.println("EVALUATED: " + ((TermNode) ast.child(0)).evaluate());
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