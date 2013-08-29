package de.codesourcery.lsystems.lsystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.codesourcery.lsystems.Main;
import de.codesourcery.lsystems.lsystem.Token.TokenType;

public class ExpressionLexer implements TokenStream
{
	private static boolean DEBUG=false;
	
	private final List<Token> tokens = new ArrayList<>();
	
	private final Scanner scanner;
	private final StringBuilder buffer = new StringBuilder();

	private String input;
	
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
			if ( eof() ) {
				throw new IllegalStateException("Already at EOF");
			}
			return currentSymbol;
		}
		
		public char next() {
			char c = currentSymbol;
			index++;
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
		if ( DEBUG) {
			System.out.println("PARSING >"+s+"<");
		}
		this.input=s;
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
		
		// careful , the cases in this switch need to be in line with the switch in parseToken()
		if ( DEBUG ) {
			System.out.println("Peek(): "+scanner.peek());
		}
		switch( scanner.peek() ) 
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
				break;
			case 'r':
				tokens.add( parseToken(TokenType.COLOR_RED ) );
				break;		
			case 'c':
				tokens.add( parseToken(TokenType.DRAW_CIRCLE ) );
				break;	
			case 'C':
				tokens.add( parseToken(TokenType.DRAW_FILLED_CIRCLE ) );
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
				tokens.add( parseToken(TokenType.CHARACTERS) );
				break;
		}
	}
	
	private boolean isKnownSymbol(char c) 
	{
		// careful , the cases in this switch need to be in line with the switch in parseTokens()
		switch(c) 
		{
			case '[':
			case ']':
			case 'g':
			case 'b':
			case 'r':
			case 'c':
			case 'C':
			case 'f':
			case 'F':
			case '+':
			case '-':
				return true;
			default:
				return false;
		}
	}

	private Token parseToken(TokenType tokenType) 
	{
		final String literal;
		if ( TokenType.CHARACTERS.equals(tokenType) ) 
		{
			buffer.setLength( 0 );			
			while ( ! scanner.eof() && ! isKnownSymbol( scanner.peek() ) ) {
				buffer.append( scanner.next() );
			}
			literal = buffer.toString();
		} else {
			literal = Character.toString( scanner.next() );
		}
		
		List<String> params = null;
		if ( !scanner.eof() && tokenType.supportsParameters() && scanner.peek() == '(' ) 
		{
			final int argListOffset = scanner.getOffset();
			scanner.next('(');
			
			params = new ArrayList<>();
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
		}
		Token result = new Token(tokenType,literal , params );
		if ( DEBUG ) { 
			System.out.println(">> "+result+" (type:"+tokenType+")");
		}
		return result;
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

	@Override
	public Iterator<Token> iterator() 
	{
		return new Iterator<Token>() {

			@Override
			public boolean hasNext() {
				return ! eof();
			}

			@Override
			public Token next() {
				return next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("remove()");
			}
		};
	}
}