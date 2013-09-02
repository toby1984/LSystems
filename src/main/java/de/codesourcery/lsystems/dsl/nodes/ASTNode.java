package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.ParseContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 6:27 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ASTNode {

    public ASTNode parent;
    public final List<ASTNode> children = new ArrayList<>();

    public void addChild(ASTNode child) {
        if ( child == null ) {
            throw new IllegalArgumentException("child must not be NULL");
        }
        children.add( child );
        child.setParent(this);
    }

    public final void reverseChildren() {
        Collections.reverse(children);
    }

    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    public ASTNode child(int index) {
        return children.get(index);
    }

    public boolean hasParent() {
        return parent != null;
    }

    public boolean hasChildren() {
        return ! children.isEmpty();
    }

    public abstract ASTNode parse(ParseContext context);

    public String toString() {
        final StringBuffer result = new StringBuffer();
        for ( ASTNode child : children ) {
            result.append( child.toString() );
        }
        return result.toString();
    }

    public abstract String toDebugString();
}
