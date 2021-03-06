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
import de.codesourcery.lsystems.lsystem.rules.SimpleRule;

/**
 * Abstract helper class that provides static factory methods
 * for common {@link RewritingRule}s.
 *
 * @author tobias.gierke@code-sourcery.de
 */
public abstract class RuleGenerator {

	protected RuleGenerator() {
	}
	
	/**
	 * Creates a {@link SimpleRule} instance that replaced a matching symbol
	 * with some replacement sequence.
	 *  
	 * @param expected
	 * @param replacement
	 * @return
	 */
	public static RewritingRule replaceRule(TokenType expected,TokenSeq replacement) {
		return new SimpleRule( expected , replacement );
	}
	
	/**
	 * Creates a {@link SimpleRule} instance that replaced a matching symbol
	 * with some replacement sequence.
	 *  
	 * @param expected
	 * @param replacement
	 * @return
	 */
	public static RewritingRule replaceRule(TokenType expected,String replacement) 
	{
		return new SimpleRule( expected , ExpressionLexer.parse( replacement ) );
	}	

	public static RewritingRule replaceRule(String expectedString , String replacement) 
	{
		return new SimpleRule( TokenType.CHARACTERS , expectedString , ExpressionLexer.parse( replacement ) );
	}		
}
