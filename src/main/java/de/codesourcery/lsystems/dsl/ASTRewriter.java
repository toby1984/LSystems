package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.nodes.*;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class ASTRewriter {

    public void reduce(AST ast,final ExpressionContext exprContext) {

        ast.visitPostOrder( new NodeVisitor() {
            @Override
            public void visit(IASTNode node, IterationContext context)
            {
                if ( node instanceof ExpressionNode) {

                    TermNode expr = (ExpressionNode) node;
                    final TermNode reduced = expr.reduce(exprContext);
                    System.out.println("Reduced "+expr+" to "+reduced);
                    node.replaceWith( reduced );
                }
            }
        });
    }
}
