package de.codesourcery.lsystems.dsl.nodes;

import de.codesourcery.lsystems.dsl.ParseContext;
import de.codesourcery.lsystems.dsl.ParsedToken;
import de.codesourcery.lsystems.dsl.ParsedTokenType;

import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExpressionNode extends ASTNode implements  TermNode
{
    private final Stack<ASTNode> values = new Stack<>();

    private final Stack<Operator> operators = new Stack<>();

    @Override
    public double evaluate(ExpressionContext context)
    {
        if ( ! hasChildren() ) {
            return 0;
        }
        return ((TermNode) child(0)).evaluate(context);
    }

    @Override
    public ASTNode reduce(ExpressionContext context) {
        return hasChildren() ? ((TermNode) child(0)).reduce(context) : this;
    }

    public static enum Operator {
        PLUS('+', 1) {
            @Override
            public double evaluate(OperatorNode operatorNode,ExpressionContext context) {
                return ((TermNode) operatorNode.child(0)).evaluate(context) + ((TermNode) operatorNode.child(1)).evaluate(context);
            }

            @Override
            public ASTNode reduce(OperatorNode operatorNode, ExpressionContext context)
            {
                ASTNode value1 = ((TermNode) operatorNode.child(0)).reduce(context);
                ASTNode value2 = ((TermNode) operatorNode.child(1)).reduce(context);

                if ( value1 instanceof NumberNode && value2 instanceof NumberNode) {
                    return new NumberNode( ((NumberNode) value1).value + ((NumberNode) value2).value );
                }
                return operatorNode;
            }
        },
        MINUS('-', 1) {
            @Override
            public double evaluate(OperatorNode operatorNode,ExpressionContext context) {
                return ((TermNode) operatorNode.child(0)).evaluate(context) - ((TermNode) operatorNode.child(1)).evaluate(context);
            }

            @Override
            public ASTNode reduce(OperatorNode operatorNode, ExpressionContext context)
            {
                ASTNode value1 = ((TermNode) operatorNode.child(0)).reduce(context);
                ASTNode value2 = ((TermNode) operatorNode.child(1)).reduce(context);

                if ( value1 instanceof NumberNode && value2 instanceof NumberNode) {
                    return new NumberNode( ((NumberNode) value1).value - ((NumberNode) value2).value );
                }
                return operatorNode;
            }
        },
        TIMES('*', 2) {
            @Override
            public double evaluate(OperatorNode operatorNode,ExpressionContext context) {
                return ((TermNode) operatorNode.child(0)).evaluate(context) * ((TermNode) operatorNode.child(1)).evaluate(context);
            }

            @Override
            public ASTNode reduce(OperatorNode operatorNode, ExpressionContext context)
            {
                ASTNode value1 = ((TermNode) operatorNode.child(0)).reduce(context);
                ASTNode value2 = ((TermNode) operatorNode.child(1)).reduce(context);

                if ( value1 instanceof NumberNode && value2 instanceof NumberNode) {
                    return new NumberNode( ((NumberNode) value1).value * ((NumberNode) value2).value );
                }
                return operatorNode;
            }
        },
        DIVIDE('/', 2) {
            @Override
            public double evaluate(OperatorNode operatorNode,ExpressionContext context) {
                return ((TermNode) operatorNode.child(0)).evaluate(context) / ((TermNode) operatorNode.child(1)).evaluate(context);
            }

            @Override
            public ASTNode reduce(OperatorNode operatorNode, ExpressionContext context)
            {
                ASTNode value1 = ((TermNode) operatorNode.child(0)).reduce(context);
                ASTNode value2 = ((TermNode) operatorNode.child(1)).reduce(context);

                if ( value1 instanceof NumberNode && value2 instanceof NumberNode) {
                    return new NumberNode( ((NumberNode) value1).value / ((NumberNode) value2).value );
                }
                return operatorNode;
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
            public double evaluate(OperatorNode operatorNode,ExpressionContext context) {
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
            public ASTNode reduce(OperatorNode operatorNode, ExpressionContext context) {
                throw new UnsupportedOperationException("cannot reduce parens");
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

        public abstract double evaluate(OperatorNode operatorNode,ExpressionContext context);

        public abstract ASTNode reduce(OperatorNode operatorNode, ExpressionContext context);
    }

    @Override
    public ASTNode parse(ParseContext context)
    {
        while( ! context.eof() )
        {
            final ParsedToken tok = context.peek();
            switch(tok.type )
            {
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

    private void pushValue(ASTNode node)
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
            while ( ! operators.isEmpty() ) {
                Operator top = operators.peek();
                if ( top == Operator.PARENS ) {
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
            operators.push(o1);
            return;
        }

        if ( o1 == Operator.PARENS ) {
            operators.push(o1);
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
            Operator o2 = operators.peek();
            if ( (o1.isLeftAssociative() && (o1.getPrecedence() == o2.getPrecedence())) ||
                 ( o1.getPrecedence() < o2.getPrecedence() && o2 != Operator.PARENS ) )
            {
                popOperator(context);
            } else {
                break;
            }
        }
        operators.push(o1);
    }

    private void popOperator(ParseContext context)
    {
        if ( operators.isEmpty() ) {
            throw new IllegalStateException("Empty operator stack?");
        }

        if ( operators.peek() == Operator.PARENS ) {
            throw new IllegalStateException("Parens still on operator stack?");
        }
        final Operator op = operators.pop();
        OperatorNode newNode = new OperatorNode(op);
        for ( int i = 0 ; i < op.getArgumentCount() ; i++ ) {
            if ( values.isEmpty() ) {
                context.fail("Too few arguments for operator "+op);
            }
            newNode.addChild( values.pop() );
        }
        newNode.reverseChildren();
        pushValue(newNode);
    }

    @Override
    public String toDebugString()
    {
        return "Expression";
    }
}