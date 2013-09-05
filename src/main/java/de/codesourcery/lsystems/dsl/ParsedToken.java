package de.codesourcery.lsystems.dsl;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class ParsedToken {

    public final ParsedTokenType type;
    public final String value;
    public final int offset;
    public final TextRegion region;

    public ParsedToken(ParsedTokenType type, char value, int offset) {
        this(type,Character.toString(value),offset);
    }

    public ParsedToken(ParsedTokenType type, String value, int offset) {
    	if ( type == null ) {
			throw new IllegalArgumentException("type must not be NULL");
		}
    	if ( value == null ) {
			throw new IllegalArgumentException("value must not be NULL");
		}
        this.type = type;
        this.value = value;
        this.offset = offset;
        this.region = new TextRegion( offset , value.length() );
    }
    
    public boolean isWhitespace() {
    	return hasType( ParsedTokenType.WHITESPACE ) || hasType( ParsedTokenType.EOL );
    }
    
    public boolean hasType(ParsedTokenType t) {
    	return t.equals( type );
    }    

    @Override
    public String toString() {
        return value+" ["+type+" , "+region+" ]";
    }
}
