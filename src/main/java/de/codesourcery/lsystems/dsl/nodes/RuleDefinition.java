package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.Identifier;
import de.codesourcery.lsystems.dsl.ParseContext;
import de.codesourcery.lsystems.dsl.ParsedTokenType;
import de.codesourcery.lsystems.lsystem.ExpressionLexer;
import de.codesourcery.lsystems.lsystem.RewritingRule;
import de.codesourcery.lsystems.lsystem.Token.TokenType;
import de.codesourcery.lsystems.lsystem.TokenSeq;
import de.codesourcery.lsystems.lsystem.rules.SimpleRule;

public class RuleDefinition extends ASTNode {

	public Identifier ruleName; // may be NULL
	
	public String expectedExpression;
	public String replacementExpression;
	
	@Override
	public IASTNode parse(ParseContext context)
	{
		// rule somerule: a -> b
		
		mergeRegion( context.next( ParsedTokenType.RULE ) );
	
		if (  context.peek( ParsedTokenType.IDENTIFIER ) ) { // rule name
			this.ruleName = new Identifier( mergeRegion( context.next(ParsedTokenType.IDENTIFIER ) ).value );
		}
		
		mergeRegion( context.next(ParsedTokenType.COLON ) );
		
		this.expectedExpression = parseStringAndMerge(context);
		
		mergeRegion( context.next(ParsedTokenType.ARROW) );		
		this.replacementExpression = parseStringAndMerge(context); 
		
		return this;
	}
	
	private String parseStringAndMerge(ParseContext context) 
	{
		final boolean oldState = context.isSkipWhitespace();
		try {
			String value = "";
			do {
				if ( ! context.peek().isWhitespace() ) {
					value += mergeRegion( context.next() ).value;
					context.setSkipWhitespace( false );
				} else {
					context.fail("Expected at least one symbol to map from");
				}
			} while ( ! context.eof() && ! context.peek().isWhitespace() );
			return value;
		} 
		finally {
			context.setSkipWhitespace(oldState);
		}
	}

	@Override
	public String toDebugString() {
		return "rule";
	}
	
	@Override
	protected RuleDefinition cloneThisNodeOnly() 
	{
		final RuleDefinition result = new RuleDefinition();
		result.expectedExpression = this.expectedExpression;
		result.replacementExpression = this.replacementExpression;
		result.ruleName = this.ruleName;
		return result;
	}

	public RewritingRule toRewritingRule() 
	{
		final TokenSeq replacement = ExpressionLexer.parse( this.replacementExpression );
		
		if ( ruleName != null ) 
		{
			return new SimpleRule( this.ruleName.toString() , TokenType.CHARACTERS , this.expectedExpression , replacement );
		}
		return new SimpleRule( TokenType.CHARACTERS , this.expectedExpression , replacement );		
	} 	
}
