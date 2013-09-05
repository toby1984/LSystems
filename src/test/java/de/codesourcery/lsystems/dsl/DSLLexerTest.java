package de.codesourcery.lsystems.dsl;

import junit.framework.TestCase;
import de.codesourcery.lsystems.dsl.nodes.AST;
import de.codesourcery.lsystems.dsl.nodes.Assignment;
import de.codesourcery.lsystems.dsl.nodes.RuleDefinition;
import de.codesourcery.lsystems.dsl.nodes.Statement;

public class DSLLexerTest extends TestCase {

	public void testAssignment() throws Exception 
	{
		final String s = "set axiom = abc";
		
		final AST ast = parse( s );
		assert( ast.child(0) instanceof Statement);
		assert( ast.child(0).child(0) instanceof Assignment);
		
		final Assignment def = (Assignment) ast.child(0).child(0);
		assertEquals( new Identifier("axiom") , def.name );
		assertEquals( "abc" , def.value );
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
