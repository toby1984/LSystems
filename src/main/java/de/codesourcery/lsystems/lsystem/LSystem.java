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
import java.util.Collections;
import java.util.List;

import de.codesourcery.lsystems.lsystem.Token.TokenType;

/**
 * Lindenmayer-System.
 *
 * @author tobias.gierke@code-sourcery.de
 */
public class LSystem 
{
	private final TokenSeq axiom;
	public List<Token> state;
	private final List<RewritingRule> rules = new ArrayList<>();
	
	public int desiredRecursionCount=5;
	public int recursionCount=0;
	
	private ParameterProvider parameterProvider = new ParameterProvider() {
        @Override
        public String getParameter(Token token, String identifier) {
            return null;
        }
    };

	public LSystem(TokenSeq axiom) 
	{
		this.axiom = axiom;
		this.state = new ArrayList<>( axiom.toList() );
    }

	public ParameterProvider getParameterProvider() {
		return parameterProvider;
	}
	
	/**
	 * Resets this L-System to it's initial state.
	 * 
	 * @return
	 */
	public final LSystem reset() 
	{
		this.state = new ArrayList<>( axiom.toList() );
		this.recursionCount = 0;
		resetHook();
		return this;
	}
	
	/**
	 * Sets the desired max. recursion count.
	 * 
	 * This count is used by {@link #rewriteRecursively()} to determine
	 * how many times {@link #rewrite()} should be invoked.
	 * 
	 * @param desiredRecursionCount
	 * @throws IllegalArgumentException if <code>desiredRecursionCount</code> is less than 1
	 */
	public void setDesiredRecursionCount(int desiredRecursionCount) 
	{
		if ( desiredRecursionCount < 1 ) {
			throw new IllegalArgumentException("Invalid recursion count: "+desiredRecursionCount);
		}
		this.desiredRecursionCount = desiredRecursionCount;
	}
	
	public int getDesiredRecursionCount() {
		return desiredRecursionCount;
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
	
	public final LSystem addRules(RewritingRule rule,RewritingRule... moreRules ) 
	{
		if (rule == null) {
			throw new IllegalArgumentException("rule must not be null");
		}
		rules.add(rule);
		if ( moreRules != null )
        {
            Collections.addAll(rules,moreRules);
		}
		return this;
	}	
	
	/**
	 * Returns the <b>current<b/> recursion depth.
	 * 
	 * Use {@link #getDesiredRecursionCount()} to find out
	 * how many times {@link #rewriteRecursively()} would actually recursve.</p>
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
    public final void rewriteRecursively()
    {
    	this.recursionCount = 0;
        for ( int i = desiredRecursionCount; i > 0 ; i-- ) {
            rewrite();
        }
    }

	/**
	 * Transforms this L-System by applying all matching rules.
	 * 
	 * <p>This method performs a single recursion, incrementing this system's recursion counter by one.</p>
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
				if ( r.matches( ctx , parameterProvider ) ) 
				{
					r.rewrite( ctx , parameterProvider );
					continue outer;
				}
			}
			ctx.write( ctx.next() );
		}
		this.state = ctx.buffer;
		recursionCount++;
	}

    public void setParameterProvider(ParameterProvider provider) {
        this.parameterProvider = provider;
    }

    protected final class MyContext implements RewritingContext
	{
		private List<Token> lexer;
		private int index=0;
		public final List<Token> buffer = new ArrayList<>();
		
		public MyContext(List<Token> lexer) {
			this.lexer = lexer;
		}
		
		@Override
		public boolean eof() {
			return index>=lexer.size();
		}

		@Override
		public Token peek() {
			return lexer.get(index);
		}

		@Override
		public Token next() {
			return lexer.get(index++);
		}
		
		@Override
		public Token next(TokenType type) 
		{
			if ( ! peek().type.equals( type ) ) {
				throw new RuntimeException("Unexpected token "+peek()+", expected: "+type);
			}
			return next();
		}		

		@Override
		public void write(TokenSeq s) {
			buffer.addAll( s.toList() );
		}
		
		@Override
		public void write(Token s) {
			buffer.add( s );
		}		
		
		@Override
		public boolean peek(TokenType type) {
			return type.equals( peek().type );
		}

		@Override
		public int getRecursionCount() {
			return recursionCount;
		}
	}
}