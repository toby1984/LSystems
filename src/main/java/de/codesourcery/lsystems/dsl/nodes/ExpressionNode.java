package de.codesourcery.lsystems.dsl.nodes;

import java.util.Stack;

import de.codesourcery.lsystems.dsl.ParseContext;
import de.codesourcery.lsystems.dsl.ParsedToken;
import de.codesourcery.lsystems.dsl.ParsedTokenType;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class ExpressionNode extends ASTNode implements  TermNode
{
    private final Stack<IASTNode> values = new Stack<>();

    private final Stack<OperatorNode> operators = new Stack<>();

    @Override
    public TermNode evaluate(ExpressionContext context)
    {
        if ( ! hasChildren() ) {
            return this;
        }
        return ((TermNode) child(0)).evaluate(context);
    }

    @Override
    public TermNode reduce(ExpressionContext context) {
        return hasChildren() ? ((TermNode) child(0)).reduce(context) : this;
    }

    public static enum Operator
    {
        PLUS('+', 1) {
            @Override
            public TermNode evaluate(OperatorNode operatorNode,ExpressionContext context)
            {
                final TermNode child1 = ((TermNode) operatorNode.child(0)).evaluate( context );
                final TermNode child2 = ((TermNode) operatorNode.child(1)).evaluate( context );

                if ( isNumeric( child1 , context ) && isNumeric( child2 , context ) )
                {
                    final TermType inferredType = inferType(operatorNode.child(0), operatorNode.child(1), context);
                    return new NumberNode( getDoubleValue( child1 , context) + getDoubleValue( child2 , context) , inferredType);
                }

                // special case: <string> + <string>
                if ( isCompatibleToStringValue( child1 , context ) && isCompatibleToStringValue( child2 , context ) ) {
                    return new StringNode( getStringValue( child1 , context ) + getStringValue( child2 , context ) );
                }
                throw new UnsupportedOperationException("Incompatible operands "+child1+" and "+child2);
            }

            @Override
            public TermNode reduce(OperatorNode operatorNode, ExpressionContext context)
            {
                TermNode value1 = ((TermNode) operatorNode.child(0)).evaluate(context);
                TermNode value2 = ((TermNode) operatorNode.child(1)).evaluate(context);

                if ( isNumeric(value1,context) && isNumeric( value2 , context ) )
                {
                    return numberNode( ((NumberNode) value1).value + ((NumberNode) value2).value  , inferType( value1 , value2 , context ));
                }

                // special case: <string> + <string>
                if ( isCompatibleToStringValue( value1 , context ) && isCompatibleToStringValue( value2 , context ) ) {
                    return new StringNode( getStringValue( value1 , context ) + getStringValue( value2 , context ) );
                }

                return operatorNode;
            }

            @Override
            public TermType inferType(IASTNode n1, IASTNode n2, ExpressionContext context)
            {
                if ( n1 instanceof  TermNode && n2 instanceof  TermNode) {
                    final TermNode child1 = (TermNode) n1;
                    final TermNode child2 = (TermNode) n2;
                    if ( isNumeric( child1 , context ) && isNumeric( child2 , context ) ) {
                        return super.inferType( n1,n2,context );
                    }
                    // special case: <string> + <string>
                    if ( isCompatibleToStringValue( child1 , context ) && isCompatibleToStringValue( child2 , context ) ) {
                        return TermType.STRING_LITERAL;
                    }
                }
                return super.inferType(n1, n2, context);    //To change body of overridden methods use File | Settings | File Templates.
            }
        },
        MINUS('-', 1) {
            @Override
            public TermNode evaluate(OperatorNode operatorNode,ExpressionContext context)
            {
                TermNode value1 = ((TermNode) operatorNode.child(0)).evaluate(context);
                TermNode value2 = ((TermNode) operatorNode.child(1)).evaluate(context);

                final TermType inferredType = inferType(value1,value2, context);

                double result = getDoubleValue( (TermNode) operatorNode.child(0) , context ) - getDoubleValue( (TermNode) operatorNode.child(1) , context );
                return numberNode(result,inferredType);
            }

            @Override
            public TermNode reduce(OperatorNode operatorNode, ExpressionContext context)
            {
                TermNode value1 = ((TermNode) operatorNode.child(0)).evaluate(context);
                TermNode value2 = ((TermNode) operatorNode.child(1)).evaluate(context);

                if ( isNumeric(value1,context) && isNumeric( value2 , context ) )
                {
                    return numberNode( ((NumberNode) value1).value - ((NumberNode) value2).value , inferType( value1 , value2 , context ));
                }
                return operatorNode;
            }
        },
        TIMES('*', 2) {
            @Override
            public TermNode evaluate(OperatorNode operatorNode,ExpressionContext context)
            {
                TermNode value1 = ((TermNode) operatorNode.child(0)).evaluate(context);
                TermNode value2 = ((TermNode) operatorNode.child(1)).evaluate(context);

                double value = getDoubleValue( value1.evaluate(context) , context ) * getDoubleValue( value2.evaluate(context)  , context );
                final TermType inferredType = inferType( value1 , value2 , context );
                return numberNode(value,inferredType);
            }

            @Override
            public TermNode reduce(OperatorNode operatorNode, ExpressionContext context)
            {
                TermNode value1 = ((TermNode) operatorNode.child(0)).evaluate(context);
                TermNode value2 = ((TermNode) operatorNode.child(1)).evaluate(context);

                if ( isNumeric(value1,context) && isNumeric( value2 , context ) )
                {
                    return new NumberNode( ((NumberNode) value1).value * ((NumberNode) value2).value  , inferType( value1 , value2 , context ));
                }
                return operatorNode;
            }
        },
        DIVIDE('/', 2) {
            @Override
            public TermNode evaluate(OperatorNode operatorNode,ExpressionContext context)
            {
                TermNode value1 = ((TermNode) operatorNode.child(0)).evaluate(context);
                TermNode value2 = ((TermNode) operatorNode.child(1)).evaluate(context);
                double result = getDoubleValue( value1 , context ) / getDoubleValue(value2,context);
                final TermType inferredType = inferType(value1, value2, context);
                return numberNode( result , inferredType );
            }

            @Override
            public TermNode reduce(OperatorNode operatorNode, ExpressionContext context)
            {
                TermNode value1 = ((TermNode) operatorNode.child(0)).evaluate(context);
                TermNode value2 = ((TermNode) operatorNode.child(1)).evaluate(context);

                if ( isNumeric( value1 , context ) && isNumeric( value2 , context ) )
                {
                    double result = getDoubleValue(value1,context) / getDoubleValue(value2,context);
                    TermType inferredType = inferType( value1,value2,context);
                    return numberNode( result , inferredType );
                }
                return operatorNode;
            }

            @Override
            public TermType inferType(IASTNode n1, IASTNode n2, ExpressionContext context)
            {
                if ( n1 instanceof  TermNode && n2 instanceof  TermNode)
                {
                    final TermType t1 = ((TermNode) n1).getType( context );
                    final TermType t2 = ((TermNode) n2).getType( context );

                    if ( t1.isNumeric() && t2.isNumeric() ) {
                        // <int> / <int> => int
                        if ( t1.isInteger() && t2.isInteger() ) {
                            return TermType.INTEGER;
                        }
                        return TermType.FLOATING_POINT;
                    }
                }
                return TermType.UNKNOWN;
            }
        },
        PARENS('(', 100) {
            @Override
            public boolean isLeftAssociative() {
                return false;
            }

            @Override
            public int getArgumentCount() {
                return 1;
            }

            @Override
            public TermNode evaluate(OperatorNode operatorNode,ExpressionContext context) {
                return ((TermNode) operatorNode.child(0)).evaluate(context);
            }

            public String toString(OperatorNode node)
            {
                if ( ! node.hasChildren() ) {
                    return "( ? )";
                }
                if (node.getChildren().size() == 1) {
                    return "( " + node.child(0).toString() + " )";
                }
                return "( "+node.child(0)+" ) [ ... more children? ... ]";
            }

            @Override
            public TermNode reduce(OperatorNode operatorNode, ExpressionContext context) {
                return operatorNode;
            }
        };

        private final char symbol;
        private final int precedence;

        private Operator(char symbol, int precedence) {
            this.symbol = symbol;
            this.precedence = precedence;
        }

        public char getSymbol() {
            return symbol;
        }

        private static NumberNode numberNode(double value,TermType type) {
            return new NumberNode(value,type);
        }

        private static boolean isNumeric(TermNode n,ExpressionContext context)
        {
            return ((TermNode) n).getType( context ).isNumeric();
        }

        private static double getDoubleValue(TermNode n,ExpressionContext context)
        {
            if ( !isNumeric(n, context) ) {
                throw new IllegalArgumentException("Not a TermNode implementation: "+n);
            }
            final TermNode value = ((TermNode) n).evaluate(context);
            if ( !(value instanceof NumberNode ) ) {
                throw new IllegalArgumentException("Not a numberic expression: "+n);
            }
            return ((NumberNode) value).value;
        }

        private static boolean isCompatibleToStringValue(TermNode n,ExpressionContext context)
        {
            final TermNode value = ((TermNode) n).evaluate( context );
            return ( value instanceof StringNode || value instanceof NumberNode);
        }

        private static String getStringValue(IASTNode n,ExpressionContext context)
        {
            final TermNode value = ((TermNode) n).evaluate( context );
            if ( value instanceof StringNode) {
                return ((StringNode) value).value;
            } else if ( value instanceof NumberNode) {

                if ( ((NumberNode) value).hadDecimalPoint ) {
                    return Double.toString( ((NumberNode) value).value );
                }
                return Long.toString((long) ((NumberNode) value).value);
            }
            throw new IllegalArgumentException("Neither a string nor numberic literal: "+n);
        }

        /**
         * Infer type.
         *
         * @param n1 node1 , may be <code>null</code>
         * @param n2 node2 , may be <code>null</code>
         * @param context
         * @return
         */
        public TermType inferType(IASTNode n1,IASTNode n2,ExpressionContext context)
        {
            TermType t1 = null;
            if ( n1 instanceof TermNode) {
                t1 = ((TermNode) n1).getType( context );
            }
            TermType t2 = null;
            if ( n2 instanceof TermNode) {
                t2 = ((TermNode) n2).getType( context );
            }
            if ( n1 == null || n2 == null ) {
                return TermType.UNKNOWN;
            }
            if ( t1 == t2 ) {
                return t1;
            }
            if ( t1 == TermType.FLOATING_POINT && t2 == TermType.INTEGER ) {
                return t1;
            }
            if ( t2 == TermType.FLOATING_POINT && t1 == TermType.INTEGER ) {
                return t2;
            }
            throw new RuntimeException("Internal error,failed to infer types for operator " + name() + " and " + t1 + " , " + t2);
        }

        public String toString(OperatorNode node)
        {
            if ( ! node.hasChildren() ) {
                return "? " +symbol+" ?";
            }
            if ( node.getChildren().size() == 1 ) {
                return node.child(0).toString()+symbol+" ?";
            }
            if ( node.getChildren().size() == 2 ) {
                return node.child(0).toString()+ symbol + node.child(1).toString();
            }
            return node.child(0).toString() + symbol + node.child(1).toString() + " [ ... more children? ... ]";
        }

        public boolean isLeftAssociative() {
            return true;
        }

        public int getPrecedence() {
            return precedence;
        }

        public static Operator fromToken(ParsedToken tok)
        {
            switch(tok.value ) {
                case "(":
                    return Operator.PARENS;
                case "+":
                    return Operator.PLUS;
                case "-":
                    return Operator.MINUS;
                case "*":
                    return Operator.TIMES;
                case "/":
                    return Operator.DIVIDE;
                default:
                    throw new IllegalArgumentException("Unhandled operator: "+tok);
            }
        }

        public int getArgumentCount() {
            return 2;
        }

        public abstract TermNode evaluate(OperatorNode operatorNode,ExpressionContext context);

        public abstract TermNode reduce(OperatorNode operatorNode, ExpressionContext context);
    }

    @Override
    public IASTNode parse(ParseContext context)
    {
        final  boolean oldState = context.isSkipWhitespace();

        context.setSkipWhitespace( false );
        try {
            while( ! context.eof() )
            {
                final ParsedToken tok = context.peek();
                if ( tok.hasType(ParsedTokenType.EOL ) ) {
                    break;
                }
                if ( tok.isWhitespace() ) {
                    context.next();
                    continue;
                }
                switch(tok.type )
                {
                    case QUOTE:
                        pushValue( new StringNode().parse( context ) );
                        break;
                    case IDENTIFIER:
                        pushValue( new IdentifierNode().parse(context ) );
                        break;
                    case NUMBER:
                        pushValue(new NumberNode().parse(context));
                        break;
                    case PARENS_OPEN:
                    case PARENS_CLOSE:
                    case OPERATOR:
                        pushOperator(context.next(),context);
                        break;
                    default:
                        context.fail("Unhandled token: "+tok);
                }
            }
        } finally {
            context.setSkipWhitespace( oldState );
        }

        while ( ! operators.isEmpty() ) {
            popOperator(context);
        }

        if ( values.size() > 1 ) {
            context.fail("Dangling values in expression ?");
        } else if ( values.size() == 1 ) {
            addChild( values.get(0) );
        }
        return this;
    }

    private void clearStacks() {
    }

    private void pushValue(IASTNode node)
    {
        if (node == null) {
            throw new IllegalArgumentException("node must not be null");
        }
        System.out.println("VALUE: "+node);
        values.push( node );
    }

    private void pushOperator(ParsedToken tok,ParseContext context)
    {
        System.out.println("OP: "+tok);
        if ( tok.type == ParsedTokenType.PARENS_CLOSE )
        {
            if ( operators.isEmpty() )
            {
                context.fail("Misplaced closing parens");
            }

            /*
    If the token is a right parenthesis:

        Until the token at the top of the stack is a left parenthesis, pop operators off the stack onto the output queue.
        Pop the left parenthesis from the stack, but not onto the output queue.
        If the token at the top of the stack is a function token, pop it onto the output queue.
        If the stack runs out without finding a left parenthesis, then there are mismatched parentheses.
             */
            while ( ! operators.isEmpty() )
            {
                if ( operators.peek().hasType( Operator.PARENS ) ) {
                    break;
                }
                popOperator(context);
            }
            operators.pop(); // pop opening parens
            return;
        }

        final Operator o1 = Operator.fromToken(tok);

        if (operators.isEmpty())
        {
            operators.push( new OperatorNode(o1,tok) );
            return;
        }

        if ( o1 == Operator.PARENS ) {
            operators.push(new OperatorNode(o1,tok) );
            return;
        }

        /*
        - while there is an operator token, o2, at the top of the stack, and

                either o1 is left-associative and its precedence is equal to that of o2,
                or o1 has precedence less than that of o2,

            pop o2 off the stack, onto the output queue;

        push o1 onto the stack.
         */
        while (!operators.isEmpty())
        {
            Operator o2 = operators.peek().type;
            if ( (o1.isLeftAssociative() && (o1.getPrecedence() == o2.getPrecedence())) ||
                    ( o1.getPrecedence() < o2.getPrecedence() && o2 != Operator.PARENS ) )
            {
                popOperator(context);
            } else {
                break;
            }
        }
        operators.push( new OperatorNode(o1,tok) );
    }

    private void popOperator(ParseContext context)
    {
        if ( operators.isEmpty() ) {
            throw new IllegalStateException("Empty operator stack?");
        }

        if ( operators.peek().hasType( Operator.PARENS ) ) {
            throw new IllegalStateException("Parens still on operator stack?");
        }
        final OperatorNode newNode = operators.pop();
        for ( int i = 0 ; i < newNode.type.getArgumentCount() ; i++ ) {
            if ( values.isEmpty() ) {
                context.fail("Too few arguments for operator "+newNode.type);
            }
            newNode.addChild( values.pop() );
        }
        newNode.reverseChildren();
        pushValue(newNode);
    }

    @Override
    public String toDebugString()
    {
        return "Expression "+getTextRegion();
    }

    @Override
    public TermType getType(ExpressionContext context)
    {
        if ( ! hasChildren() ) {
            return TermType.VOID;
        }
        IASTNode reduced = reduce( context );
        if ( reduced != this && reduced instanceof TermNode ) {
            return ((TermNode) reduced).getType( context );
        }
        return TermType.UNKNOWN;
    }

    @Override
    public boolean isLiteralValue() {
        return false;
    }

    @Override
    protected ExpressionNode cloneThisNodeOnly() {
        return new ExpressionNode();
    }
}