package de.codesourcery.lsystems.dsl.parsing;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
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

    public void pushBack() {
        if ( currentIndex == 0 ) {
            throw new IllegalStateException("Already at the beginning");
        }
        currentIndex--;
        currentSymbol = input.charAt(currentIndex);
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