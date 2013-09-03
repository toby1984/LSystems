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
        this.type = type;
        this.value = value;
        this.offset = offset;
        this.region = new TextRegion( offset , value.length() );
    }

    @Override
    public String toString() {
        return value+" ["+type+"]";
    }
}
