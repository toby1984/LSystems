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
package de.codesourcery.lsystems.rendering;

import java.awt.Color;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * A stack to hold turtle state.
 *
 * @author tobias.gierke@code-sourcery.de
 */
public final class TurtleStack {

	private final Stack<TurtleStackEntry2D> stack = new Stack<>();
	
	/**
	 * Pushes the turtle's current state onto the stack.
	 * 
	 * @param turtle
	 */
	public void pushState(Turtle2D turtle) 
	{
		stack.push( new TurtleStackEntry2D(turtle));
	}
	
	/**
	 * Pops the turtle's current state from the stack.
	 * 
	 * @param turtle
	 */
	public void popState(Turtle2D turtle) throws EmptyStackException {
		stack.pop().apply( turtle );
	}
	
	protected final class TurtleStackEntry2D {

		public final Vec2 position;
		public final Vec2 heading;
		public final Color color;

		public TurtleStackEntry2D(Turtle2D turtle) 
		{
			this.position = new Vec2(turtle.position);
			this.heading= new Vec2(turtle.heading);
			this.color = turtle.color;
		}

		public void apply(Turtle2D t) 
		{
			t.setColor( color );
			t.position.set(this.position);
			t.heading.set(this.heading);
		}
	}
}
