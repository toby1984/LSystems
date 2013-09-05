package de.codesourcery.lsystems.dsl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.codesourcery.lsystems.dsl.nodes.NumberNode;
import de.codesourcery.lsystems.dsl.nodes.OperatorNode;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class DSLLexer {

    private final Scanner scanner;

    private final List<ParsedToken> tokens = new ArrayList<>();

    private final StringBuffer buffer = new StringBuffer();
    
    private boolean skipWhitespace = true;

    public DSLLexer(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean eof() {
        if (tokens.isEmpty()) {
            parseTokens();
        }
        return tokens.isEmpty();
    }

    public ParsedToken peek() {
        if ( eof() ) {
            throw new IllegalStateException("Premature end of input");
        }
        return tokens.get(0);
    }

    public ParsedToken next() {
        if ( eof() ) {
            throw new IllegalStateException("Premature end of input");
        }
        return tokens.remove(0);
    }
    
    private void addToken(ParsedToken tok) {
    	if ( tok == null ) {
			throw new IllegalArgumentException("tok must not be NULL");
		}
    	System.out.println("Parsed: "+tok);
    	this.tokens.add( tok );
    }
    
    public boolean isSkipWhitespace() {
    	return this.skipWhitespace;
    }
    
    public void setSkipWhitespace(boolean newState) 
    {
    	final boolean oldState = this.skipWhitespace;
		this.skipWhitespace = newState;
		
		// change: do_not_ignore_whitespace => ignore whitespace
		if ( newState == true && oldState == false )  
		{ 
			// remove any whitespace tokens that were already parsed from queue
			for (Iterator<ParsedToken> it = tokens.iterator(); it.hasNext();) 
			{
				if ( it.next().isWhitespace() ) {
					it.remove();
				}
			}
		}
	}
    
    /**
     * 
     * @return true if parsing should continue, <code>false</code> if this method
     * added one or more whitespace tokens to the queue and thus parsing does not need to continue
     */
    private boolean parseAndSkipWhitespace() {
    	
    	if ( isSkipWhitespace() ) 
    	{
            while (!scanner.eof() && isWhitespace(scanner.peek())) {
                scanner.next();
            }
            return true;
    	}
    	
    	if ( ! scanner.eof() && isWhitespace( scanner.peek() ) ) 
    	{
    		final int offset = scanner.peek();
			switch( scanner.peek() ) 
			{
				case '\r':
					scanner.next();
					if ( ! scanner.eof() && scanner.peek() == '\n') 
					{
						scanner.next();
   						addToken( new ParsedToken(ParsedTokenType.EOL , "\r\n" , offset ) );    							
					} 
					else 
					{
						addToken( new ParsedToken(ParsedTokenType.WHITESPACE, "\r" , offset ) );
					}    					
					return false;
				case '\n':
					scanner.next();
					addToken( new ParsedToken(ParsedTokenType.EOL , "\n" , offset ) );
					return false;
				default:
					// fall-through, neither a CR nor LF
			}
			
			buffer.setLength( 0 );
			do 
			{
				buffer.append( scanner.next() );
			} while ( ! scanner.eof() && isWhitespace( scanner.peek() ) && ( scanner.peek() != '\r' && scanner.peek() != '\n' ) );
			addToken( new ParsedToken(ParsedTokenType.WHITESPACE, buffer.toString() , offset ) );
    		return false;
    	}
    	return true;
    }
    
    private boolean isWhitespace(char c) {
    	return Character.isWhitespace( c );
    }
    
    private void parseTokens() {

        if (scanner.eof()) {
            return;
        }
        
        // buffer is used by parseAndSkipWhitespace() as well,
        // make sure it's reset
        buffer.setLength(0);        

        if ( ! parseAndSkipWhitespace() ) {
        	return;
        }
        
        if (scanner.eof()) {
            return;
        }

        int offset = scanner.currentOffset();
        char c = scanner.peek();
        while (!scanner.eof()) {
            c = scanner.peek();
            
            if ( isWhitespace( c ) ) {
            	break;
            }
            switch (c) 
            {
                case '\'':
                case '\"':
                    addUnparsed(offset);
                    offset = scanner.currentOffset();
                    addToken(new ParsedToken(ParsedTokenType.QUOTE, scanner.next(), offset ));
                    return;
            	case ':':
	                addUnparsed(offset);
	                offset = scanner.currentOffset();
	                addToken(new ParsedToken(ParsedTokenType.COLON, scanner.next(), offset ));
	                return;                		
            	case '=':
	                addUnparsed(offset);
	                offset = scanner.currentOffset();
	                addToken(new ParsedToken(ParsedTokenType.ASSIGNMENT, scanner.next(), offset ));
	                return;            
                case '(':
                    addUnparsed(offset);
                    offset = scanner.currentOffset();
                    addToken(new ParsedToken(ParsedTokenType.PARENS_OPEN, scanner.next(), offset ));
                    return;
                case ')':
                    addUnparsed(offset);
                    offset = scanner.currentOffset();
                    addToken(new ParsedToken(ParsedTokenType.PARENS_CLOSE, scanner.next(), offset ));
                    return;
                case '-':
                    scanner.next();
                    if ( scanner.peek() == '>' ) { // found '->'
                    	scanner.next();
                        addToken(new ParsedToken(ParsedTokenType.ARROW, "->", scanner.currentOffset()-1 ) );
                        return;
                    }
                    scanner.pushBack();
                case '.':
                    addUnparsed(offset);
                    offset = scanner.currentOffset();
                    addToken(new ParsedToken(ParsedTokenType.DOT, scanner.next(), offset ));
                    return;
            }

            if (OperatorNode.isValidOperator(c)) {
                addUnparsed(offset);
                offset = scanner.currentOffset();
                addToken(new ParsedToken(ParsedTokenType.OPERATOR, scanner.next(), offset ));
                return;
            }

            if (Character.isDigit(c)) 
            {
            	if ( bufferContainsNoValidIdentifier() ) 
            	{
	                addUnparsed(offset);
	                offset = scanner.currentOffset();
	                buffer.append(scanner.next());
	                while (!scanner.eof() && NumberNode.isValidNumber(buffer.toString() + scanner.peek())) {
	                    buffer.append(scanner.next());
	                }
	                addToken(new ParsedToken(ParsedTokenType.NUMBER, buffer.toString(), offset));
	                return;
            	}
            	// buffer currently contains a valid identifier , since we allow identifiers to contain digits after the
            	// first character, we'll just add this digit to the buffer as well.
            }
            buffer.append(scanner.next());
        }
        addUnparsed(offset);
    }
    
    private boolean bufferContainsNoValidIdentifier() 
    {
    	return ! Identifier.isValidIdentifier( buffer.toString() );
    }

    private void addUnparsed(int offset)
    {
        if (buffer.length() > 0)
        {
            final String s = buffer.toString();
            
            if ( "rule".equals( s ) ) {
                addToken(new ParsedToken(ParsedTokenType.RULE, s, offset));  
            } else if ( "map".equals( s ) ) {
                addToken(new ParsedToken(ParsedTokenType.MAP, s, offset));  
            } else if ( "set".equals( s ) ) {
                addToken(new ParsedToken(ParsedTokenType.SET, s, offset));            	
            } else if (Identifier.isValidIdentifier(s)) {
                addToken(new ParsedToken(ParsedTokenType.IDENTIFIER, s, offset));
            } else {
                addToken(new ParsedToken(ParsedTokenType.UNPARSED, s, offset));
            }
            buffer.setLength(0);
        }
    }

    public ParsedToken next(ParsedTokenType type) {
        if ( ! peek(type ) ) {
            throw new RuntimeException("Expected "+type+" but got "+peek());
        }
        return next();
    }

    public boolean peek(ParsedTokenType type)
    {
        return peek().hasType(type);
    }
}