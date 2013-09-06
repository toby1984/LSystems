package de.codesourcery.lsystems.dsl.symbols;

import de.codesourcery.lsystems.dsl.nodes.ASTNode;

/**
 * A symbol.
 * 
 * @author tobias.gierke@voipfuture.com
 */
public abstract class Symbol 
{
	public final Identifier name;
	public final Scope scope;
	public final SymbolType type;
	private final Identifier absoluteName; 
	
	private ASTNode declarationSite;
	
	public static enum SymbolType {
		VARIABLE;
	}
	
	public Symbol(Identifier name,Scope scope,SymbolType type) 
	{
		this(name,scope,type,null);
	}
	
	public Symbol(Identifier name,Scope scope,SymbolType type,ASTNode declarationSite) 
	{
		if ( name == null ) {
			throw new IllegalArgumentException("name must not be NULL");
		}
		if (type == null) {
			throw new IllegalArgumentException("type must not be NULL");
		}
		if ( scope == null ) {
			throw new IllegalArgumentException("scope must not be NULL");
		}
		this.name = name;
		this.scope = scope;
		this.type = type;
		this.declarationSite = declarationSite;
		this.absoluteName = createAbsoluteName();
	}
	
	public Identifier getName() {
		return name;
	}
	
	private Identifier createAbsoluteName() {
		return Identifier.createInternalIdentifier( scope.getAbsoluteName().toString()+"$"+name.toString() );
	}	
	
	public Identifier getAbsoluteName() {
		return absoluteName;
	}
	
	public final boolean hasType(SymbolType t) {
		return t.equals( type );
	}

	public final ASTNode getDeclarationSite() {
		return declarationSite;
	}
	
	public final void setDeclarationSite(ASTNode definitionSite) {
		this.declarationSite = definitionSite;
	}

	@Override
	public int hashCode() {
		final int result = 31  + getAbsoluteName().hashCode();
		return 31 * result + type.hashCode();
	}
	
	@Override
	public final boolean equals(Object obj) 
	{
		if ( obj instanceof Symbol) {
			return this.getAbsoluteName().equals( ((Symbol) obj).getAbsoluteName() ) && this.type.equals( ((Symbol) obj).type );
		}
		return false;
	}	

	@Override
	public String toString() {
		return "Symbol[ '"+name+"' , "+type+"]";
	}
}
