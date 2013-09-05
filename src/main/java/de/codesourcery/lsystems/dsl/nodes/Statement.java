package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.ParseContext;
import de.codesourcery.lsystems.dsl.ParsedTokenType;

public class Statement extends ASTNode 
{
	@Override
	public IASTNode parse(ParseContext context)
	{
		if ( context.peek(ParsedTokenType.RULE ) )
		{
			addChild( new RuleDefinition().parse( context ) );
		} 
		else 
		{
			addChild( new ExpressionNode().parse( context ) );
		}
		return this;
	}

	@Override
	public String toDebugString() {
		return "Statement";
	}

	@Override
	protected IASTNode cloneThisNodeOnly() {
		return new Statement();
	}

}
