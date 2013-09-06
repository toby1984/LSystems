package de.codesourcery.lsystems.dsl.symbols;

import java.util.Map;

import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;

public interface SymbolTable 
{
	boolean hasSymbol(Identifier identifier);
	
	boolean hasSymbol(Identifier identifier,boolean searchParentScopes);
	
	Symbol getSymbol(Identifier identifier) throws UnknownIdentifierException;
	
	Symbol getSymbol(Identifier identifier,boolean searchParentScopes) throws UnknownIdentifierException;
	
	void addOrUpdateSymbol(Symbol symbol);

	Map<Identifier, Symbol> getSymbols();
}
