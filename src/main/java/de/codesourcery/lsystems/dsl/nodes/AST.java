package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.ParseContext;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class AST extends ASTNode
{
    @Override
    public AST parse(ParseContext context) 
    {
    	while ( ! context.eof() ) {
    		addChild( new Statement().parse( context ) );
    	}
        return this;
    }

    @Override
    public String toDebugString() {
        return "AST "+getTextRegion();
    }

	@Override
	protected AST cloneThisNodeOnly() {
		return new AST();
	}
}
