package de.codesourcery.lsystems.dsl;

import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 6:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Identifier {

    private static final Pattern VALID_IDENTIFIER = Pattern.compile("[_0-9a-zA-Z]+");

    private final String value;

    public Identifier(String value) {
        if ( ! isValidIdentifier( value ) ) {
            throw new IllegalArgumentException("Not a valid identifier: "+value);
        }
        this.value = value;
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
