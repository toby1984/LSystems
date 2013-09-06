package de.codesourcery.lsystems.dsl.symbols;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * An identifier.
 * 
 * <p>Identifiers come in two flavors , external (user-provided) identifiers and 
 * internal identifiers that support a slightly more relaxed valid character set.</p>
 * 
 * <p>External identifiers must satisfy the regex [_0-9a-zA-Z]+</p>
 * <p>Internal identifiers must satisfy the regex  [\\$\\._0-9a-zA-Z]+</p>
 * 
 * @author tobias.gierke@code-sourcery.de
 */
public final class Identifier 
{
    private static final Pattern VALID_INTERNAL_IDENTIFIER = Pattern.compile("[\\$\\._0-9a-zA-Z]+");
	
    private static final Pattern VALID_IDENTIFIER = Pattern.compile("[_0-9a-zA-Z]+");

    private final String value;

    public Identifier(String value) {
        if ( ! isValidIdentifier( value ) ) {
            throw new IllegalArgumentException("Not a valid identifier: "+value);
        }
        this.value = value;
    }
    
    private Identifier(String value,boolean dummy) { // dummy parameter to distinguish this constructor from the one-arg variant
        this.value = value;
    }
    
    public static Identifier createInternalIdentifier(String s) 
    {
    	if ( StringUtils.isBlank( s ) || ! VALID_INTERNAL_IDENTIFIER.matcher( s ).matches() ) {
    		throw new IllegalArgumentException("Not a valid INTERNAL identifier: '"+s+"'");
    	}
    	return new Identifier(s,false);
    }

    public Identifier append(Identifier other) {
    	return new Identifier( this.value + other.value , true );
    }
    
    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Identifier && this.value.equals( ((Identifier) obj).value );
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public static boolean isValidIdentifier(String s) {
        return s != null && VALID_IDENTIFIER.matcher( s ).matches();
    }
}
