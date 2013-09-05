package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.Identifier;
import de.codesourcery.lsystems.dsl.ParseContext;
import de.codesourcery.lsystems.dsl.ParsedToken;
import de.codesourcery.lsystems.dsl.ParsedTokenType;

public class Assignment extends ASTNode 
{
	public Identifier name; 
	public String value;
	
	@Override
	public ASTNode parse(ParseContext context) 
	{
		mergeRegion( context.next(ParsedTokenType.SET ) );
		
		name = new Identifier( mergeRegion( context.next(ParsedTokenType.IDENTIFIER ) ).value );
		
		mergeRegion( context.next(ParsedTokenType.ASSIGNMENT ) );
		
		final boolean oldState = context.isSkipWhitespace();
		try {
			value = "";
			boolean required = true;
			do 
			{
				ParsedToken tok = context.peek();
				if ( ! tok.isWhitespace() )
				{
					value += mergeRegion( context.next() ).value;
					context.setSkipWhitespace(false);
					required = false;
				} else if ( required ) {
					context.fail( "Unexpected token in value: "+context.peek() );
				} else {
					break;
				}
			} while ( ! context.eof() );
		} finally {
			context.setSkipWhitespace(oldState);
		}

		return this;
	}

	@Override
	public String toDebugString() {
		return name+" = "+value;
	}
	
	@Override
	protected Assignment cloneThisNodeOnly() {
		final Assignment result = new Assignment();
		result.value = this.value;
		return result;
	}
}
