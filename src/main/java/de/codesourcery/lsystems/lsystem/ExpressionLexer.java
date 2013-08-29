package de.codesourcery.lsystems.lsystem;

import java.util.ArrayList;
import java.util.List;

import de.codesourcery.lsystems.lsystem.Token.TokenType;

public class ExpressionLexer 
{
	private final List<Token> tokens = new ArrayList<>();
	
	private final Scanner scanner;
	
	public static TokenSeq parse(String s) {
		ExpressionLexer lexer = new ExpressionLexer(s);
		final List<Token> tok = new ArrayList<>();
		while( ! lexer.eof() ) {
			tok.add( lexer.next() );
		}
		return TokenSeq.create( tok );
	}
	
	protected static final class Scanner {
		
		private final String s;
		private int index;
		private char currentSymbol;
	
		public Scanner(String s) {
			this.s = s;
			currentSymbol = s.charAt(0);
		}
		
		public boolean eof() {
			return index >= s.length();
		}
		
		public char peek() {
			return currentSymbol;
		}
		
		public char next() {
			char c = s.charAt(index++);
			if ( index < s.length() ) {
				currentSymbol = s.charAt(index);
			}
			return c;
		}
		
		public char next(char c) 
		{
			if ( peek() != c ) {
				throw new RuntimeException("Expected '"+c+"' but got '"+peek()+"' at offset "+getOffset());
			}
			return next();
		}		

		public int getOffset() {
			return index;
		}
	}
	
	public ExpressionLexer(String s) 
	{
		this.scanner = new Scanner(s);
	}
	
	public boolean eof() 
	{
		if ( tokens.isEmpty() ) {
			parseTokens();
		}
		return tokens.isEmpty();
	}
	
	private void parseTokens() 
	{
		while ( ! scanner.eof() && Character.isWhitespace( scanner.peek() ) ) 
		{
			scanner.next();
		}
		
		if ( scanner.eof() ) {
			return;
		}		
		
		switch( scanner.next() ) 
		{
			case '[':
				tokens.add( parseToken(TokenType.PUSH_STATE ) );
				break;
			case ']':
				tokens.add( parseToken(TokenType.POP_STATE ) );			
				break;
			case 'g':
				tokens.add( parseToken(TokenType.COLOR_GREEN) );			
				break;
			case 'b':
				tokens.add( parseToken(TokenType.COLOR_BLUE) );			
			case 'r':
				tokens.add( parseToken(TokenType.COLOR_RED ) );
				break;		
			case 'c':
				tokens.add( parseToken(TokenType.DRAW_CIRCLE ) );
				break;	
			case 'f':
				tokens.add( parseToken(TokenType.FORWARD_NODRAW ) );
				break;
			case 'F':
				tokens.add( parseToken(TokenType.FORWARD) );			
				break;
			case '+':
				tokens.add( parseToken(TokenType.ROTATE_LEFT) );
				break;
			case '-':
				tokens.add( parseToken(TokenType.ROTATE_RIGHT) );
				break;		
			default:
				throw new RuntimeException("Parse error, invalid character '"+scanner.peek()+"' at index "+ ( scanner.getOffset()-1 ));
		}
	}

	private Token parseToken(TokenType tokenType) 
	{
		if ( tokenType.supportsParameters() && scanner.peek() == '(' ) 
		{
			final int argListOffset = scanner.getOffset();
			scanner.next('(');
			
			final List<String> params = new ArrayList<>();
			final StringBuilder currentParam = new StringBuilder();
			while ( ! scanner.eof() && scanner.peek() != ')' ) 
			{
				if ( Character.isWhitespace( scanner.peek() ) ) {
					scanner.next();
					continue;
				}
				if ( scanner.peek() == ',' && ! currentParam.toString().isEmpty() ) 
				{
					scanner.next(',');
					params.add( currentParam.toString() );
					currentParam.setLength(0);
					continue;
				}
				currentParam.append( scanner.next() );
			}
			
			if ( scanner.eof() ) {
				throw new RuntimeException("Missing ')' at index "+scanner.getOffset());
			}
			if ( ! currentParam.toString().isEmpty() ) {
				params.add( currentParam.toString() );
			}
			scanner.next(')');
			
			if ( params.size() < tokenType.getMinParameterCount() || params.size() > tokenType.getMaxParameterCount() ) {
				throw new RuntimeException("Argument at index "+argListOffset+" has wrong parameter count "+params.size()+" , needs to be ["+
						tokenType.getMinParameterCount()+","+tokenType.getMaxParameterCount()+"]");
			}
			return new Token(tokenType, params );
		}
		return new Token(tokenType);
	}
	
	public Token next() 
	{
		if ( eof() ) {
			throw new IllegalStateException("Already at EOF");
		}
		return tokens.remove(0);
	}
	
	public Token peek() 
	{
		if ( eof() ) {
			throw new IllegalStateException("Already at EOF");
		}
		return tokens.get(0);
	}
}
