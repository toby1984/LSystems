package de.codesourcery.lsystems.dsl.parsing;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public final class TextRegion {

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

    public TextRegion(TextRegion region) {
    	this( region.start() , region.length() );
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextRegion that = (TextRegion) o;

        if (length != that.length) return false;
        if (start != that.start) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + length;
        return result;
    }

    public boolean contains(int pos) {
        return start <= pos && pos < end();
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
        int newEnd = Math.max(this.end(), other.end());
        return new TextRegion( newStart , newEnd - newStart );
    }

    @Override
    public String toString() {
        return "[" + start + "-" + (end()-1) + "]";
    }
}
