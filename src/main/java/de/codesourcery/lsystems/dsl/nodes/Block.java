package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.parsing.ParseContext;
import de.codesourcery.lsystems.dsl.parsing.ParsedTokenType;
import de.codesourcery.lsystems.dsl.symbols.Scope;

public class Block extends AbstractASTNode implements ScopeDefinition
{
	public Scope scope;
	
    @Override
    public Block parse(ParseContext context) 
    {
    	scope = Scope.createAnonScope( context.getCurrentScope() );
    	
    	context.pushScope( scope );
    	
    	mergeRegion( context.next(ParsedTokenType.CURLY_BRACE_OPEN ) );
    	
    	while ( ! context.eof() ) {
    		addChild( new Statement().parse( context ) );
    	}
    	
    	mergeRegion( context.next(ParsedTokenType.CURLY_BRACE_CLOSE) );
    	
    	context.popScope();
        return this;
    }

    @Override
    public String toDebugString()
    {
        return "Block "+( getTextRegion() == null ? "" : getTextRegion().toString() );
    }

	@Override
	protected Block cloneThisNodeOnly() {
		return new Block();
	}

	@Override
	public Scope getScope() {
		return this.scope;
	}
}