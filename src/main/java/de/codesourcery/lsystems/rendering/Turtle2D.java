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
	
	private boolean recordingShape = false;
	private final FloatPolygon polygon = new FloatPolygon();
	
	public Turtle2D(RenderingContext2D context) {
		this.context = context;
	}

	public static final class FloatPolygon 
	{
		public int npoints = 0;
		public float[] xpoints;
		public float[] ypoints;

		public FloatPolygon(FloatPolygon polygon) 
		{
			this.xpoints = new float[ polygon.npoints ];
			this.ypoints = new float[ polygon.npoints ];
			this.npoints = polygon.npoints;
			
			System.arraycopy( polygon.xpoints, 0, this.xpoints, 0, polygon.npoints );
			System.arraycopy( polygon.ypoints, 0, this.ypoints, 0, polygon.npoints );
		}
		
		public FloatPolygon() {
			xpoints = new float[32];
			ypoints = new float[32];
		}
		
		public int size() { return npoints; }
		
		public void reset() {
			npoints=0;
		}
		
		public void addPoint(Vec2 p) 
		{
			if ( npoints == xpoints.length ) {
				float [] tmp = new float[ xpoints.length * 2 ];
				System.arraycopy( xpoints , 0 , tmp , 0 , xpoints.length );
				xpoints = tmp;
				tmp = new float[ ypoints.length * 2 ];
				System.arraycopy( ypoints , 0 , tmp , 0 , ypoints.length );
				ypoints = tmp;			
			}
			xpoints[npoints]=p.x;
			ypoints[npoints]=p.y;
			npoints++;
		}
	}
	
	public void startShape() 
	{
		if ( recordingShape ) {
			throw new IllegalStateException("Already recording a shape, call endShape() first");
		}
		recordingShape = true;
	}
	
	public FloatPolygon endShape() 
	{
		if ( ! recordingShape ) {
			throw new IllegalStateException("Not recording a shape ?");
		}
		try 
		{
			return new FloatPolygon( this.polygon );
		} 
		finally 
		{
			recordingShape = false;
			this.polygon.reset();
		}
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
		if ( isPenDown ) 
		{
			if ( recordingShape ) {
				polygon.addPoint( position );
				polygon.addPoint( tmp );
			} else {
				context.drawLine( color, position , tmp );
			}
		}
		position.set( tmp );
	}
	
	public void filledCircle(float radius) 
	{
		circle(radius,true);
	}	
	
	public void circle(float radius) 
	{
		circle(radius,false);
	}	
	
	private void circle(float radius,boolean filled) 
	{
		if ( isPenDown ) 
		{
			if ( recordingShape) {
				// 	TODO: support recording a circle as polygon/shape
			} else {
				if ( filled ) 
				{
					context.drawFilledCircle( color , position , radius );
				} else {
					context.drawCircle( color , position , radius );
				}
			}
		}
	}	
}