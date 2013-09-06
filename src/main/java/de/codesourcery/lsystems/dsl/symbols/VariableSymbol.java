package de.codesourcery.lsystems.dsl.symbols;

import de.codesourcery.lsystems.dsl.nodes.ASTNode;
import de.codesourcery.lsystems.dsl.nodes.TermNode.TermType;

public final class VariableSymbol extends Symbol {

	private final TermType variableType;
	
	public VariableSymbol(Identifier name, Scope scope,TermType variableType) 
	{
		super(name, scope , SymbolType.VARIABLE );
		assertValidType( variableType );
		this.variableType = variableType;
	}
	
	public VariableSymbol(Identifier name, Scope scope,ASTNode declarationSite,TermType variableType) 
	{
		super(name, scope , SymbolType.VARIABLE , declarationSite);
		assertValidType( variableType );
		this.variableType = variableType;
	}
	
	private void assertValidType(TermType t) 
	{
		if ( t == null || t == TermType.VOID ) {
			throw new IllegalArgumentException("Invalid type "+t+" for variable "+name);
		}
	}	

	public TermType getVariableType() {
		return variableType;
	}
	
	public boolean hasVariableType(TermType t) {
		return t.equals( variableType );
	}
}
