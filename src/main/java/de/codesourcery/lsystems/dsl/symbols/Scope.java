package de.codesourcery.lsystems.dsl.symbols;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import de.codesourcery.lsystems.dsl.nodes.ASTNode;
import de.codesourcery.lsystems.dsl.nodes.TermNode.TermType;
import de.codesourcery.lsystems.dsl.symbols.Symbol.SymbolType;

/**
 * Holds all symbols associated with a specific block of code.
 * 
 * Scopes may have an identifier but it's also perfectly fine if they don't. Scope instances
 * are arranged in a tree structure with the top-level/global scope at the root.
 * 
 * @author tobias.gierke@code-sourcery.de
 * 
 * @see SymbolTable
 * @see #getSymbolTable()
 */
public class Scope 
{
	/**
	 * Name the top-level / global scope.
	 * 
	 * @see #isGlobal()
	 */
	public static final Identifier GLOBAL_SCOPE_NAME = Identifier.createInternalIdentifier("$$GLOBAL$$");
	
	private static final boolean DEBUG = true;
	
	public final Identifier name;
	
	private final Scope parentScope;
	public final SymbolTable symbols = new SymbolTableImpl(this);
	
	private final List<Scope> children = new ArrayList<>();
	
	private static final AtomicLong ANON_SCOPE_IDS = new AtomicLong(0);
	
	/**
	 * Creates an anonymous scope with an optional parent scope.
	 * 
	 * @param parentScope
	 */	
	public static Scope createAnonScope(Scope parent) 
	{
		final Identifier name = new Identifier("anonScope"+ANON_SCOPE_IDS.incrementAndGet());
		return new Scope(name , parent );
	}
	
	/**
	 * Create instance.
	 * 
	 * @param name scope identifier, must not be <code>null</code>
	 */
	public Scope(Identifier name) 
	{
		this(name,null);
	}
	
	/**
	 * Create instance.
	 * 
	 * @param name scope identifier, must not be <code>null</code>
	 * @param parentScope optional parent scope
	 */	
	public Scope(Identifier name, Scope parentScope) 
	{
		if ( name == null ) {
			throw new IllegalArgumentException("name must not be NULL");
		}
		this.name = name;
		this.parentScope = parentScope;
		if ( parentScope != null ) {
			parentScope.addChild( this );
		}
	}
	
	private final void addChild(Scope scope) {
		if (scope == null) {
			throw new IllegalArgumentException("scope must not be NULL");
		}
		this.children.add( scope );
	}	
	
	/**
	 * Returns the child scopes of this instance.
	 * 
	 * @return
	 */
	public List<Scope> getChildren() {
		return children;
	}
	
	/**
	 * Returns whether this is the top-level/global scope.
	 * 
	 * The global scope has no parent and its name equals
	 * {@link #GLOBAL_SCOPE_NAME}.
	 * 
	 * @return
	 */
	public boolean isGlobal() {
		return ! hasParentScope() && GLOBAL_SCOPE_NAME.equals( this.name );
	}
	
	/**
	 * Returns the fully-qualified identifier of this scope.
	 * 
	 * @return
	 */
	public Identifier getAbsoluteName() 
	{
		StringBuilder result = new StringBuilder();
		Scope current = this;
		while( current != null ) 
		{
			if ( current.name != null ) 
			{
				if ( result.length() > 0 ) {
					result.append(".");
				}
				result.append( current.name.toString() );
			}
			current = current.getParentScope();
		}
		return Identifier.createInternalIdentifier( result.toString() );
	}	
	
	/**
	 * Returns the name of this 
	 * @return
	 */
	public Identifier getName() {
		return name;
	}
	
	/**
	 * Defines a variable within this scope.
	 * 
	 * <p>If the variable is already declared with the same type, nothing (bad) happens.</p>
	 * <p>If the variable is already defined <b>and</b> it's 
	 * type does not match the type passed to this method, an {@link RuntimeException} will be thrown.
	 * </p>
	 * 
	 * @param a
	 * @param node
	 * @param variableType
	 */
	public void defineVariable(Identifier a,ASTNode node,TermType variableType) 
	{
		if ( node == null ) {
			throw new IllegalArgumentException("node must not be NULL");
		}
		if ( variableType == null || variableType.equals( TermType.UNKNOWN ) || variableType.equals( TermType.VOID ) ) {
			throw new IllegalArgumentException("variableType must not be NULL , VOID or UNKNOWN");
		}
		if ( ! isDefined( a ) ) 
		{
			symbols.addOrUpdateSymbol( new VariableSymbol( a , this , node , variableType) );
			return;
		}
		
		final Symbol s = symbols.getSymbol( a );
		switch(s.type) 
		{
			case VARIABLE:
				final VariableSymbol var = (VariableSymbol) s;
				if ( ! var.getVariableType().equals( variableType ) ) 
				{
					throw new RuntimeException("Internal error, re-defining variable "+var+" with different type "+variableType);
				}
				break;
			default:
				throw new RuntimeException("Internal error,unhandled symbol type: "+s);					
		}
	}
	
	/**
	 * Declares a variable with the type initialized to {@link TermType#UNKNOWN}.
	 * 
	 * <p>If the variable is already declared, nothing happens.</p>
	 * @param a
	 * @param declarationSite
	 */
	public void declareVariable(Identifier a,ASTNode declarationSite) 
	{
		if ( ! isDeclared( a ) ) {
			logDebug("Declaring variable "+a+" at "+declarationSite);
			symbols.addOrUpdateSymbol( new VariableSymbol( a , this , declarationSite , TermType.UNKNOWN ) );
		}
	}		
	
	private void logDebug(String string) {
		if ( DEBUG ) {
			System.out.println("Scope "+this+": "+string);
		}
	}

	public boolean isVariableSymbol(Identifier identifier) {
		return symbols.hasSymbol( identifier ) && symbols.getSymbol( identifier ).hasType( SymbolType.VARIABLE );
	}
	
	public boolean isDeclared(Identifier identifier) {
		return  symbols.hasSymbol( identifier );
	}
	
	public boolean isDefined(Identifier identifier) 
	{
		if ( ! isDeclared( identifier ) ) {
			return false;
		}
		final Symbol s = symbols.getSymbol( identifier );
		switch( s.type ) 
		{
			case VARIABLE:
				return ! TermType.UNKNOWN.equals( ((VariableSymbol) s).getVariableType() );
			default:
				throw new RuntimeException("Internal error,unhandled symbol type: "+s);
		}
	}	
	
	/**
	 * Checks whether this scope has a parent.
	 * 
	 * @return
	 * @see #getParentScope()
	 */
	public boolean hasParentScope() {
		return parentScope != null;
	}

	/**
	 * Returns the parent of this scope.
	 * 
	 * @return parent scope,may be <code>null</code>
	 * @see #getParentScope()
	 */
	public Scope getParentScope() {
		return parentScope;
	}
	
	@Override
	public String toString() {
		return name != null ? name.toString() : "<anon scope>";
	}
	
	/**
	 * Returns the symbol table for this scope.
	 * @return
	 */
	public SymbolTable getSymbolTable() {
		return symbols;
	}
}