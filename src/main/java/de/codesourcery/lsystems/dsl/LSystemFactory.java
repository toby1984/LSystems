package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.ASTValidator.ValidationResult;
import de.codesourcery.lsystems.dsl.nodes.*;
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
	public LSystem createLSystem(AST ast,ExpressionContext context)
	{
		if (ast == null) {
			throw new IllegalArgumentException("ast must not be NULL");
		}
		new ASTValidator().validate( ast , context ).assertNoErrors();

        final LSystemEngine engine = new LSystemEngine();
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
