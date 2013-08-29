package de.codesourcery.lsystems.rendering;

import de.codesourcery.lsystems.lsystem.Token;

public class Primitive {

	public PrimitiveType type;
	public Token token;
	
	public Primitive(PrimitiveType type, Token token) {
		this.type = type;
		this.token = token;
	}
	
}
