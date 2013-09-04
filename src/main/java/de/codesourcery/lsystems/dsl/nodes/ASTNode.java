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

    public void addChild(ASTNode child) {
        if (child == null) {
            throw new IllegalArgumentException("child must not be NULL");
        }
        children.add(child);
        child.setParent(this);

        if ( child.getRegion() != null ) {
            mergeRegion( child.getRegion() );
        }
    }

    protected void setTextRegion(TextRegion region)
    {
        if ( region == null ) {
            throw new IllegalArgumentException("region cannot be NULL");
        }
        this.region = region;
    }

    public ParsedToken mergeRegion(ParsedToken token) {
        mergeRegion( token.region );
        return token;
    }

    public ASTNode mergeRegion(TextRegion otherRegion) {
        if ( this.region == null ) {
            this.region = otherRegion;
        } else {
            this.region = this.region.merge( otherRegion );
        }
        return this;
    }

    public TextRegion getRegion() {
        return region;
    }

    public final void reverseChildren() {
        Collections.reverse(children);
    }

    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    public List<ASTNode> getChildren() {
        return children;
    }

    public ASTNode child(int index) {
        return children.get(index);
    }

    public boolean hasParent() {
        return parent != null;
    }

    public boolean hasChildren() {
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

    public ASTNode getParent() {
        return parent;
    }
}
