/**
 * Copyright 2013 Tobias Gierke <tobias.gierke@code-sourcery.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codesourcery.lsystems.lsystem;

import de.codesourcery.lsystems.lsystem.Token.TokenType;

/**
 * Rewriting context.
 *
 * <p>Provides operations to traverse the system's current state using 
 * a cursor and rewrite operations to output the state used for the next recursion.</p>
 * 
 * @author tobias.gierke@code-sourcery.de
 */
public interface RewritingContext
{
	/**
	 * Returns whether the cursor is at/past the end
	 * of the current L-system's state.
	 * 
	 * @return
	 */
	boolean eof();
	
	/**
	 * Peeks at the symbol at the current cursor position.
	 * @return
	 */
	Token peek();
	
	/**
	 * Peeks at the symbol at the current cursor position.
	 * @return
	 */
	boolean peek(Token.TokenType type);	
	
	/**
	 * Returns the symbol at the current cursor position and
	 * advances the cursor by one symbol.
	 * 
	 * @return
	 */
	Token next();
	
	/**
	 * Returns the symbol at the current cursor position and
	 * advances the cursor by one symbol.
	 * 
	 * @return
	 */
	Token next(TokenType type);	
	
	/**
	 * Writes a sequence of tokens.
	 * @param s
	 */
	void write(TokenSeq token);
	
	/**
	 * Writes a token.
	 * @param s
	 */
	void write(Token token);	
	
	public int getRecursionCount();
}
