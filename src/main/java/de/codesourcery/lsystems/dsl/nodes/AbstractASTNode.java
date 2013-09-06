package de.codesourcery.lsystems.dsl.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.codesourcery.lsystems.dsl.nodes.NodeVisitor.IterationContext;
import de.codesourcery.lsystems.dsl.parsing.ParsedToken;
import de.codesourcery.lsystems.dsl.parsing.TextRegion;
import de.codesourcery.lsystems.dsl.symbols.Scope;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public abstract class AbstractASTNode implements ASTNode {

    private ASTNode parent;
    private final List<ASTNode> children = new ArrayList<>();
    private TextRegion region;

    protected AbstractASTNode() {
    }

    public final void replaceWith(ASTNode child) {
        if (child == null) {
            throw new IllegalArgumentException("child must not be null");
        }
        if ( ! hasParent() ) {
            throw new IllegalStateException("Cannot replaceWith() on node "+this+" that has no parent");
        }
        getParent().replaceChild(this, child);
    }

	@Override
	public Scope getDefinitionScope() 
	{
		if ( hasParent() ) 
		{
			ASTNode node = getParent();
			while( node != null ) {
				if ( node instanceof ScopeDefinition ) {
					return ((ScopeDefinition) node).getScope();
				}
				node = node.getParent();
			}
		} 
		throw new RuntimeException("Failed to determine definition scope of "+this);
	}    

    @Override
    public final void replaceChild(AbstractASTNode oldChild , ASTNode newChild) {

        if (oldChild == null) {
            throw new IllegalArgumentException("old child must not be null");
        }

        if (newChild == null) {
            throw new IllegalArgumentException("new child must not be null");
        }

        final int idx = indexOf( oldChild );
        if ( idx == -1 ) {
            throw new IllegalArgumentException( oldChild+" is no child of "+this);
        }

        children.set( idx , newChild );
        newChild.setParent( this );
    }

    @Override
    public int indexOf(ASTNode child) {
        return children.indexOf( child );
    }

    protected AbstractASTNode(TextRegion region) {
        setTextRegion( region );
    }

    protected AbstractASTNode(ParsedToken token) {
        setTextRegion( token.region );
    }

    @Override
    public final void addChild(ASTNode child) {
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

    protected ASTNode mergeRegion(TextRegion otherRegion) {
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
    	for ( ASTNode child : children ) {
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
    public final void setParent(ASTNode parent) {
        this.parent = parent;
    }

    @Override
    public final List<ASTNode> getChildren() {
        return children;
    }

    @Override
    public final ASTNode child(int index) {
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
        for (ASTNode child : children) {
            result.append(child.toString());
        }
        return result.toString();
    }

    @Override
    public final ASTNode getParent() {
        return parent;
    }
    
    @Override
    public final <T extends AbstractASTNode> List<T> find(final NodeMatcher matcher)
    {
    	final List<T> result = new ArrayList<>();
    	visitPostOrder( new NodeVisitor() {

			@SuppressWarnings("unchecked")
			@Override
			public void visit(ASTNode node, IterationContext context)
			{
				if ( matcher.matches( node ) ) {
					result.add( (T) node );
				}
			}
		});
    	return result;
    }

    @Override
    public final void visitPostOrder(NodeVisitor visitor)
    {
        visitPostOrder( visitor, new MyIterationContext() );
    }
    
    public final void visitPostOrder(NodeVisitor visitor,NodeVisitor.IterationContext context)
    {
        // traverse copy just in case visitor calls replaceWith() on a child
        final List<ASTNode> copy = new ArrayList<>( children );
    	for ( ASTNode child : copy )
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
    public final void visitInOrder(NodeVisitor visitor)
    {
        visitInOrder( visitor, new MyIterationContext() );
    }

    public final void visitInOrder(NodeVisitor visitor,NodeVisitor.IterationContext context)
    {
        context.reset();
        visitor.visit( this , context);
        if ( context.isDontGoDeeper() || context.isStop() ) {
             return;
        }

        final List<ASTNode> copy = new ArrayList<>( children );
        for ( ASTNode child : copy )
        {
            child.visitInOrder(visitor,context);
            if ( context.isStop() ) {
                return;
            }
        }
    }
    
    @Override
    public final ASTNode createCopy(boolean includeChildNodes)
    {
    	final ASTNode result = cloneThisNodeOnly();
    	if (result == null) {
			throw new RuntimeException("Internal error, node "+getClass().getName()+" returned NULL value from cloneThisNodeOnly()");
		}
    	if ( includeChildNodes ) 
    	{
    		for ( ASTNode child : children ) {
    			result.addChild( child.createCopy( true ) );
    		}
    	}
    	return result;
    }
    
    protected abstract ASTNode cloneThisNodeOnly();
    
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