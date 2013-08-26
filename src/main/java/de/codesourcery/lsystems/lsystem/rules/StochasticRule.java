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

import java.util.ArrayList;
import java.util.List;

import de.codesourcery.lsystems.lsystem.RewritingContext;
import de.codesourcery.lsystems.lsystem.RewritingRule;

public abstract class StochasticRule implements RewritingRule {

	private final char expected;
	private final List<Interval> rules=new ArrayList<>();
	
	protected static final class Interval 
	{
		private final float start;
		private final float end;
		public final RewritingRule rule;
		
		public Interval(float start, float end,RewritingRule rule) 
		{
			this.rule = rule;
			this.start = start;
			this.end = end;
		}
		
		public boolean contains(float v) {
			return start <= v && v < end;
		}
		
		@Override
		public String toString() {
			return "["+start+","+end+"[ => "+rule;
		}
	}
	
	public StochasticRule(char expected,RewritingRule[] rules) 
	{
		this.expected =expected;
		final float[] probabilities = new float[rules.length];
		float prop = 1.0f / rules.length;
		for ( int i = 0 ; i < rules.length ; i++ ) {
			probabilities[i] = prop;
		}
		setupRules( rules , probabilities);
	}
	
	public StochasticRule(char expected,RewritingRule[] rules,float[] probabilities) 
	{
		this.expected =expected;
		setupRules( rules , probabilities);
	}
	
	private void setupRules(RewritingRule[] rules,float[] probabilities) {
		
		if ( rules == null || rules.length < 1 || rules.length != probabilities.length) {
			throw new IllegalArgumentException();
		}
		float start = 0.0f;
		for ( int i = 0 ; i < rules.length ; i++)
		{
			float range = 1.0f * probabilities[i];
			final Interval interval = new Interval( start , start+range , rules[i] );
			// System.out.println( "ADD: "+interval);
			this.rules.add( interval );
			start += range;
		}		
	}
	
	@Override
	public boolean matches(RewritingContext context) 
	{
		return context.peek() == expected;
	}

	@Override
	public void rewrite(RewritingContext context) 
	{
		final float value = getRandomNumber();
		for ( Interval iv : rules ) 
		{
			if ( iv.contains( value ) ) 
			{
				iv.rule.rewrite( context );
				return;
			}
		}
		throw new IllegalStateException("No rule matched?");
	}
	
	protected abstract float getRandomNumber();
}
