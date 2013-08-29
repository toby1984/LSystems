package de.codesourcery.lsystems.lsystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class TokenSeq {

	public static TokenSeq create(Token tok1,Token... tokens) 
	{
		return new MultiTokenSequence( tok1, tokens == null ? Collections.<Token>emptyList() : Arrays.asList(tokens) );
	}
	
	public static TokenSeq create(Token tok1) {
		return new SingleTokenSequence(tok1);
	}	
	
	public static TokenSeq create(List<Token> tok) {
		return new MultiTokenSequence(tok);
	}		
	
	protected static final class SingleTokenSequence extends TokenSeq {
		
		private final Token token;
		
		public SingleTokenSequence(Token tok1) {
			this.token = tok1;
		}
		
		@Override
		public boolean isEmpty() {
			return token == null;
		}
		
		@Override
		public String getAsString(ParameterProvider provider,boolean resolvePlaceholders) {
			return token == null ? "" : token.getAsString(provider,resolvePlaceholders);
		}		
		
		@Override
		public List<Token> toList() {
			final List<Token> result = new ArrayList<>();
			result.add(token);
			return result;
		}		
	}	
	
	protected static final class MultiTokenSequence extends TokenSeq {
	
		private final List<Token> tokens;
		
		public MultiTokenSequence(List<Token> tokens) {
			this.tokens = new ArrayList<>(tokens);
		}
		
		public MultiTokenSequence(Token tok1,List<Token> tokens) {
			this.tokens = new ArrayList<>();
			this.tokens.add(tok1);
			this.tokens.addAll(tokens);
		}
		
		@Override
		public boolean isEmpty() {
			return tokens.isEmpty();
		}
		
		@Override
		public String getAsString(ParameterProvider provider,boolean resolvePlaceholders) {
			final StringBuilder builder = new StringBuilder();
			for ( Token tok : tokens ) {
				builder.append( tok.getAsString(provider,resolvePlaceholders) );
			}
			return builder.toString();
		}		
		
		@Override
		public List<Token> toList() {
			return tokens;
		}
	}
	
	public abstract boolean isEmpty();	
	
	public abstract String getAsString(ParameterProvider provider,boolean resolvePlaceholders);

	public abstract List<Token> toList();
}
