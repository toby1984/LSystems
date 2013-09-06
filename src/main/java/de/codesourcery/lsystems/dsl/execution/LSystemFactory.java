package de.codesourcery.lsystems.dsl.execution;

import de.codesourcery.lsystems.dsl.execution.ASTValidator.ValidationResult;
import de.codesourcery.lsystems.dsl.nodes.AST;
import de.codesourcery.lsystems.dsl.nodes.ExpressionContext;
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

        final LSystemInterpreter engine = new LSystemInterpreter();
        engine.setAST( ast );
        engine.run();

		final String axiomSymbols = engine.getStringValue(ASTValidator.AXIOM);
        final int recursionCount = engine.getIntValue( ASTValidator.RECURSION_COUNT);

        final TokenSeq axiom = ExpressionLexer.parse( axiomSymbols );

        final LSystem result = new LSystem( axiom );
        result.setDesiredRecursionCount( recursionCount );

		for ( RuleDefinition r : ASTValidator.getRuleNodes( ast ) ) {
			result.addRule( r.toRewritingRule() );
		}
		return result;
	}
}
