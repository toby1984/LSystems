package de.codesourcery.lsystems.dsl.parsing;

import de.codesourcery.lsystems.dsl.symbols.Scope;

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
	
	public void pushScope(Scope scope);
	
	public Scope popScope();
	
	public Scope getCurrentScope();
}
