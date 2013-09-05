package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;
import de.codesourcery.lsystems.dsl.nodes.*;
import junit.framework.TestCase;
import de.codesourcery.lsystems.dsl.ASTValidator.Assignments;
import de.codesourcery.lsystems.dsl.ASTValidator.ValidationResult;

public class ASTValidatorTest extends TestCase {
	
	public void testValidAST() 
	{
		final String s = "set axiom = \"a\"\n"+
	                     "set recursionCount = 123\n"+
		                 "rule: a -> aba\n"+
				         "rule test2: b -> bb";
		
		final AST ast = new Parser().parse( s );

        ExpressionContext context = new ExpressionContext() {
            @Override
            public IASTNode lookup(Identifier identifier) throws UnknownIdentifierException {
                throw new UnknownIdentifierException(identifier);
            }
        };

		final ValidationResult result = new ASTValidator().validate( ast , context );
		
		assertFalse( "Expected no validation errors but got: \n"+result.toString() , result.hasErrors() );
		
		Assignments assignments = ASTValidator.getAssignments( ast );

        Assignment assignment= assignments.getSingleValue(ASTValidator.AXIOM);
        assertEquals( "a" , stringValue(assignment,context) );

        assignment= assignments.getSingleValue(ASTValidator.RECURSION_COUNT);
		assertEquals( 123 , intValue( assignment , context ) );
	}

    private String stringValue(Assignment n,ExpressionContext context) {
        final TermNode value = n.getValue().reduce( context );
        return ((StringNode) value).value;
    }

    private int intValue(Assignment n,ExpressionContext context) {
        final TermNode value = n.getValue().reduce( context );
        return (int) ((NumberNode) value).value;
    }

	public void testValidAST2() 
	{
		final String s = "set axiom = \"F\"\n"+
	            "set recursionCount = 123\n"+
	            "rule: F -> F[+F]F[-F]F";
		
		final AST ast = new Parser().parse( s );
        ExpressionContext context = new ExpressionContext() {
            @Override
            public IASTNode lookup(Identifier identifier) throws UnknownIdentifierException {
                throw new UnknownIdentifierException(identifier);
            }
        };
        final ValidationResult result = new ASTValidator().validate( ast , context );
		
		assertFalse( "Expected no validation errors but got: \n"+result.toString() , result.hasErrors() );

        Assignments assignments = ASTValidator.getAssignments( ast );

        Assignment assignment= assignments.getSingleValue(ASTValidator.AXIOM);
        assertEquals( "F" , stringValue(assignment,context) );

        assignment= assignments.getSingleValue(ASTValidator.RECURSION_COUNT);
		assertEquals( 123 , intValue( assignment , context ) );
	}
}
