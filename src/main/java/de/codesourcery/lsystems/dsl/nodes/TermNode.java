package de.codesourcery.lsystems.dsl.nodes;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 8:40 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TermNode
{
    double evaluate(ExpressionContext context);

    ASTNode reduce(ExpressionContext context);
}
