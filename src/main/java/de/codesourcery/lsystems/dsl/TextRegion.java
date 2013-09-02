package de.codesourcery.lsystems.dsl;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/2/13
 * Time: 11:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextRegion {

    public final int start;
    public final int length;

    public TextRegion(int start, int length) {
        if ( start < 0 ) {
            throw new IllegalArgumentException("start < 0 ?");
        }
        if ( length < 0 ) {
            throw new IllegalArgumentException("length < 0");
        }
        this.start = start;
        this.length = length;
    }

    public int start() {
        return start;
    }

    public int length() {
        return length;
    }

    public int end() {
        return start + length;
    }

    public TextRegion merge(TextRegion other) {
        int newStart = Math.min( this.start , other.start );
        int newEnd = Math.max( this.end(), other.end() );
        return new TextRegion( newStart , newEnd - newStart );
    }
}
