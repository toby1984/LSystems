package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.ASTValidator.ValidationResult;
import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;
import de.codesourcery.lsystems.dsl.nodes.AST;
import de.codesourcery.lsystems.dsl.nodes.ExpressionContext;
import de.codesourcery.lsystems.dsl.nodes.IASTNode;
import junit.framework.TestCase;

public class ASTValidatorTest extends TestCase {
	
	public void testValidAST() 
	{
		final String s = "axiom = \"a\"\n"+
	                     "recursionCount = 123\n"+
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
    }
}
