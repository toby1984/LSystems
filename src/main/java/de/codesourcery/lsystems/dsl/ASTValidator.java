package de.codesourcery.lsystems.dsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import de.codesourcery.lsystems.dsl.nodes.*;
import jdk.nashorn.internal.ir.Assignment;
import org.apache.commons.lang.StringUtils;

/**
 * Performs semantic validation on an {@link AST}.
 * 
 * @author tobias.gierke@code-sourcery.de
 */
public class ASTValidator 
{
	public static final Identifier AXIOM = new Identifier("axiom" );
	public static final Identifier RECURSION_COUNT = new Identifier("recursionCount");
	
	public static final class ValidationError 
	{
		public final String message;
		public final IASTNode node;
		
		public ValidationError(String message, IASTNode node) {
			if ( message == null ) {
				throw new IllegalArgumentException("message must not be NULL");
			}
			this.message = message;
			this.node = node;
		}
		
		@Override
		public String toString() {
			return message;
		}
	}
	
	public static final class ValidationResult 
	{
		public final List<ValidationError> errors = new ArrayList<>();
		
		public void addError(String message) 
		{
			addError(message,null);
		}		
		
		public void addError(String message, IASTNode node) {
			addError( new ValidationError(message,node ) );
		}
		
		@Override
		public String toString() 
		{
			return StringUtils.join( errors,  "\n" );
		}
		
		private void addError(ValidationError error) {
			if ( error == null ) {
				throw new IllegalArgumentException("error must not be NULL");
			}
			this.errors.add(error);
		}
		
		public void assertNoErrors() 
		{
			if ( hasErrors() ) {
				throw new IllegalStateException("AST has validation errors: "+toString());
			}
		}
		
		public boolean hasErrors() {
			return ! errors.isEmpty();
		}
	}
	
	public ValidationResult validate(AST ast,ExpressionContext context)
	{
		final ValidationResult result = new ValidationResult();
		
		// verify rule names (if set) are unique
		final Set<Identifier> ruleNames = new HashSet<>();
		for ( RuleDefinition r :  getRuleNodes(ast) ) {
			if ( r.ruleName != null ) 
			{
				if ( ruleNames.contains( r.ruleName ) ) {
					result.addError("Duplicate rule name '"+r.ruleName, r );
				} else {
					ruleNames.add( r.ruleName );
				}
			}
		}
		return result;
	}

	public static List<RuleDefinition> getRuleNodes(AST ast) {
		return ast.find( new NodeMatcher() {

			@Override
			public boolean matches(IASTNode node)
			{
				return node instanceof RuleDefinition;
			}
		});		
	}	
}
