package de.codesourcery.lsystems.dsl.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.codesourcery.lsystems.dsl.ParseContext;
import de.codesourcery.lsystems.dsl.ParsedToken;
import de.codesourcery.lsystems.dsl.TextRegion;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public abstract class ASTNode {

    private ASTNode parent;
    private final List<ASTNode> children = new ArrayList<>();
    private TextRegion region;

    protected ASTNode() {
    }

    protected ASTNode(TextRegion region) {
        setTextRegion( region );
    }

    protected ASTNode(ParsedToken token) {
        setTextRegion( token.region );
    }

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
    public final TextRegion getTextRegion() {
        return region;
    }
    
    /**
     * Returns the text region occupied by this node and
     * all it's children.
     *  
     * @return
     */    
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

    public final void reverseChildren() {
        Collections.reverse(children);
    }

    public final void setParent(ASTNode parent) {
        this.parent = parent;
    }

    public final List<ASTNode> getChildren() {
        return children;
    }

    public final ASTNode child(int index) {
        return children.get(index);
    }

    public final boolean hasParent() {
        return parent != null;
    }

    public final boolean hasChildren() {
        return !children.isEmpty();
    }

    public abstract ASTNode parse(ParseContext context);

    public String toString() {
        final StringBuffer result = new StringBuffer();
        for (ASTNode child : children) {
            result.append(child.toString());
        }
        return result.toString();
    }

    public abstract String toDebugString();

    public final ASTNode getParent() {
        return parent;
    }
}