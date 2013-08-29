package de.codesourcery.lsystems.rendering;

import de.codesourcery.lsystems.lsystem.TokenStream;

public interface TokenTranslator {

	public Primitive read(TokenStream stream);
}
