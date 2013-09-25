package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.parsing.ParseContext;
import de.codesourcery.lsystems.dsl.parsing.ParsedTokenType;

public class Statement extends AbstractASTNode 
{
	@Override
	public ASTNode parse(ParseContext context)
	{
		if ( context.peek(ParsedTokenType.CURLY_BRACE_OPEN ) ) 
		{
			addChild( new Block().parse(context) );
		} 
		else if ( context.peek( ParsedTokenType.RULE ) )
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
	protected ASTNode cloneThisNodeOnly() {
		return new Statement();
	}

}
