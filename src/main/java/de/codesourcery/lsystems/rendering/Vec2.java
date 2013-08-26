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

/**
 * A 2-dimensional vector that provides basic vector math functions.
 *
 * @author tobias.gierke@code-sourcery.de
 */
public final class Vec2 
{
	public float x;
	public float y;

	public Vec2() {
	}
	
	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2(Vec2 v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	public Vec2 set(Vec2 v) {
		this.x = v.x;
		this.y = v.y;
		return this;
	}
	
	public Vec2 set(float x,float y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Vec2 multiply(float factor) {
		this.x *= factor;
		this.y *= factor;
		return this;
	}

	public Vec2 plus(Vec2 v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}
	
	public Vec2 min(Vec2 v ) {
		this.x = Math.min(this.x , v.x );
		this.y = Math.min(this.y , v.y );
		return this;
	}
	
	public Vec2 max(Vec2 v ) {
		this.x = Math.max(this.x , v.x );
		this.y = Math.max(this.y , v.y );
		return this;
	}	

	public Vec2 minus(Vec2 v) {
		this.x -= v.x;
		this.y -= v.y;
		return this;
	}

	public float len() 
	{
		return (float) Math.sqrt(x*x+y*y);
	}

	public Vec2 normalize() 
	{
		float l = len();
		if ( l != 0 ) {
			x = x / l;
			y = y / l;
		}
		return this;
	}	

	public Vec2 rotate(float angleInDeg) 
	{
		float angleInRad = (float) (angleInDeg*(Math.PI/180.0f));

		double cs = Math.cos(angleInRad);
		double sn = Math.sin(angleInRad);

		double x2 = x * cs - y * sn;
		double y2 = x * sn + y * cs;
		this.x = (float) x2;
		this.y = (float) y2;
		return this;
	}

	public Vec2 minus(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}
	
	public Vec2 plus(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}	
}