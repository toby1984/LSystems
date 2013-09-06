package de.codesourcery.lsystems.dsl.utils;

import de.codesourcery.lsystems.dsl.nodes.ASTNode;
import de.codesourcery.lsystems.dsl.nodes.IdentifierNode;
import de.codesourcery.lsystems.dsl.nodes.NodeVisitor;
import de.codesourcery.lsystems.dsl.nodes.NumberNode;
import de.codesourcery.lsystems.dsl.nodes.OperatorNode;
import de.codesourcery.lsystems.dsl.nodes.Statement;
import de.codesourcery.lsystems.dsl.nodes.StringNode;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class ASTPrinter {

    private final StringBuilder buffer = new StringBuilder();

    public String print(ASTNode ast)
    {
        buffer.setLength(0);

        ast.visitInOrder(new NodeVisitor() {
            @Override
            public void visit(ASTNode node, IterationContext context)
            {
                System.out.println("Visiting "+node.toDebugString());
                printNode( node , context );
            }
        });
        return buffer.toString();
    }

    private void printNode(ASTNode node, NodeVisitor.IterationContext context) {

        if ( node instanceof Statement)
        {
            if ( buffer.length() > 0 ) {
                buffer.append("\n");
            }
        } else if ( node instanceof OperatorNode) {
            printOperator((OperatorNode) node);
            context.dontGoDeeper();
        }
        else if ( node instanceof StringNode ) {
            printString( (StringNode) node);
        }
        else if ( node instanceof NumberNode )
        {
            printNumber( (NumberNode) node );
        } else if ( node instanceof IdentifierNode ) {
            printIdentifier((IdentifierNode) node);
        }
    }

    private void printNumber(NumberNode node)
    {
        if ( node.hadDecimalPoint ) {
            buffer.append( node.value );
        } else {
            buffer.append( (long) node.value );
        }
    }

    private void printString(StringNode node) {
        buffer.append( '"' ).append( node.value ).append('"');
    }

    private void printIdentifier(IdentifierNode node) {
        buffer.append( node.value.toString() );
    }

    private void printOperator(OperatorNode node) {

        if ( node.type.isLeftAssociative() )
        {
            // left-associative operators like  "a.b"
            String arg1 = new ASTPrinter().print( node.child(0) );
            String arg2 = new ASTPrinter().print(node.child(1));
            buffer.append( arg1 + " "+ node.type.getSymbol()+ " "+ arg2);
        } else {
            // right-associative operator like ~ , !
            String arg1 = node.type.getArgumentCount() >= 1 ? new ASTPrinter().print( node.child(0) ) : "";
            buffer.append( node.type.getSymbol()+ " "+ arg1);
        }
    }
}