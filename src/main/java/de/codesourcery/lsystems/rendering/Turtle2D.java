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

/**
 * 2D turtle.
 *
 * @author tobias.gierke@code-sourcery.de
 */
public final class Turtle2D {

	private final Vec2 tmp = new Vec2(0,0);
	
	public final Vec2 position = new Vec2(0,0);
	public final Vec2 heading = new Vec2(0,-1);
	public Color color = Color.BLACK;
	public boolean isPenDown = false;
	private final RenderingContext2D context;
	
	public Turtle2D(RenderingContext2D context) {
		this.context = context;
	}
	
	public void penDown() {
		isPenDown = true;
	}
	
	public void penUp() {
		isPenDown = false;
	}
	
	public void rotateLeft(float angleInDeg) {
		heading.rotate( -angleInDeg );
	}
	
	public void rotateRight(float angleInDeg) {
		heading.rotate( angleInDeg );
	}	
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void move(float len) 
	{
		tmp.set( heading ).multiply( len ).plus( position );
		if ( isPenDown ) {
			context.drawLine( color, position , tmp );
		}
		position.set( tmp );
	}
	
	public void circle(float radius) 
	{
		if ( isPenDown ) 
		{
			context.drawCircle( color , position , radius );
		}
	}
}