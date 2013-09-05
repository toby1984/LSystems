package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.nodes.*;
import jdk.nashorn.internal.ir.Assignment;
import junit.framework.TestCase;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class ParserTest extends TestCase {

    public void testParseIntAssignment() {

        final String s = "a = 3";
        final AST ast = new Parser().parse(s);
        assertTrue( ast.hasChildren() );
        assertEquals(Statement.class, ast.child(0).getClass());

        IASTNode expr = ast.child(0).child(0);
        assertEquals(ExpressionNode.class, expr.getClass());

        assertEquals(OperatorNode.class, expr.child(0).getClass());
        final OperatorNode op = (OperatorNode) expr.child(0);

        assertEquals(IdentifierNode.class, op.child(0).getClass());

        final IdentifierNode id = (IdentifierNode) op.child(0);
        assertEquals( new Identifier("a"),  id.value );

        // check RHS
        expr = (TermNode) op.child(1);
        assertEquals(new NumberNode(3, TermNode.TermType.INTEGER), expr );
    }
}
