package de.codesourcery.lsystems.lsystem;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class Token 
{
	public final TokenType type;
	public final List<String> params;
	
	public Token(TokenType type) {
		this.type = type;
		this.params = null;
	}
	
	public Token(TokenType type,List<String> params) {
		this.type = type;
		this.params = params;
	}
	
	public boolean hasParameters() {
		return params != null && params.size() > 0;
	}
	
	public int getParameterCount() {
			return params != null ? params.size() : 0;
	}
	
	public List<String> getParameters() {
		return params;
	}
	
	public int intParameter(int index,ParameterProvider provider) 
	{
		return Integer.parseInt( substituteParameters( params.get(index) , provider ) );
	}	
	
	public float floatParameter(int index,ParameterProvider provider) {
		return Float.parseFloat( substituteParameters( params.get(index) , provider ) );		
	}	
	
	public String parameter(int index,ParameterProvider provider) {
		return substituteParameters( params.get(index) , provider );		
	}	
	
	private final Pattern PARAMETER_EXPR = Pattern.compile( "\\$\\{(.*?)\\}");
	
	private String substituteParameters(String expression,ParameterProvider provider) 
	{
		String result = expression;
		Matcher m = PARAMETER_EXPR.matcher( result );
		while ( m.find() ) 
		{
			final String paramName = m.group(1);
			final String value = provider.getParameter( this , paramName );
			result = result.replaceAll( "\\$\\{"+paramName+"\\}" , value );
			m = PARAMETER_EXPR.matcher( result );
		} 
		return result;
	}
	
	public String getAsString(ParameterProvider provider,boolean resolvePlaceholders) 
	{
		if ( ! hasParameters() ) {
			return type.getIdentifier();
		}
		final StringBuilder paramString = new StringBuilder();
		
		if ( resolvePlaceholders ) 
		{
			for (Iterator<String> it = params.iterator(); it.hasNext();) {
				paramString.append( substituteParameters( it.next() , provider ) );
				if ( it.hasNext() ) {
					paramString.append(",");
				}
			}
		} else {
			for (Iterator<String> it = params.iterator(); it.hasNext();) {
				paramString.append( it.next() );
				if ( it.hasNext() ) {
					paramString.append(",");
				}
			}
		}
		return type.getIdentifier()+"("+paramString+")";
	}
	
	public static enum TokenType 
	{
		PUSH_STATE("["),
		POP_STATE("]"),
		COLOR_GREEN("g"),
		COLOR_BLUE("b"),
		COLOR_RED("r"),
		DRAW_CIRCLE("c"),
		FORWARD("F"),
		FORWARD_NODRAW("f"),
		ROTATE_LEFT("+",0,1),
		ROTATE_RIGHT("-",0,1);
		
		private final int maxParameterCount;
		private final int minParameterCount;		
		private final String s;
		
		private TokenType(String s) {
			this.s = s;
			this.minParameterCount = this.maxParameterCount=0;
		}
		
		private TokenType(String s,int minParameterCount,int maxParameterCount) {
			this.s = s;
			if ( minParameterCount > maxParameterCount ) {
				throw new IllegalArgumentException();
			}
			this.maxParameterCount = maxParameterCount;
			this.minParameterCount = minParameterCount;
		}		
		
		public final boolean supportsParameters() {
			return minParameterCount>0 || maxParameterCount > 0 ;
		}
		
		public int getMinParameterCount() {
			return minParameterCount;
		}
		
		public int getMaxParameterCount() {
			return maxParameterCount;
		}
		
		public final String getIdentifier() {
			return s;
		}
		
		protected final int parseInt(String s) {
			return Integer.valueOf( s );
		}
		
		protected final float parseFloat(String s) {
			return Float.valueOf( s );
		}		
	}	
}
