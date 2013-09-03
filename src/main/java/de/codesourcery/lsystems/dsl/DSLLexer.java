package de.codesourcery.lsystems.dsl;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;
import de.codesourcery.lsystems.dsl.nodes.*;

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
                    tokens.add(new ParsedToken(ParsedTokenType.PARENS_OPEN, scanner.next(), scanner.currentOffset()));
                    return;
                case ')':
                    addUnparsed(offset);
                    tokens.add(new ParsedToken(ParsedTokenType.PARENS_CLOSE, scanner.next(), scanner.currentOffset()));
                    return;
                case '-':
                    scanner.next();
                    if ( scanner.peek() == '>' ) { // found '->'
                        tokens.add(new ParsedToken(ParsedTokenType.ARROW, "->", scanner.currentOffset()-1 ) );
                        return;
                    }
                    scanner.pushBack();
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

    private void addUnparsed(int offset)
    {
        if (buffer.length() > 0)
        {
            final String s = buffer.toString();
            if (Identifier.isValidIdentifier(s)) {
                tokens.add(new ParsedToken(ParsedTokenType.IDENTIFIER, s, offset));
            } else {
                tokens.add(new ParsedToken(ParsedTokenType.UNPARSED, s, offset));
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