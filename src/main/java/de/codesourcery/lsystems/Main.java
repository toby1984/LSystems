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
package de.codesourcery.lsystems;

import static de.codesourcery.lsystems.lsystem.RuleGenerator.replaceRule;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.StyleContext.SmallAttributeSet;

import de.codesourcery.lsystems.lsystem.ExpressionLexer;
import de.codesourcery.lsystems.lsystem.LSystem;
import de.codesourcery.lsystems.lsystem.ParameterProvider;
import de.codesourcery.lsystems.lsystem.RewritingRule;
import de.codesourcery.lsystems.lsystem.Token;
import de.codesourcery.lsystems.lsystem.Token.TokenType;
import de.codesourcery.lsystems.lsystem.rules.StochasticRule;
import de.codesourcery.lsystems.rendering.LSystemRenderer2D;
import de.codesourcery.lsystems.rendering.MinMaxRenderingContext2D;
import de.codesourcery.lsystems.rendering.RenderingContext2D;
import de.codesourcery.lsystems.rendering.Vec2;

public class Main 
{
	private static long seed = 0xdeadbeef;
	
	public static void main(String[] args) {

		MyPanel panel = new MyPanel( createLSystem( new Random() ) );
		panel.setSize( new Dimension(600,400 ) );
		panel.setMinimumSize( new Dimension(600,400 ) );
		panel.setPreferredSize( new Dimension(600,400 ) );

		final JFrame frame = new JFrame();
		frame.setTitle("Press any key to generate a new random plant");
		frame.addKeyListener( panel.keyListener );
		frame.getContentPane().addKeyListener( panel.keyListener );
		frame.getContentPane().setLayout( new BorderLayout() );
		frame.getContentPane().add( panel , BorderLayout.CENTER );
		frame.pack();
		frame.setVisible( true );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		panel.requestFocus();
	}
	
	protected static LSystem createLSystem(final Random random) 
	{
		final LSystem lSystem;
		final ParameterProvider provider = new ParameterProvider() 
		{
			@Override
			public String getParameter(Token token, String identifier) 
			{
				switch(identifier) 
				{
				case "bigAngle":
					return "15";
				case "smallAngle":
					return "7";
				default:
					throw new NoSuchElementException("Unknown parameter '"+identifier+"' in token "+token);
				}
			}
		};
		
		lSystem = new LSystem( ExpressionLexer.parse( "F" ) , provider ) 
		{
			@Override
			protected void resetHook() 
			{
				random.setSeed( seed );
			}
		};
		
		final RewritingRule[] rules = {
				replaceRule( TokenType.FORWARD , "F[+(${bigAngle})F]F[-(${bigAngle})F]F" ),
				replaceRule( TokenType.FORWARD , "F[+(${smallAngle})F]+(${smallAngle})F" ),
				// replaceRule( 'F' , "F" ),
				replaceRule( TokenType.FORWARD , "F[-(${smallAngle})F]-(${smallAngle})-(${smallAngle})F" )
		}; 
		
		lSystem.addRule( new StochasticRule(TokenType.FORWARD , rules )
		{
			@Override
			protected float getRandomNumber() {
				return random.nextFloat();
			}
		});		
		return lSystem;
	}

	protected static class MyPanel extends JPanel 
	{
		private final LSystem lSystem;
		
		public final KeyAdapter keyListener =  new KeyAdapter() 
		{
			public void keyTyped(java.awt.event.KeyEvent e) 
			{
				SwingUtilities.invokeLater( new Runnable() {

					@Override
					public void run() 
					{
						seed = System.currentTimeMillis();
						MyPanel.this.repaint();
					}
				});
			}
		};
		
		public MyPanel(LSystem lSystem) 
		{
			setBackground( Color.WHITE );
			setFocusable( true );
			addKeyListener( keyListener );
			this.lSystem = lSystem;
		}
		
		@Override
		protected void paintComponent(Graphics g) 
		{
			super.paintComponent(g);
			render((Graphics2D) g);
		}

		private void render(final Graphics2D g) 
		{
			final float alphaInDegrees = 15f;
			
			lSystem.reset();
			
			int recursion = 1;
			for ( ; recursion < 9 ; recursion++) 
			{
				lSystem.rewrite();
			}
			render( lSystem , alphaInDegrees ,g );
		}

		private void render(LSystem lSystem, float alphaInDegrees , Graphics2D g) 
		{
			final LSystemRenderer2D renderer = new LSystemRenderer2D();
			renderer.setAlphaInDegrees( alphaInDegrees );
			
			// 1st pass: determine min/max coordinates
			MinMaxRenderingContext2D ctx = new MinMaxRenderingContext2D();
			renderer.render( lSystem , ctx );
			
			float modelWidth = ctx.max.x - ctx.min.x ;
			float modelHeight = ctx.max.y - ctx.min.y ;
			
			float modelCenterX = (ctx.max.x + ctx.min.x)/2.0f;
			float modelCenterY = (ctx.max.y + ctx.min.y)/2.0f;
			
			float scaleX = getWidth() / modelWidth;
			float scaleY = getHeight() / modelHeight;
			
			// 2nd pass: render using scaled coordinates
			int screenCenterX = getWidth() / 2;
			int screenCenterY = getHeight() / 2;
			
			final BasicRenderingContext2D ctx2 = new BasicRenderingContext2D( modelCenterX , modelCenterY , scaleX ,scaleY ,screenCenterX,screenCenterY, g );
			renderer.render( lSystem , ctx2 );			
		} 
	} 

	public static final class BasicRenderingContext2D implements RenderingContext2D 
	{
		private final Graphics2D g;

		private final float cx;
		private final float cy;

		private final float scaleX;
		private final float scaleY;
		
		private final int screenCenterX;
		private final int screenCenterY; 

		private Color lastColor = null;

		public BasicRenderingContext2D(float cx, float cy, float scaleX, float scaleY, int screenCenterX , int screenCenterY , Graphics2D g) 
		{
			this.cx = cx;
			this.cy = cy;
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.screenCenterX = screenCenterX;
			this.screenCenterY = screenCenterY;
			this.g = g;
		}

		private void setColor( Color color ) 
		{
			if ( lastColor == null || lastColor != color ) {
				g.setColor( color );
				lastColor = color;
			}
		}

		@Override
		public void drawLine(Color color , Vec2 p1, Vec2 p2) {

			int x1 = screenCenterX + Math.round( (p1.x-cx)*scaleX );
			int y1 = screenCenterY + Math.round( (p1.y-cy)*scaleY );

			int x2 = screenCenterX + Math.round( (p2.x-cx)*scaleX );
			int y2 = screenCenterY + Math.round( (p2.y-cy)*scaleY );
			
			setColor( color );
			g.drawLine( x1,y1,x2,y2 );
		}

		@Override
		public void drawCircle(Color color, Vec2 p1, float radius) 
		{
			setColor(color);

			int w = 2 * (int) radius;
			int h = 2 * (int) radius;
			
			float x = p1.x-radius;
			float y = p1.y-radius;
			
			int x1 = screenCenterX + Math.round( (x-cx)*scaleX );
			int y1 = screenCenterY + Math.round( (y-cy)*scaleY );
			
			g.fillArc( x1 , y1 , w , h , 0 , 360 );
		}				
	}
}