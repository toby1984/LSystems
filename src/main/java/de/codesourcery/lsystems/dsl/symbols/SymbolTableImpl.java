package de.codesourcery.lsystems.dsl.symbols;

import java.util.HashMap;
import java.util.Map;

import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;

public class SymbolTableImpl implements SymbolTable {

	public final Scope scope;
	
	private final Map<Identifier,Symbol> symbols = new HashMap<>();
	
	public SymbolTableImpl(Scope scope) {
		if (scope == null) {
			throw new IllegalArgumentException("scope must not be NULL");
		}
		this.scope = scope;
	}
	
	@Override
	public Map<Identifier,Symbol>  getSymbols() {
		return symbols;
	}
	
	@Override
	public boolean hasSymbol(Identifier identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("identifier must not be NULL");
		}
		return symbols.containsKey( identifier );
	}

	@Override
	public Symbol getSymbol(Identifier identifier) throws UnknownIdentifierException 
	{
		if (identifier == null) {
			throw new IllegalArgumentException("identifier must not be NULL");
		}
		final Symbol result = symbols.get(identifier);
		if ( result == null ) {
			throw new UnknownIdentifierException( identifier );
		}
		return result;
	}

	@Override
	public void addOrUpdateSymbol(Symbol symbol) 
	{
		if (symbol == null) {
			throw new IllegalArgumentException("symbol must not be NULL");
		}
		symbols.put( symbol.name , symbol );
	}

	@Override
	public boolean hasSymbol(Identifier identifier, boolean searchParentScopes) 
	{
		if ( ! searchParentScopes ) {
			return hasSymbol( identifier );
		}
		return scope.hasParentScope() ? scope.getParentScope().symbols.hasSymbol(identifier, searchParentScopes) : false;
	}

	@Override
	public Symbol getSymbol(Identifier identifier, boolean searchParentScopes) throws UnknownIdentifierException 
	{
		if ( ! searchParentScopes ) {
			return getSymbol( identifier );
		}
		if ( ! scope.hasParentScope() ) 
		{
			throw new UnknownIdentifierException(identifier);
		}
		return scope.getParentScope().symbols.getSymbol(identifier, searchParentScopes);
	}
}
