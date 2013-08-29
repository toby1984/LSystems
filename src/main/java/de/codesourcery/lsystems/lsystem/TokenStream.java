package de.codesourcery.lsystems.lsystem;

public interface TokenStream extends Iterable<Token> {

	public boolean eof();
	
	public Token next();
	
	public Token peek();
}
