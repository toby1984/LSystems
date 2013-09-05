package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.ParseContext;
import de.codesourcery.lsystems.dsl.ParsedToken;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class OperatorNode extends ASTNode implements TermNode {

    public ExpressionNode.Operator type;

    private OperatorNode() {
    }
    
    public OperatorNode(ExpressionNode.Operator op,ParsedToken token) {
        super(token);
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

    public boolean hasType(ExpressionNode.Operator expected) {
        return expected.equals( this.type );
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
    public double evaluate(ExpressionContext context) {
        return type.evaluate( this , context );
    }

    @Override
    public ASTNode reduce(ExpressionContext context) {
        return type.reduce(this, context);
    }

    @Override
    public TermType getType(ExpressionContext context)
    {
        if ( getChildren().size() == 2 ) {
            return this.type.inferType( child(0) , child(1) , context );
        } else if ( getChildren().size() == 1 ) {
            return this.type.inferType( child(0) , null , context );
        }
        throw new RuntimeException("Internal error, cannot infer types for operator with "+getChildren().size()+" arguments ?");
    }

    public String toDebugString() {
        return "Operator("+Character.toString(type.getSymbol())+") "+getTextRegion();
    }
    
	@Override
	protected OperatorNode cloneThisNodeOnly() 
	{
		final OperatorNode result = new OperatorNode();
		result.type = this.type;
		return result;
	}      
}