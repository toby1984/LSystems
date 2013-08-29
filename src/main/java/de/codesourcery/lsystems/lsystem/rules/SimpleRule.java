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
package de.codesourcery.lsystems.lsystem.rules;

import de.codesourcery.lsystems.lsystem.ParameterProvider;
import de.codesourcery.lsystems.lsystem.RewritingContext;
import de.codesourcery.lsystems.lsystem.RewritingRule;
import de.codesourcery.lsystems.lsystem.Token.TokenType;
import de.codesourcery.lsystems.lsystem.TokenSeq;

public final class SimpleRule implements RewritingRule {

	private final TokenType expected;
	private final TokenSeq replacement;
	
	public SimpleRule(TokenType expected,TokenSeq replacement) 
	{
		this.expected = expected;
		this.replacement = replacement;
	}
	
	@Override
	public boolean matches(RewritingContext context,ParameterProvider provider) 
	{
		return context.peek(expected);
	}

	@Override
	public void rewrite(RewritingContext context,ParameterProvider provider) 
	{
		context.next(expected);
		context.write( replacement );
	}
	
	@Override
	public String toString() {
		return expected+" -> "+replacement;
	}
}