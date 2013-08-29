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

import de.codesourcery.lsystems.lsystem.LSystem;
import de.codesourcery.lsystems.lsystem.ParameterProvider;
import de.codesourcery.lsystems.lsystem.Token;
import de.codesourcery.lsystems.lsystem.TokenSeq;
import de.codesourcery.lsystems.lsystem.TokenStream;

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

	protected float drawLen = 2f;
	protected float alphaInDegrees = 15f;
	protected TokenTranslator tokenTranslator = new DefaultTokenTranslator();
	
	public LSystemRenderer2D() {
	}
	
	public void setAlphaInDegrees(float alphaInDegrees) 
	{
		this.alphaInDegrees = alphaInDegrees;
	}
	
	@Override
	public void render(LSystem system, RenderingContext2D context) 
	{
		float drawLen = (float) Math.pow( this.drawLen , system.getRecursionCount() ); 

		final PrimitiveRenderer renderer = createRenderer(system, context, drawLen);

		final TokenStream stream = TokenSeq.toTokenStream( system.state );
		final TokenTranslator translator = getTokenTranslator();
		while( ! stream.eof() ) {
			renderer.renderPrimitive( translator.read( stream ) );
		}
	}
	
	protected TokenTranslator getTokenTranslator() {
		return tokenTranslator;
	}
	
	@Override
	public void setTokenTranslator(TokenTranslator translator) {
		this.tokenTranslator = translator;
	}

	protected PrimitiveRenderer createRenderer(LSystem system,RenderingContext2D context, float drawLen) 
	{
		return new DefaultTokenRenderer(context, drawLen , alphaInDegrees , system );
	}	

	protected static class DefaultTokenRenderer implements PrimitiveRenderer 
	{
		protected final Turtle2D turtle;
		protected final TurtleStack stack;
		protected final float drawLen;
		protected final float alphaInDegrees;
		protected final Color green = darken(Color.GREEN,0.5f);
		protected final ParameterProvider parameterProvider;
		protected final RenderingContext2D context;
		
		public DefaultTokenRenderer(RenderingContext2D context,float drawLen,float alphaInDegrees,LSystem system) 
		{
			this.context = context;
			this.parameterProvider = system.getParameterProvider();
			this.drawLen = drawLen;
			this.alphaInDegrees = alphaInDegrees;
			
			this.turtle = new Turtle2D(context);					
			this.stack = new TurtleStack();

			turtle.setColor(Color.BLACK);
			turtle.position.set( 100,100 );			
			turtle.penDown();
		}
		
		@Override
		public void renderPrimitive(Primitive primitive) 
		{
			switch( primitive.type )
			{
				case PUSH_STATE:
					stack.pushState( turtle );
					break;
				case POP_STATE:
					stack.popState( turtle );
					break;
				case COLOR_GREEN:
					turtle.setColor( green ); 
					break;
				case COLOR_BLUE:
					turtle.setColor( Color.BLUE );
					break;
				case COLOR_RED:
					turtle.setColor( Color.RED );						
					break;	
				case DRAW_FILLED_CIRCLE:
					turtle.circle( 2.0f );
					break;						
				case DRAW_CIRCLE:
					turtle.circle( 2.0f );
					break;	
				case FORWARD_NODRAW:
					turtle.penUp();
					if ( primitive.token.hasParameters() ) {
						turtle.move( drawLen*primitive.token.floatParameter(0,parameterProvider ) );
					} else {
						turtle.move( drawLen );
					}					
					turtle.penDown();
					break;
				case FORWARD:
					if ( primitive.token.hasParameters() ) {
						turtle.move( drawLen*primitive.token.floatParameter(0,parameterProvider ) );
					} else {
						turtle.move( drawLen );
					}
					break;
				case ROTATE_LEFT:
					if ( primitive.token.hasParameters() ) {
						turtle.rotateLeft( primitive.token.floatParameter(0,parameterProvider) );
					} else {
						turtle.rotateLeft( alphaInDegrees );
					}
					break;
				case ROTATE_RIGHT:
					if ( primitive.token.hasParameters() ) {
						turtle.rotateRight( primitive.token.floatParameter(0,parameterProvider) );
					} else {
						turtle.rotateRight( alphaInDegrees );
					}					
					break;
				case NOP:
					System.out.println("*** NOP: "+primitive.token+" ***");
					break;
				default:
					throw new RuntimeException("Unhandled primitive: '"+primitive.type+"'");
			}
		}
	}
	
	public static Color darken(Color c , float f) {
		return new Color( (c.getRed()/255.0f) * f , (c.getGreen()/255.0f ) *f , ( c.getBlue()*255.0f)*f );
	}	
}