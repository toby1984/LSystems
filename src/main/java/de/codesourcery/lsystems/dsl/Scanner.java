package de.codesourcery.lsystems.dsl;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Scanner {

    private final String input;
    private int currentIndex=0;

    private char currentSymbol;

    public Scanner(String input) {
        this.input = input;
        if ( input.length() > 0 ) {
            currentSymbol = input.charAt(0);
        }
    }

    public int currentOffset() {
        return currentIndex;
    }

    public boolean eof() {
        return currentIndex >= input.length();
    }

    public char peek() {
        if ( eof() ) {
            throw new IllegalStateException("At EOF");
        }
        return currentSymbol;
    }

    public char next() {
        if ( eof() ) {
            throw new IllegalStateException("At EOF");
        }
        char result = currentSymbol;
        currentIndex++;
        if ( currentIndex < input.length() ) {
            currentSymbol = input.charAt(currentIndex);
        }
        return result;
    }
}