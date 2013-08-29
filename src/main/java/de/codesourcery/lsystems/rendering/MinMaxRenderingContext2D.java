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
import java.awt.Point;

import de.codesourcery.lsystems.rendering.Turtle2D.FloatPolygon;

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
	
	@Override
	public void toScreenCoordinates(float modelX, float modelY, Point point) {
		point.x = (int) modelX;
		point.y = (int) modelY;
	}

	@Override
	public void drawPolygon(Color color , FloatPolygon polygon) 
	{
		for ( int i = 0 ; i < polygon.npoints ; i++ ) {
			this.min.min( polygon.xpoints[i] , polygon.ypoints[i] );
			this.max.max( polygon.xpoints[i] , polygon.ypoints[i] );
		}
	}

	@Override
	public void drawFilledPolygon(Color color , FloatPolygon polygon) {
		drawPolygon( color , polygon );
	}

	@Override
	public void drawFilledCircle(Color color, Vec2 center, float radius) {
		drawCircle(color, center, radius);
	}
}