package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.exceptions.InvalidInstructionException;
import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;
import de.codesourcery.lsystems.dsl.nodes.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class LSystemEngine {

    private static final boolean LOG_DEBUG = true;

    private AST ast;

    private Iterator<IASTNode> instructionPointer;
    private IASTNode currentInstruction;

    private final Variables variables = new Variables();

    public static final class Variable
    {
        public final Identifier name;
        public final Object value;

        public Variable(Identifier name, Object value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return name+" = "+value;
        }
    }

    public ExpressionContext getCurrentContext() {
        return variables;
    }

    public Map<Identifier,Variable> getVariables()
    {
        final Map<Identifier,Variable> result = new HashMap<>();
        for ( Map.Entry<Identifier, TermNode> entry : variables.variables.entrySet() )
        {
            final TermNode evaluated = entry.getValue().evaluate(variables);
            final Object value = toJavaValue(evaluated);
            final Identifier varName = entry.getKey();
            final Variable variable = new Variable(varName, value);
            result.put(varName , variable);
        }
        return result;
    }

    private Object toJavaValue(TermNode node) {
        if ( ! node.isLiteralValue() )
        {
            throw new InvalidInstructionException("Does not evaluate to a literal value: "+node,node);
        }
        if ( node instanceof StringNode) {
            return ((StringNode) node).value;
        }

        if ( node instanceof NumberNode) {
            NumberNode n = (NumberNode) node;
            switch(n.getType( variables ) ) {
                case FLOATING_POINT:
                    return n.value;
                case INTEGER:
                    return (int) n.value;
                default:
                    // fall-through
            }
        }
        throw new RuntimeException("Internal error,unhandled literal value "+node);
    }

    public String getStringValue(Identifier identifier)
    {
        final TermNode term = variables.get(identifier);
        if ( ! term.isLiteralValue() )
        {
            throw new InvalidInstructionException("Failed to evaluate value for variable '"+identifier+"'" , term);
        }
        return ExpressionNode.Operator.getStringValue( term , variables );
    }

    public int getIntValue(Identifier identifier) {
        final TermNode term = variables.get(identifier);
        if ( ! term.isLiteralValue() )
        {
            throw new InvalidInstructionException("Failed to evaluate value for variable '"+identifier+"'" , term);
        }
        return ExpressionNode.Operator.getIntValue(term, variables);
    }

    protected static final class Variables implements ExpressionContext
    {
        public final Map<Identifier,TermNode> variables = new HashMap<>();

        public void clear() {
            variables.clear();
        }

        public void set(Identifier identifier,TermNode node)
        {
            if (identifier == null) {
                throw new IllegalArgumentException("identifier must not be null");
            }
            if (node == null) {
                throw new IllegalArgumentException("node must not be null");
            }
            variables.put(identifier,node);
        }

        public TermNode get(Identifier identifier)
        {
            if (identifier == null) {
                throw new IllegalArgumentException("identifier must not be null");
            }
            final TermNode value = variables.get(identifier);
            if ( value == null ) {
                throw new UnknownIdentifierException( "Variable '"+identifier+"' is not defined" , identifier );
            }
            return variables.get(identifier);
        }

        public void unset(Identifier identifier) {
            variables.remove(identifier);
        }

        public boolean isDefined(Identifier identifier) {
            return variables.containsKey( identifier );
        }

        @Override
        public IASTNode lookup(Identifier identifier) throws UnknownIdentifierException {
            return get(identifier);
        }
    }

    public void setAST(AST ast)
    {
        if (ast == null) {
            throw new IllegalArgumentException("AST must not be NULL");
        }
        reset();

        new ASTValidator().validate( ast , variables ).assertNoErrors();

        this.ast = ast;
        this.instructionPointer = ast.getChildren().iterator();
        this.currentInstruction = nextInstruction();
    }

    public LSystemEngine()
    {
    }

    private IASTNode nextInstruction() {
        return this.instructionPointer.hasNext() ? this.instructionPointer.next() : null;
    }

    private void reset() {
        debug("reset()");
        if ( ast != null ) {
            this.instructionPointer = ast.getChildren().iterator();
            this.currentInstruction = nextInstruction();
        } else {
            this.currentInstruction = null;
        }
    }

    public IASTNode getCurrentInstruction() {
        return currentInstruction;
    }

    public int run() {
        reset();
        int executed = 0;
        while ( currentInstruction != null ) {
            executeOneInstruction();
            executed++;
        }
        return executed;
    }

    private void executeOneInstruction()
    {
        boolean success = false;
        try {
            execute(currentInstruction);
            success = true;
        }
        finally
        {
            if ( success )
            {
                currentInstruction = nextInstruction();
            }
        }
    }

    protected void execute(IASTNode insn)
    {
        debug("executing "+insn);
        if ( isAssignment( insn ) )
        {
            executeAssignment(toOperator(insn));
        } else {
            debug("Skipping unimplemented instruction: "+insn);
        }
    }

    private void executeAssignment(OperatorNode operatorNode)
    {
        debug("executing assignment "+operatorNode);
        if ( isVariableIdentifier( operatorNode.child(0) ) )
        {
            final TermNode rhs = (TermNode) operatorNode.child(1);
            final Identifier var = ((IdentifierNode) operatorNode.child(0)).value;
            debug("setting variable "+var+" to "+rhs+" ( "+rhs.getType( variables ) );
            variables.set(var, rhs );
            return;
        }
        if ( ! isDereferenceOperator( operatorNode ) ) {
            throw new InvalidInstructionException( "Invalid LHS of assignment: "+operatorNode , operatorNode );
        }

        // TODO: Handle a.b = c case
        throw new RuntimeException("Dereference operator not implemented yet");
    }

    public TermNode getValue(Identifier identifier) {
        return variables.get( identifier );
    }

    protected boolean isVariableIdentifier(IASTNode node) {
        return node instanceof IdentifierNode;
    }

    protected OperatorNode toOperator(IASTNode node)
    {
        if ( node instanceof Statement && node.child(0) instanceof  ExpressionNode ) {
            final ExpressionNode expr = (ExpressionNode) node.child(0);
            return (OperatorNode) expr.child(0);
        }
        if ( node instanceof  ExpressionNode ) {
            final ExpressionNode expr = (ExpressionNode) node;
            return (OperatorNode) expr.child(0);
        }
        if ( node instanceof OperatorNode) {
            return (OperatorNode) node;
        }
        throw new IllegalArgumentException("Don't know how to convert " + node + " to OperatorNode");
    }

    protected boolean isDereferenceOperator(IASTNode node)
    {
        if ( node instanceof OperatorNode ) {
            final OperatorNode op = (OperatorNode) node;
            if ( op.hasType(ExpressionNode.Operator.DEREFERENCE ) ) {
                return true;
            }
        }
        return false;
    }

    protected boolean isAssignment(IASTNode someNode)
    {
        IASTNode node = someNode;
        OperatorNode op = null;
        if ( node instanceof Statement && node.hasChildren() && node.child(0) instanceof ExpressionNode ) {
            node = node.child(0);
        }
        if ( node instanceof ExpressionNode)
        {
            final ExpressionNode expr = (ExpressionNode) node;
            if ( expr.hasChildren() && expr.child(0) instanceof OperatorNode) {
                node = expr.child(0);
            }
        }

        if ( node instanceof OperatorNode ) {
            op = (OperatorNode) node;
        }
        return op != null && op.hasType( ExpressionNode.Operator.ASSIGNMENT );
    }

    private void debug(String message)
    {
        if ( LOG_DEBUG ) {
            System.out.println(message);
        }
    }
}