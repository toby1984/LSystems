package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.ParseContext;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 6:31 PM
 * To change this template use File | Settings | File Templates.
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
        return "AST";
    }
}
