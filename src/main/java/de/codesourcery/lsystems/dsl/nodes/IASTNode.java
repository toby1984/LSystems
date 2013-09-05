package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.ParseContext;
import de.codesourcery.lsystems.dsl.TextRegion;

import java.util.List;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public interface IASTNode {
    void addChild(IASTNode child);

    TextRegion getTextRegion();

    TextRegion getTextRegionIncludingChildren();

    void reverseChildren();

    void setParent(IASTNode parent);

    List<IASTNode> getChildren();

    IASTNode child(int index);

    boolean hasParent();

    boolean hasChildren();

    IASTNode parse(ParseContext context);

    String toDebugString();

    IASTNode getParent();

    void visitPostOrder(NodeVisitor visitor);

    void visitPostOrder(NodeVisitor visitor,NodeVisitor.IterationContext context);

    <T extends ASTNode> List<T> find(NodeMatcher matcher);

    IASTNode createCopy(boolean includeChildNodes);
}
