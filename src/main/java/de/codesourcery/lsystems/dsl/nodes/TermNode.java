package de.codesourcery.lsystems.dsl.nodes;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public interface TermNode
{
    public static enum TermType {
        INTEGER,
        FLOATING_POINT,
        VOID, UNKNOWN
    }

    double evaluate(ExpressionContext context);

    ASTNode reduce(ExpressionContext context);

    public TermType getType(ExpressionContext context);
}
