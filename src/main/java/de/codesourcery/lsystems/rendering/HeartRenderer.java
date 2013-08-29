package de.codesourcery.lsystems.rendering;

import de.codesourcery.lsystems.lsystem.LSystem;

public class HeartRenderer extends LSystemRenderer2D {

	@Override
	protected PrimitiveRenderer createRenderer(LSystem system,RenderingContext2D context, float drawLen) 
	{
		return new HeartRenderer2D(context, drawLen, alphaInDegrees, system);
	}
	
	protected static final class HeartRenderer2D extends DefaultTokenRenderer {

		public HeartRenderer2D(RenderingContext2D context, float drawLen,float alphaInDegrees, LSystem lsystem) 
		{
			super(context, drawLen, alphaInDegrees, lsystem );
		}
		
		@Override
		public void renderPrimitive(Primitive primitive) 
		{
			switch( primitive.type ) 
			{
				case CUSTOM_1:
				case DRAW_FILLED_CIRCLE: /* render heart */				
				case DRAW_CIRCLE: /* render heart */
					
					float drawLen = this.drawLen*0.3f;
					turtle.startShape();
					
					stack.pushState( turtle );
					
					// render 1st half
					turtle.rotateRight( 43.9f );
					turtle.move( 2*drawLen);
					
					turtle.rotateLeft( 45 );
					turtle.move( drawLen );
					
					turtle.rotateLeft( 22.5f );
					turtle.move( drawLen*0.5f );	
					
					turtle.rotateLeft( 22.5f );
					turtle.move( drawLen*0.5f );
					
					turtle.rotateLeft( 80f );
					turtle.move( drawLen );
					
					stack.popState( turtle ); 
					
					// render 2nd half
					turtle.rotateLeft( 43.9f );
					turtle.move( 2*drawLen);
					
					turtle.rotateRight( 45 );
					turtle.move( drawLen );
					
					turtle.rotateRight( 22.5f );
					turtle.move( drawLen*0.5f );	
					
					turtle.rotateRight( 22.5f );
					turtle.move( drawLen*0.5f );
					
					turtle.rotateRight( 80f );
					turtle.move( drawLen );		
					
					context.drawFilledPolygon( turtle.color , turtle.endShape() );
					break;
				default:
					super.renderPrimitive(primitive);
			}
		}
		
	}
}
