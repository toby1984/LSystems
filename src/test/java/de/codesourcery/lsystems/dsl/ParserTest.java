package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.nodes.*;
import junit.framework.TestCase;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class ParserTest extends TestCase {

    public void testParseIntAssignment() {

        final String s = "set a = 3";
        final AST ast = new Parser().parse(s);
        assertTrue( ast.hasChildren() );
        assertEquals(Statement.class, ast.child(0).getClass());

        final IASTNode child = ast.child(0).child(0);
        assertEquals(Assignment.class, child.getClass());

        Assignment a = (Assignment) child;
        assertEquals( new Identifier("a") , ((Assignment) child).name );

        TermNode expr = ((Assignment) child).getValue();
        assertEquals(ExpressionNode.class , expr.getClass() );

        TermNode value = (TermNode) expr.child(0);
        assertTrue( value.isLiteralValue() );
        assertEquals(new NumberNode(3, TermNode.TermType.INTEGER), value);
    }

    public void testParseStringAssignment1() {

        final String s = "set a = \"3\"";
        final AST ast = new Parser().parse(s);
        assertTrue( ast.hasChildren() );
        assertEquals(Statement.class, ast.child(0).getClass());

        final IASTNode child = ast.child(0).child(0);
        assertEquals(Assignment.class, child.getClass());

        Assignment a = (Assignment) child;
        assertEquals( new Identifier("a") , ((Assignment) child).name );

        TermNode expr = ((Assignment) child).getValue();
        assertEquals(ExpressionNode.class , expr.getClass() );

        TermNode value = (TermNode) expr.child(0);
        assertTrue( value.isLiteralValue() );
        assertEquals(new StringNode("3"), value);
    }
}
