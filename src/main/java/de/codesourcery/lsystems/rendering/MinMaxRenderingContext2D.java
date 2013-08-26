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

public final class MinMaxRenderingContext2D implements RenderingContext2D 
{
	private final Vec2 tmp = new Vec2(0,0);
	
	public final Vec2 min = new Vec2(Float.MAX_VALUE,Float.MAX_VALUE);
	public final Vec2 max = new Vec2(-Float.MAX_VALUE,-Float.MAX_VALUE);
	
	@Override
	public void drawLine(Color color, Vec2 p1, Vec2 p2) {
		this.min.min( p1 );
		this.max.max( p1 );
		this.min.min( p2  );
		this.max.max( p2 );	
	}
	
	@Override
	public void drawCircle(Color color, Vec2 center, float radius) {
		tmp.set( center );
		tmp.minus( radius , radius );
		this.min.min( tmp );
		
		tmp.set( center );
		tmp.plus( radius , radius );
		this.max.max( tmp );				
	}
}