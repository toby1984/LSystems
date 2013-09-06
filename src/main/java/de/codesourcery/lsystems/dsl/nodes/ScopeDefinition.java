package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.symbols.Scope;

/**
 * AST nodes implementing this interface define a scope / block.
 * 
 * <p>
 * Symbols are associated with the scope/block they are defined in.
 * </p>
 * 
 * @author tobias.gierke@tobias.gierke@code-sourcery.de
 */
public interface ScopeDefinition extends ASTNode {

	/**
	 * Returns the scope defined by this AST node.
	 * @return
	 */
	public Scope getScope();
}
