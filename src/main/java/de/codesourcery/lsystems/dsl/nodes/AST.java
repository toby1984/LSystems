package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.parsing.ParseContext;
import de.codesourcery.lsystems.dsl.symbols.Scope;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class AST extends AbstractASTNode implements ScopeDefinition
{
	public final Scope scope = new Scope(Scope.GLOBAL_SCOPE_NAME);
	
    @Override
    public AST parse(ParseContext context) 
    {
    	context.pushScope( scope );
    	
    	while ( ! context.eof() ) {
    		addChild( new Statement().parse( context ) );
    	}
    	context.popScope();
        return this;
    }

    @Override
    public String toDebugString()
    {
        return "AST "+( getTextRegion() == null ? "" : getTextRegion().toString() );
    }

	@Override
	protected AST cloneThisNodeOnly() {
		return new AST();
	}

	@Override
	public Scope getDefinitionScope() 
	{
		return scope;
	}

	@Override
	public Scope getScope() {
		return scope;
	}
}
