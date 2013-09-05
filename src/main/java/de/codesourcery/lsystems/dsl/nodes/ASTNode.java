package de.codesourcery.lsystems.dsl.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.codesourcery.lsystems.dsl.ParsedToken;
import de.codesourcery.lsystems.dsl.TextRegion;
import de.codesourcery.lsystems.dsl.nodes.NodeVisitor.IterationContext;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public abstract class ASTNode implements IASTNode {

    private IASTNode parent;
    private final List<IASTNode> children = new ArrayList<>();
    private TextRegion region;

    protected ASTNode() {
    }

    protected ASTNode(TextRegion region) {
        setTextRegion( region );
    }

    protected ASTNode(ParsedToken token) {
        setTextRegion( token.region );
    }

    @Override
    public final void addChild(IASTNode child) {
        if (child == null) {
            throw new IllegalArgumentException("child must not be NULL");
        }
        children.add(child);
        child.setParent(this);
    }

    protected final void setTextRegion(TextRegion region)
    {
        if ( region == null ) {
            throw new IllegalArgumentException("region cannot be NULL");
        }
        this.region = new TextRegion(region);
    }

    protected final ParsedToken mergeRegion(ParsedToken token) {
        mergeRegion( token.region );
        return token;
    }

    protected IASTNode mergeRegion(TextRegion otherRegion) {
        if ( this.region == null ) {
            this.region = otherRegion;
        } else {
            this.region = this.region.merge( otherRegion );
        }
        return this;
    }

    /**
     * Returns the text region occupied by THIS node only (excluding any
     * text regions occupied by child nodes).
     *  
     * @return
     */
    @Override
    public final TextRegion getTextRegion() {
        return region;
    }
    
    /**
     * Returns the text region occupied by this node and
     * all it's children.
     *  
     * @return
     */    
    @Override
    public final TextRegion getTextRegionIncludingChildren()
    {
    	TextRegion result = this.region == null ? null : new TextRegion(this.region);
    	for ( IASTNode child : children ) {
    		final TextRegion childRegion = child.getTextRegionIncludingChildren() ;
    		
    		if ( childRegion != null ) {
    			if ( result == null ) {
    				result = new TextRegion( childRegion );
    			} else {
    				result = result.merge( childRegion );
    			}
    		}
    	}
        return result;
    }    

    @Override
    public final void reverseChildren() {
        Collections.reverse(children);
    }

    @Override
    public final void setParent(IASTNode parent) {
        this.parent = parent;
    }

    @Override
    public final List<IASTNode> getChildren() {
        return children;
    }

    @Override
    public final IASTNode child(int index) {
        return children.get(index);
    }

    @Override
    public final boolean hasParent() {
        return parent != null;
    }

    @Override
    public final boolean hasChildren() {
        return !children.isEmpty();
    }

    public String toString() {
        final StringBuffer result = new StringBuffer();
        for (IASTNode child : children) {
            result.append(child.toString());
        }
        return result.toString();
    }

    @Override
    public final IASTNode getParent() {
        return parent;
    }
    
    @Override
    public final void visitPostOrder(NodeVisitor visitor)
    {
    	visitPostOrder( visitor, new MyIterationContext() );
    }
    
    @Override
    public final <T extends ASTNode> List<T> find(final NodeMatcher matcher)
    {
    	final List<T> result = new ArrayList<>();
    	visitPostOrder( new NodeVisitor() {

			@SuppressWarnings("unchecked")
			@Override
			public void visit(IASTNode node, IterationContext context)
			{
				if ( matcher.matches( node ) ) {
					result.add( (T) node );
				}
			}
		});
    	return result;
    }
    
    public final void visitPostOrder(NodeVisitor visitor,NodeVisitor.IterationContext context)
    {
    	for ( IASTNode child : children )
    	{
    		if ( context.isDontGoDeeper() )
    		{
    			visitor.visit(  child , context );
    		} else {
    			child.visitPostOrder(visitor,context);
    		}
    		if ( context.isStop() ) {
    			return;
    		}
    	}
    	context.reset();
    	visitor.visit( this , context);
    }
    
    @Override
    public final IASTNode createCopy(boolean includeChildNodes)
    {
    	final IASTNode result = cloneThisNodeOnly();
    	if (result == null) {
			throw new RuntimeException("Internal error, node "+getClass().getName()+" returned NULL value from cloneThisNodeOnly()");
		}
    	if ( includeChildNodes ) 
    	{
    		for ( IASTNode child : children ) {
    			result.addChild( child.createCopy( true ) );
    		}
    	}
    	return result;
    }
    
    protected abstract IASTNode cloneThisNodeOnly();
    
    protected static final class MyIterationContext implements IterationContext {

    	private boolean isStop = false;
        private boolean isDontGoDeeper = false;

        @Override
    	public void reset() {
    		isStop = false;               
    		isDontGoDeeper = false;       
    	}
    	
		@Override
		public void stop() {
			isStop = true;			
		}

		@Override
		public void dontGoDeeper() {
			isDontGoDeeper = true;
		}

        @Override
        public boolean isStop() {
            return isStop;
        }

        @Override
        public boolean isDontGoDeeper() {
            return isDontGoDeeper;
        }
    }
}