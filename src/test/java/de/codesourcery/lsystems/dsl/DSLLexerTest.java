package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;
import de.codesourcery.lsystems.dsl.nodes.*;
import junit.framework.TestCase;

public class DSLLexerTest extends TestCase {

	public void testAssignment() throws Exception 
	{
		final String s = "set axiom = \"abc\"";
		
		final AST ast = parse( s );
		assert( ast.child(0) instanceof Statement);
		assert( ast.child(0).child(0) instanceof Assignment);
		
		final Assignment def = (Assignment) ast.child(0).child(0);
		assertEquals( new Identifier("axiom") , def.name );
        ExpressionContext context=new ExpressionContext() {
            @Override
            public IASTNode lookup(Identifier identifier) throws UnknownIdentifierException {
                throw new UnknownIdentifierException(identifier);
            }
        };
        assertEquals( "abc" , ((StringNode) def.getValue().reduce( context )).value );
	}
	
	private AST parse(String s) throws Exception {
		return new Parser().parse( s );
	}
	
	public void testDefineAnonymousRule() throws Exception {
		
		final String s = "rule: ab -> cd";
		
		final AST ast = parse( s );
		assert( ast.child(0) instanceof Statement);
		assert( ast.child(0).child(0) instanceof RuleDefinition);
		
		RuleDefinition def = (RuleDefinition) ast.child(0).child(0);
		assertNull( def.ruleName );
		assertEquals( "ab" , def.expectedExpression);
		assertEquals( "cd" , def.replacementExpression );
	}	
	
	public void testDefineNamedRule() throws Exception {
		
		final String s = "rule myFanceRule : ab -> cd";
		
		final AST ast = parse( s );
		assert( ast.child(0) instanceof Statement);
		assert( ast.child(0).child(0) instanceof RuleDefinition);
		
		RuleDefinition def = (RuleDefinition) ast.child(0).child(0);
		assertEquals( new Identifier("myFanceRule") , def.ruleName );
		assertEquals( "ab" , def.expectedExpression);
		assertEquals( "cd" , def.replacementExpression );
	}	
}
