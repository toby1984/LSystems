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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyAdapter;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import de.codesourcery.lsystems.dsl.Identifier;
import de.codesourcery.lsystems.dsl.LSystemFactory;
import de.codesourcery.lsystems.dsl.Parser;
import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;
import de.codesourcery.lsystems.dsl.nodes.AST;
import de.codesourcery.lsystems.dsl.nodes.ExpressionContext;
import de.codesourcery.lsystems.dsl.nodes.IASTNode;
import de.codesourcery.lsystems.lsystem.LSystem;
import de.codesourcery.lsystems.lsystem.ParameterProvider;
import de.codesourcery.lsystems.lsystem.RuleGenerator;
import de.codesourcery.lsystems.lsystem.Token;
import de.codesourcery.lsystems.rendering.DefaultTokenTranslator;
import de.codesourcery.lsystems.rendering.HeartRenderer;
import de.codesourcery.lsystems.rendering.LSystemRenderer2D;
import de.codesourcery.lsystems.rendering.MinMaxRenderingContext2D;
import de.codesourcery.lsystems.rendering.PrimitiveType;
import de.codesourcery.lsystems.rendering.RenderingContext2D;
import de.codesourcery.lsystems.rendering.TokenTranslator;
import de.codesourcery.lsystems.rendering.Turtle2D.FloatPolygon;
import de.codesourcery.lsystems.rendering.Vec2;

public class Main extends RuleGenerator
{
	public static final boolean DEBUG = false;
    public static final int RECURSION_COUNT = 10;

    private static long seed = 0xdeadbeef;
	
	private final TokenTranslator myTranslator = new DefaultTokenTranslator() {
		
		{
			mapSymbols( "H" , PrimitiveType.CUSTOM_1 );
			mapSymbols( "G" , PrimitiveType.FORWARD );			
		}
	};	
	
	public static void main(String[] args) {
        new Main().run(args);
	}
	
	protected void run(String[] args) 
	{
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
		frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
		panel.requestFocus();		
	}
	
	protected static LSystem createLSystem(final Random random) 
	{
		final String dsl = "set axiom = F\n"+
                           "set recursionCount = 123\n"+
                           "rule: F -> F[+F]F[-F]F";
		
		final AST ast = new Parser().parse( dsl );


        final ExpressionContext context = new ExpressionContext() {

            @Override
            public IASTNode lookup(Identifier identifier) throws UnknownIdentifierException {
                throw new UnknownIdentifierException( identifier );
            }
        };

		final LSystem result = new LSystemFactory().createLSystem( ast , context );

        final ParameterProvider provider = new ParameterProvider()
        {
            @Override
            public String getParameter(Token token, String identifier)
            {
                switch(identifier)
                {
                    case "angle":
                        return Float.toString( 90 / (float) result.recursionCount );
                    case "len":
                        return Float.toString( 10 / (float) result.recursionCount );
                    case "bigAngle":
                        return "15";
                    case "smallAngle":
                        return "7";
                    default:
                        throw new NoSuchElementException("Unknown parameter '"+identifier+"' in token "+token);
                }
            }
        };
        result.setParameterProvider( provider );
        return result;
	}

	protected class MyPanel extends JPanel 
	{
		private final LSystem lSystem;
		private boolean autoFit = true;
		
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

            lSystem.rewriteRecursively();

			render( lSystem , alphaInDegrees ,g );
		}
		
		private void render(LSystem lSystem, float alphaInDegrees , Graphics2D g) 
		{
			final LSystemRenderer2D renderer = new HeartRenderer();
			renderer.setTokenTranslator( myTranslator );
			renderer.setAlphaInDegrees( alphaInDegrees );
			
			final float width = (getWidth()*0.98f);
			final float height = (getHeight()*0.98f);
			
			final int screenCenterX = getWidth() / 2;
			final int screenCenterY = getHeight() / 2;
			
			final RenderingContext2D ctx2;
			if ( autoFit ) {
				// 1st pass: determine min/max coordinates
				final MinMaxRenderingContext2D ctx = new MinMaxRenderingContext2D();
				renderer.render( lSystem , ctx );

				float modelWidth = ctx.max.x - ctx.min.x ;
				float modelHeight = ctx.max.y - ctx.min.y ;

				float modelCenterX = (ctx.max.x + ctx.min.x)/2.0f;
				float modelCenterY = (ctx.max.y + ctx.min.y)/2.0f;

				float scaleX = width / modelWidth;
				float scaleY = height / modelHeight;

				// 2nd pass: render using scaled coordinates
				ctx2 = new BasicRenderingContext2D( modelCenterX , modelCenterY , scaleX ,scaleY ,screenCenterX,screenCenterY, g );
			} else {
				ctx2 = new BasicRenderingContext2D( 0 , 0 , 1 , 1 ,screenCenterX,screenCenterY, g );				
			}
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
		
		@Override
		public void toScreenCoordinates(float modelX, float modelY, Point point) {
			point.x = screenCenterX + Math.round( ( modelX - cx ) * scaleX );
			point.y = screenCenterY + Math.round( ( modelY - cy ) * scaleY );
		}
		
		protected Polygon toPolygon(FloatPolygon input) {
			
			final Polygon result = new Polygon();
			final Point screenCoords = new Point();
			
			for ( int i = 0 ; i < input.npoints ; i++ ) 
			{
				toScreenCoordinates( input.xpoints[i] , input.ypoints[i] , screenCoords );
				result.addPoint( screenCoords.x , screenCoords.y );
			}
			return result;
		}
		
		@Override
		public void drawLine(Color color , Vec2 p1, Vec2 p2) {

			int x1 = screenCenterX + Math.round( (p1.x-cx)*scaleX );
			int y1 = screenCenterY + Math.round( (p1.y-cy)*scaleY );

			int x2 = screenCenterX + Math.round( (p2.x-cx)*scaleX );
			int y2 = screenCenterY + Math.round( (p2.y-cy)*scaleY );
			
			g.setColor(color);
			g.drawLine( x1,y1,x2,y2 );
		}

		private  void drawCircle(Color color, Vec2 center, float radius,boolean filled) 
		{
			int w = 2 * (int) radius;
			int h = 2 * (int) radius;
			
			float x = center.x-radius;
			float y = center.y-radius;
			
			int x1 = screenCenterX + Math.round( (x-cx)*scaleX );
			int y1 = screenCenterY + Math.round( (y-cy)*scaleY );
			
			g.setColor(color);
			g.fillArc( x1 , y1 , w , h , 0 , 360 );
		}

		@Override
		public void drawCircle(Color color, Vec2 p1, float radius) 
		{
			drawCircle(color, p1, radius, false );
		}
		
		@Override
		public void drawFilledCircle(Color color, Vec2 center, float radius) {
			drawCircle(color, center, radius, true );			
		}
		@Override
		public void drawPolygon(Color color , FloatPolygon polygon) 
		{
			g.setColor(color);
			g.drawPolygon( toPolygon( polygon ) );			
		}
		
		@Override
		public void drawFilledPolygon(Color color , FloatPolygon polygon) 
		{
			g.setColor(color);
			g.fillPolygon( toPolygon( polygon ) );
		}				
	}
}