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

import de.codesourcery.lsystems.lsystem.ExpressionLexer;
import de.codesourcery.lsystems.lsystem.LSystem;
import de.codesourcery.lsystems.lsystem.ParameterProvider;
import de.codesourcery.lsystems.lsystem.Token;

/**
 * Basic {@link LSystemRenderer} that supports rendering
 * 2-dimensional images of bracketed L-Systems.  
 *
 * <p>
 * The following symbols are understood:
 * 
 * <table border="1">
 *   <thead>
 *   <tr>
 *     <td>Symbol</td>
 *     <td>Interpretation</td>
 *   </tr> 
 *   </thead>
 *   <tr>
 *     <td>[</td>
 *     <td>Pushes current turtle state onto the stack</td>
 *   </tr>
 *   <tr>
 *     <td>]</td>
 *     <td>Pops turtle state from the stack</td>
 *   </tr> 
 *   <tr>
 *     <td>g</td>
 *     <td>Changes the current drawing color to green</td>
 *   </tr>  
 *   <tr>
 *     <td>b</td>
 *     <td>Changes the current drawing color to blue</td>
 *   </tr> 
 *   <tr>
 *     <td>r</td>
 *     <td>Changes the current drawing color to red</td>
 *   </tr>  
 *   <tr>
 *     <td>c</td>
 *     <td>Draws a circle using the current color if the pen is currently down</td>
 *   </tr>   
 *   <tr>
 *     <td>f</td>
 *     <td>Moves the turtle forward without drawing</td>
 *   </tr>   
 *   <tr>
 *     <td>F</td>
 *     <td>Moves the turtle forward while drawing a line</td>
 *   </tr> 
 *   <tr>
 *     <td>+</td>
 *     <td>Rotates the turtle counter-clockwise</td>
 *   </tr>  
 *   <tr>
 *     <td>-</td>
 *     <td>Rotates the turtle clockwise</td>
 *   </tr>           
 * </table>
 * </p>
 * @author tobias.gierke@code-sourcery.de
 */
public class LSystemRenderer2D implements LSystemRenderer {

	private float s = 0.8f;
	private float alphaInDegrees = 15f;
	
	public void setAlphaInDegrees(float alphaInDegrees) 
	{
		this.alphaInDegrees = alphaInDegrees;
	}
	
	@Override
	public void render(LSystem system, RenderingContext2D context) 
	{
		float drawLen = (float) Math.pow( s , system.getRecursionCount() ); 

		final DefaultTokenRenderer renderer = new DefaultTokenRenderer(context, drawLen , alphaInDegrees , system.getParameterProvider() );
		
		for ( Token tok : system.state ) 
		{
			renderer.renderToken( tok );
		}
	}	

	protected static final class DefaultTokenRenderer implements TokenRenderer 
	{
		private final Turtle2D turtle;
		private final TurtleStack stack;
		private final float drawLen;
		private final float alphaInDegrees;
		private final Color green = darken(Color.GREEN,0.5f);
		private final ParameterProvider parameterProvider;
		
		public DefaultTokenRenderer(RenderingContext2D context,float drawLen,float alphaInDegrees,ParameterProvider parameterProvider) 
		{
			this.parameterProvider = parameterProvider;
			this.drawLen = drawLen;
			this.alphaInDegrees = alphaInDegrees;
			
			this.turtle = new Turtle2D(context);					
			this.stack = new TurtleStack();

			turtle.position.set( 0,0 );			
			turtle.penDown();
		}
		
		@Override
		public void renderToken(Token token) 
		{
			switch( token.type )
			{
				case PUSH_STATE:
					stack.push( turtle );
					break;
				case POP_STATE:
					stack.pop( turtle );
					break;
				case COLOR_GREEN:
					turtle.setColor( green ); 
					break;
				case COLOR_BLUE:
					turtle.setColor( Color.BLUE );
				case COLOR_RED:
					turtle.setColor( Color.RED );						
					break;		
				case DRAW_CIRCLE:
					turtle.circle( 2.0f );
					break;	
				case FORWARD_NODRAW:
					turtle.penUp();
					turtle.move( drawLen );
					turtle.penDown();
					break;
				case FORWARD:
					turtle.move( drawLen );
					break;
				case ROTATE_LEFT:
					if ( token.hasParameters() ) {
						turtle.rotateLeft( token.floatParameter(0,parameterProvider) );
					} else {
						turtle.rotateLeft( alphaInDegrees );
					}
					break;
				case ROTATE_RIGHT:
					if ( token.hasParameters() ) {
						turtle.rotateRight( token.floatParameter(0,parameterProvider) );
					} else {
						turtle.rotateRight( alphaInDegrees );
					}					
					break;
				default:
					throw new RuntimeException("Unhandled token: '"+token+"'");
			}
		}
	}
	
	protected static Color darken(Color c , float f) {
		return new Color( (c.getRed()/255.0f) * f , (c.getGreen()/255.0f ) *f , ( c.getBlue()*255.0f)*f );
	}	
}