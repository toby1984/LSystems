package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.ASTValidator.Assignments;
import de.codesourcery.lsystems.dsl.ASTValidator.ValidationResult;
import de.codesourcery.lsystems.dsl.nodes.AST;
import de.codesourcery.lsystems.dsl.nodes.Assignment;
import de.codesourcery.lsystems.dsl.nodes.RuleDefinition;
import de.codesourcery.lsystems.lsystem.ExpressionLexer;
import de.codesourcery.lsystems.lsystem.LSystem;
import de.codesourcery.lsystems.lsystem.TokenSeq;

/**
 * Creates and configures a {@link LSystem} instance from a DSL {@link AST}.
 * 
 * @author tobias.gierke@code-sourcery.de
 */
public class LSystemFactory 
{
	/**
	 * Creates and configures a {@link LSystem} instance from a DSL {@link AST}.
	 * 
	 * @return LSystem configured according to the AST
	 * @throws IllegalStateException if the AST contained validation errors
	 * @see ValidationResult#assertNoErrors()
	 */
	public LSystem createLSystem(AST ast) 
	{
		if (ast == null) {
			throw new IllegalArgumentException("ast must not be NULL");
		}
		new ASTValidator().validate( ast ).assertNoErrors();

		final Assignments assignments = ASTValidator.getAssignments( ast );		
		final Assignment assignment = assignments.getSingleValue( ASTValidator.AXIOM );
		
		final TokenSeq axiom = ExpressionLexer.parse( assignment.value );
		
		final LSystem result = new LSystem( axiom );
		for ( RuleDefinition r : ASTValidator.getRuleNodes( ast ) ) {
			result.addRule( r.toRewritingRule() );
		}
		return result;
	}
}
