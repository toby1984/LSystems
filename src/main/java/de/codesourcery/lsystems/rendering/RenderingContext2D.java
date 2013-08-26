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
 * A rendering context provides implementations for 
 * 2-dimensional turtle drawing operations.
 *
 * @author tobias.gierke@code-sourcery.de
 */
public interface RenderingContext2D 
{
	/**
	 * Draws a line with a specific color.
	 * 
	 * @param color
	 * @param p1
	 * @param p2
	 */
	void drawLine( Color color , Vec2 p1, Vec2 p2 );
	
	/**
	 * Draws a circle with a specific color and radius.
	 * 
	 * @param color
	 * @param center
	 * @param radius
	 */
	void drawCircle( Color color , Vec2 center, float radius);			
}