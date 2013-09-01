package de.codesourcery.lsystems.lsystem;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class Token 
{
	public final TokenType type;
	public final String value;
	public final List<String> params;
	
	public Token(TokenType type,String value) {
		this.type = type;
		this.params = null;
		this.value = value;
	}
	
	public Token(TokenType type,String value , List<String> params) {
		this.type = type;
		this.params = params;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return getAsString(null, false );
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
            if ( value == null ) {
                throw new RuntimeException("Internal error, got no value to substitute placeholder ${"+paramName+"}");
            }
			result = result.replaceAll( "\\$\\{"+paramName+"\\}" , value );
			m = PARAMETER_EXPR.matcher( result );
		} 
		return result;
	}
	
	public String getAsString(ParameterProvider provider,boolean resolvePlaceholders) 
	{
		if ( ! hasParameters() ) {
			return value;
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
		return value+"("+paramString+")";
	}
	
	public static enum TokenType 
	{
		PUSH_STATE,
		POP_STATE,
		COLOR_GREEN,
		COLOR_BLUE,
		COLOR_RED,
		DRAW_CIRCLE,
		DRAW_FILLED_CIRCLE,		
		FORWARD(0,1),
		FORWARD_NODRAW(0,1),
		ROTATE_LEFT(0,1),
		ROTATE_RIGHT(0,1),
		CHARACTERS;
		
		private final int maxParameterCount;
		private final int minParameterCount;		
		
		private TokenType() {
			this.minParameterCount = this.maxParameterCount=0;
		}
		
		private TokenType(int minParameterCount,int maxParameterCount) {
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
		
		protected final int parseInt(String s) {
			return Integer.valueOf( s );
		}
		
		protected final float parseFloat(String s) {
			return Float.valueOf( s );
		}		
	}	
}
