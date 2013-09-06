package de.codesourcery.lsystems.dsl;

import junit.framework.TestCase;
import de.codesourcery.lsystems.dsl.nodes.AST;
import de.codesourcery.lsystems.dsl.nodes.ASTNode;
import de.codesourcery.lsystems.dsl.nodes.ExpressionNode;
import de.codesourcery.lsystems.dsl.nodes.IdentifierNode;
import de.codesourcery.lsystems.dsl.nodes.NumberNode;
import de.codesourcery.lsystems.dsl.nodes.OperatorNode;
import de.codesourcery.lsystems.dsl.nodes.Statement;
import de.codesourcery.lsystems.dsl.nodes.TermNode;
import de.codesourcery.lsystems.dsl.parsing.Parser;
import de.codesourcery.lsystems.dsl.symbols.Identifier;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class ParserTest extends TestCase {

    public void testParseIntAssignment() {

        final String s = "a = 3";
        final AST ast = new Parser().parse(s);
        assertTrue( ast.hasChildren() );
        assertEquals(Statement.class, ast.child(0).getClass());

        ASTNode expr = ast.child(0).child(0);
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
