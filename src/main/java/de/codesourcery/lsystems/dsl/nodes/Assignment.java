package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.Identifier;
import de.codesourcery.lsystems.dsl.ParseContext;
import de.codesourcery.lsystems.dsl.ParsedTokenType;

public class Assignment extends ASTNode 
{
	public Identifier name; 

	@Override
	public IASTNode parse(ParseContext context)
	{
		mergeRegion( context.next(ParsedTokenType.SET ) );
		
		name = new Identifier( mergeRegion( context.next(ParsedTokenType.IDENTIFIER ) ).value );
		
		mergeRegion( context.next(ParsedTokenType.ASSIGNMENT ) );

        addChild(new ExpressionNode().parse(context));

		return this;
	}

    public TermNode getValue() {
        return hasChildren() ? (TermNode) child(0) : null;
    }

	@Override
	public String toDebugString()
    {
        if ( hasChildren() ) {
            return name+" = "+child(0).toDebugString();
        }
		return name+" = <no value>";
	}
	
	@Override
	protected Assignment cloneThisNodeOnly() {
		final Assignment result = new Assignment();
		result.name = this.name;
		return result;
	}
}
