package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.ParseContext;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class OperatorNode extends ASTNode implements TermNode {

    public ExpressionNode.Operator type;

    public OperatorNode(ExpressionNode.Operator op) {
        this.type = op;
    }

    public static boolean isValidOperator(char c) {
        switch(c) {
            case '+':
            case '-':
            case '*':
            case '/':
                return true;
            default:
                return false;
        }
    }

    @Override
    public ASTNode parse(ParseContext context) {
        context.fail("Not implemented");
        return null; // never reached
    }

    @Override
    public String toString()
    {
        return type.toString( this );
    }

    @Override
    public double evaluate() {
        return type.evaluate( this );
    }
}
