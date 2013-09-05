package de.codesourcery.lsystems.dsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.codesourcery.lsystems.dsl.nodes.AST;
import de.codesourcery.lsystems.dsl.nodes.ASTNode;
import de.codesourcery.lsystems.dsl.nodes.Assignment;
import de.codesourcery.lsystems.dsl.nodes.NodeMatcher;
import de.codesourcery.lsystems.dsl.nodes.RuleDefinition;

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
		public final ASTNode node;
		
		public ValidationError(String message, ASTNode node) {
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
		
		public void addError(String message, ASTNode node) {
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
	
	public ValidationResult validate(AST ast) 
	{
		final ValidationResult result = new ValidationResult();
		
		final Assignments assignments = getAssignments( ast );
		
		for ( Map.Entry<Identifier, List<Assignment>> entry :assignments.entrySet() )  
		{
			if ( entry.getValue().size() > 1 ) 
			{
				final Iterator<Assignment> it = entry.getValue().iterator();
				it.next(); // skip first definition
				while ( it.hasNext() ) {
					result.addError( "Variable '"+entry.getKey()+" set more than once" , it.next() );
				}
			}
		}
		
		// verify we have exactly one axiom defined
		validateAssignedExactlyOnce( AXIOM , assignments , result );
		
		// verify we have exactly one recursionCount defined
		validateAssignedExactlyOnce( RECURSION_COUNT , assignments , result );		

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

	private void validateAssignedExactlyOnce(Identifier name,Assignments assignments,ValidationResult result) 
	{
		final List<Assignment> axioms = assignments.get( name );
		if ( axioms.isEmpty() ) {
			result.addError("Found no "+name+" definition");
		} else if ( axioms.size() > 1 ) {
			final Iterator<Assignment> it = axioms.iterator();
			it.next(); // skip first definition
			while ( it.hasNext() ) {
				result.addError( "Duplicate '"+name+"' definition" , it.next() );
			}			
		}
	}
	
	public static final class Assignments 
	{
		private Map<Identifier,List<Assignment>> map = new HashMap<>();
		
		protected void add(Assignment assignment) 
		{
			if (assignment == null) {
				throw new IllegalArgumentException("assignments must not be NULL");
			}
			
			Identifier id = assignment.name;
			List<Assignment> existing = map.get( id );
			if ( existing == null ) {
				existing = new ArrayList<>();
				map.put( id , existing );
			}
			existing.add( assignment );
		}
		
		public Set<Map.Entry<Identifier,List<Assignment>>> entrySet() {
			return map.entrySet();
		}

		public List<Assignment> get(Identifier name) 
		{
			if (name == null) {
				throw new IllegalArgumentException("name must not be NULL");
			}
			List<Assignment> result = map.get(name);
			if ( result == null ) {
				return new ArrayList<>();
			}
			return result;
		}
		
		public Assignment getSingleValue(Identifier name) 
		{
			if (name == null) {
				throw new IllegalArgumentException("name must not be NULL");
			}
			List<Assignment> result = map.get(name);
			if ( result == null || result.isEmpty() ) {
				throw new NoSuchElementException("Found no assignment to '"+name+"'");
			}
			if ( result.size() > 1 ) {
				throw new IllegalStateException("Found more than one assignment to '"+name+"'");
			}
			return result.get(0);
		}		
	}
	
	public static Assignments getAssignments(AST ast) 
	{
		final Assignments result = new Assignments();
		ast.find( new NodeMatcher() 
		{
			@Override
			public boolean matches(ASTNode node) 
			{
				if ( node instanceof Assignment) 
				{
					result.add( (Assignment) node);
				}
				return false;
			}
		});		
		return result;
	}
	
	public static List<RuleDefinition> getRuleNodes(AST ast) {
		return ast.find( new NodeMatcher() {

			@Override
			public boolean matches(ASTNode node) 
			{
				return node instanceof RuleDefinition;
			}
		});		
	}	
}
