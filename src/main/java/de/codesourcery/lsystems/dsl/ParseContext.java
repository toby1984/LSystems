package de.codesourcery.lsystems.dsl;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public interface ParseContext {

    boolean eof();

    ParsedToken peek();

    ParsedToken next();

    void fail(String msg) throws RuntimeException;

    ParsedToken next(ParsedTokenType number) throws RuntimeException;

    boolean peek(ParsedTokenType type);

	boolean isSkipWhitespace();

	void setSkipWhitespace(boolean b);
}
