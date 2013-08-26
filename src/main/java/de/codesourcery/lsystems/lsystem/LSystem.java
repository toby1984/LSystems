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

import java.util.ArrayList;
import java.util.List;

/**
 * Lindenmayer-System.
 *
 * @author tobias.gierke@code-sourcery.de
 */
public class LSystem 
{
	private final String axiom;
	public String state;
	private final List<RewritingRule> rules = new ArrayList<>();
	private int recursionCount=0;
	
	public LSystem(String axiom) 
	{
		this.state = axiom;
		this.axiom = axiom;
	}
	
	/**
	 * Resets this L-System to it's initial state.
	 * 
	 * @return
	 */
	public final LSystem reset() {
		this.state = axiom;
		this.recursionCount = 0;
		resetHook();
		return this;
	}
	
	protected void resetHook() {
	}
	
	/**
	 * Adds a rewriting rule to this system.
	 * 
	 * @param rule
	 * @return
	 */
	public final LSystem addRule(RewritingRule rule) 
	{
		if (rule == null) {
			throw new IllegalArgumentException("rule must not be null");
		}
		this.rules.add(rule);
		return this;
	}
	
	/**
	 * Returns the current recursion depth.
	 * 
	 * @return recursion depth, 0 if {@link #rewrite()} has not been called since the last
	 * call to {@link #reset()} / instantation of this object.
	 */
	public final int getRecursionCount() {
		return recursionCount;
	}
	
	/**
	 * Transforms this L-System by applying all matching rules.
	 * 
	 * <p>This method performs one recursion, incrementing this system's recursion counter by one.</p>
	 * 
	 * @see #getRecursionCount()
	 */
	public final void rewrite() 
	{
		final MyContext ctx = new MyContext( this.state );
outer:		
		while( ! ctx.eof() ) 
		{
			for ( RewritingRule r : rules ) {
				if ( r.matches( ctx ) ) 
				{
					r.rewrite( ctx );
					continue outer;
				}
			}
			ctx.write( ctx.next() );
		}
		this.state = ctx.buffer.toString();
		recursionCount++;
	}
	
	protected final class MyContext implements RewritingContext 
	{
		private String state;
		private int index = 0;
		
		private char currentSymbol;
	
		public final StringBuilder buffer = new StringBuilder();
		
		public MyContext(String initialState) {
			state = initialState;
			currentSymbol = initialState.charAt(0);
		}
		
		@Override
		public boolean eof() {
			return index >= state.length();
		}

		@Override
		public char peek() {
			return currentSymbol;
		}

		@Override
		public char next() {
			char result = currentSymbol;
			index++;
			if ( index < state.length() ) {
				currentSymbol = state.charAt(index);
			}
			return result;
		}

		@Override
		public void write(String s) {
			buffer.append( s );
		}

		@Override
		public void write(char c) {
			buffer.append(c);
		}
	}
}