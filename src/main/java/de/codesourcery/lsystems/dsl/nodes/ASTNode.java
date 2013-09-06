package de.codesourcery.lsystems.dsl.nodes;

import java.util.List;

import de.codesourcery.lsystems.dsl.parsing.ParseContext;
import de.codesourcery.lsystems.dsl.parsing.TextRegion;
import de.codesourcery.lsystems.dsl.symbols.Scope;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public interface ASTNode 
{
    void addChild(ASTNode child);

    TextRegion getTextRegion();

    TextRegion getTextRegionIncludingChildren();

    void reverseChildren();

    void setParent(ASTNode parent);

    List<ASTNode> getChildren();

    ASTNode child(int index);

    int indexOf(ASTNode child);
    
    /**
     * Returns the scope this node is defined in.
     * 
     * @return
     */
    public Scope getDefinitionScope();

    void replaceWith(ASTNode other);

    boolean hasParent();

    boolean hasChildren();

    ASTNode parse(ParseContext context);

    String toDebugString();

    ASTNode getParent();

    void visitPostOrder(NodeVisitor visitor);

    void visitPostOrder(NodeVisitor visitor,NodeVisitor.IterationContext context);

    void visitInOrder(NodeVisitor visitor);

    void visitInOrder(NodeVisitor visitor,NodeVisitor.IterationContext context);

    <T extends AbstractASTNode> List<T> find(NodeMatcher matcher);

    ASTNode createCopy(boolean includeChildNodes);

    void replaceChild(AbstractASTNode astNode, ASTNode child);
}
