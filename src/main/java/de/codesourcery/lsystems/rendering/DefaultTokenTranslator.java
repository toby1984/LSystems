package de.codesourcery.lsystems.rendering;

import java.util.HashMap;
import java.util.Map;

import de.codesourcery.lsystems.Main;
import de.codesourcery.lsystems.lsystem.Token;
import de.codesourcery.lsystems.lsystem.Token.TokenType;
import de.codesourcery.lsystems.lsystem.TokenStream;

public class DefaultTokenTranslator implements TokenTranslator {

	protected static final Map<Token.TokenType,PrimitiveType> defaultMapping = new HashMap<>();
	
	protected final Map<String,PrimitiveType> customMapping = new HashMap<>();
	
	public DefaultTokenTranslator() 
	{
		for ( Token.TokenType type : Token.TokenType.values() ) 
		{
			final PrimitiveType p;
			switch(type) {
				case COLOR_BLUE:
					p = PrimitiveType.COLOR_BLUE;
					break;							
				case COLOR_GREEN:
					p = PrimitiveType.COLOR_GREEN;
					break;							
				case COLOR_RED:
					p = PrimitiveType.COLOR_RED;
					break;		
				case DRAW_CIRCLE:
					p = PrimitiveType.DRAW_CIRCLE;
					break;							
				case DRAW_FILLED_CIRCLE:
					p = PrimitiveType.DRAW_FILLED_CIRCLE;
					break;							
				case FORWARD:
					p = PrimitiveType.FORWARD;
					break;		
				case FORWARD_NODRAW:
					p = PrimitiveType.FORWARD_NODRAW;
					break;						
				case POP_STATE:
					p = PrimitiveType.POP_STATE;
					break;						
				case PUSH_STATE:
					p = PrimitiveType.PUSH_STATE;
					break;					
				case ROTATE_LEFT:
					p = PrimitiveType.ROTATE_LEFT;
					break;					
				case ROTATE_RIGHT:
					p = PrimitiveType.ROTATE_RIGHT;
					break;
				default:
					// any unknown tokens are mapped to NO_OPs
					p = PrimitiveType.NOP;
			}
			defaultMapping.put( type , p );
		}
	}
	
	public void mapSymbols( String value , PrimitiveType type) {
		customMapping.put(value,type);
	}
	
	@Override
	public Primitive read(TokenStream stream) 
	{
		final Token tok = stream.next();
		
		if ( TokenType.CHARACTERS.equals( tok.type ) ) {
			PrimitiveType resultType = customMapping.get( tok.value );
			if ( resultType != null ) {
				Primitive result = new Primitive(resultType,tok);
				if ( Main.DEBUG) {
					System.out.println("Mapped "+tok+" => "+resultType);
				}
				return result;
			}
		}
		Primitive result = new Primitive( defaultMapping.get( tok.type ) , tok );
		if ( Main.DEBUG) {
			System.out.println("Mapped "+tok.type+" ("+tok+") => "+result.type);
		}
		return result;
	}
}