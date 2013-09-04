package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.ParseContext;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class AST extends ASTNode
{
    @Override
    public AST parse(ParseContext context) {
        addChild( new ExpressionNode().parse( context ) );
        return this;
    }

    @Override
    public String toDebugString() {
        return "AST "+getTextRegion();
    }
}
