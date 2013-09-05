package de.codesourcery.lsystems.dsl;

import junit.framework.TestCase;
import de.codesourcery.lsystems.dsl.ASTValidator.Assignments;
import de.codesourcery.lsystems.dsl.ASTValidator.ValidationResult;
import de.codesourcery.lsystems.dsl.nodes.AST;

public class ASTValidatorTest extends TestCase {
	
	public void testValidAST() 
	{
		final String s = "set axiom = a\n"+
	                     "set recursionCount = 123\n"+
		                 "rule: a -> aba\n"+
				         "rule test2: b -> bb";
		
		final AST ast = new Parser().parse( s );
		final ValidationResult result = new ASTValidator().validate( ast );
		
		assertFalse( "Expected no validation errors but got: \n"+result.toString() , result.hasErrors() );
		
		Assignments assignments = ASTValidator.getAssignments( ast );
		assertEquals( "a" , assignments.getSingleValue( ASTValidator.AXIOM ).value );
		assertEquals( "123" , assignments.getSingleValue( ASTValidator.RECURSION_COUNT).value );		
	}
	
	public void testValidAST2() 
	{
		final String s = "set axiom = F\n"+
	            "set recursionCount = 123\n"+
	            "rule: F -> F[+F]F[-F]F";
		
		final AST ast = new Parser().parse( s );
		final ValidationResult result = new ASTValidator().validate( ast );
		
		assertFalse( "Expected no validation errors but got: \n"+result.toString() , result.hasErrors() );
		
		Assignments assignments = ASTValidator.getAssignments( ast );
		assertEquals( "F" , assignments.getSingleValue( ASTValidator.AXIOM ).value );
		assertEquals( "123" , assignments.getSingleValue( ASTValidator.RECURSION_COUNT).value );		
	}
}
