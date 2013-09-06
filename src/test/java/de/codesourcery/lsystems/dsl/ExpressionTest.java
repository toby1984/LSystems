package de.codesourcery.lsystems.dsl;

import junit.framework.TestCase;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class ExpressionTest extends TestCase {

/*    public void testEvaluate1()
    {
        final String s = "set a = 3";
        final AST ast = new Parser().parse(s);
        assertTrue( ast.hasChildren() );
        assertEquals(Statement.class, ast.child(0).getClass());

        final IASTNode child = ast.child(0).child(0);
        assertEquals(Assignment.class, child.getClass());

        Assignment a = (Assignment) child;
        assertEquals( new Identifier("a") , ((Assignment) child).name );

        ExpressionNode expr = (ExpressionNode) ((Assignment) child).getValue();

        final ExpressionContext context=new ExpressionContext() {
            @Override
            public IASTNode lookup(Identifier identifier) throws UnknownIdentifierException {
                throw new UnknownIdentifierException(identifier);
            }
        };
        TermNode result = expr.evaluate( context );
        assertEquals( new NumberNode( 3 , TermNode.TermType.INTEGER ) , result );
    }

    public void testReduce1()
    {
        final String s = "set a = 3";
        final AST ast = new Parser().parse(s);
        assertTrue( ast.hasChildren() );
        assertEquals(Statement.class, ast.child(0).getClass());

        final IASTNode child = ast.child(0).child(0);
        assertEquals(Assignment.class, child.getClass());

        Assignment a = (Assignment) child;
        assertEquals( new Identifier("a") , ((Assignment) child).name );

        ExpressionNode expr = (ExpressionNode) ((Assignment) child).getValue();

        final ExpressionContext context=new ExpressionContext() {
            @Override
            public IASTNode lookup(Identifier identifier) throws UnknownIdentifierException {
                throw new UnknownIdentifierException(identifier);
            }
        };
        TermNode result = expr.evaluate( context );
        assertEquals( new NumberNode( 3 , TermNode.TermType.INTEGER ) , result );

        TermNode reduced = expr.reduce( context );
        assertEquals( new NumberNode( 3 , TermNode.TermType.INTEGER ) , reduced );
    }

    public void testEvaluate2()
    {
        final String s = "set a = 1+2";
        final AST ast = new Parser().parse(s);
        assertTrue( ast.hasChildren() );
        assertEquals(Statement.class, ast.child(0).getClass());

        final IASTNode child = ast.child(0).child(0);
        assertEquals(Assignment.class, child.getClass());

        Assignment a = (Assignment) child;
        assertEquals( new Identifier("a") , ((Assignment) child).name );

        ExpressionNode expr = (ExpressionNode) ((Assignment) child).getValue();

        final ExpressionContext context=new ExpressionContext() {
            @Override
            public IASTNode lookup(Identifier identifier) throws UnknownIdentifierException {
                throw new UnknownIdentifierException(identifier);
            }
        };
        TermNode result = expr.evaluate( context );
        assertEquals( new NumberNode( 3 , TermNode.TermType.INTEGER ) , result );
    }

    public void testReduce2()
    {
        final String s = "set a = 1+2";
        final AST ast = new Parser().parse(s);
        assertTrue( ast.hasChildren() );
        assertEquals(Statement.class, ast.child(0).getClass());

        final IASTNode child = ast.child(0).child(0);
        assertEquals(Assignment.class, child.getClass());

        Assignment a = (Assignment) child;
        assertEquals( new Identifier("a") , ((Assignment) child).name );

        ExpressionNode expr = (ExpressionNode) ((Assignment) child).getValue();

        final ExpressionContext context=new ExpressionContext() {
            @Override
            public IASTNode lookup(Identifier identifier) throws UnknownIdentifierException {
                throw new UnknownIdentifierException(identifier);
            }
        };
        TermNode result = expr.evaluate( context );
        assertEquals( new NumberNode( 3 , TermNode.TermType.INTEGER ) , result );

        TermNode reduced = expr.reduce( context );
        assertEquals( new NumberNode( 3 , TermNode.TermType.INTEGER ) , reduced );
    }*/
}
