package de.codesourcery.lsystems.dsl.execution;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.codesourcery.lsystems.dsl.exceptions.InvalidInstructionException;
import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;
import de.codesourcery.lsystems.dsl.nodes.AST;
import de.codesourcery.lsystems.dsl.nodes.ASTNode;
import de.codesourcery.lsystems.dsl.nodes.ExpressionContext;
import de.codesourcery.lsystems.dsl.nodes.ExpressionNode;
import de.codesourcery.lsystems.dsl.nodes.IdentifierNode;
import de.codesourcery.lsystems.dsl.nodes.NumberNode;
import de.codesourcery.lsystems.dsl.nodes.OperatorNode;
import de.codesourcery.lsystems.dsl.nodes.Statement;
import de.codesourcery.lsystems.dsl.nodes.StringNode;
import de.codesourcery.lsystems.dsl.nodes.TermNode;
import de.codesourcery.lsystems.dsl.nodes.TermNode.TermType;
import de.codesourcery.lsystems.dsl.symbols.Identifier;
import de.codesourcery.lsystems.dsl.symbols.Scope;
import de.codesourcery.lsystems.dsl.symbols.Symbol;
import de.codesourcery.lsystems.dsl.symbols.Symbol.SymbolType;
import de.codesourcery.lsystems.dsl.symbols.SymbolTable;
import de.codesourcery.lsystems.dsl.symbols.VariableSymbol;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class LSystemInterpreter {

    private static final boolean LOG_DEBUG = true;

    private AST ast;

    private Iterator<ASTNode> instructionPointer;
    private ASTNode currentInstruction;

    private final Variables variables = new Variables();

    public ExpressionContext getCurrentContext() {
        return variables;
    }

    public Map<Symbol,MyObject> getVariables()
    {
    	return variables.variables;
    }

    public String getStringValue(Identifier identifier) 
    {
        final MyObject term = variables.get(identifier);
        if ( ! term.isPrimitive() ) {
        	throw new RuntimeException("Identifier '"+identifier+"' maps to non-primitive value "+term);
        }
        switch( term.getType() ) 
        {
        	case STRING_LITERAL:
        		return (String) term.getPrimitiveValue();        		
        	default:
        		throw new RuntimeException("Don't know how to convert '"+identifier+"' with type "+term.getType()+" to string value");
        }
    }
    
    public int getIntValue(Identifier identifier) 
    {
        final MyObject term = variables.get(identifier);
        if ( ! term.isPrimitive() ) {
        	throw new RuntimeException("Identifier '"+identifier+"' maps to non-primitive value "+term);
        }
        switch( term.getType() ) 
        {
        	case FLOATING_POINT:
        	case INTEGER:
        		return ((Number) term.getPrimitiveValue()).intValue();
        	default:
        		throw new RuntimeException("Don't know how to convert '"+identifier+"' with type "+term.getType()+" to int value");
        }
    }

    protected static final class Variables implements ExpressionContext
    {
        public final Map<Symbol,MyObject> variables = new HashMap<>();

        public void clear() {
            variables.clear();
        }

        public void set(Symbol identifier,MyObject value)
        {
            if (identifier == null) {
                throw new IllegalArgumentException("identifier must not be null");
            }
            if (value== null) {
                throw new IllegalArgumentException("node must not be null");
            }
            variables.put(identifier,value);
        }

        public MyObject get(Identifier identifier)
        {
            if (identifier == null) {
                throw new IllegalArgumentException("identifier must not be null");
            }
            final MyObject value = variables.get(identifier);
            if ( value == null ) {
                throw new UnknownIdentifierException( "Variable '"+identifier+"' is not defined" , identifier );
            }
            return variables.get(identifier);
        }

        public void discard(Identifier identifier) {
            variables.remove(identifier);
        }

        public boolean isDefined(Identifier identifier) {
            return variables.containsKey( identifier );
        }

        @Override
        public ASTNode lookup(Identifier identifier, Scope scope, boolean searchParentScopes) throws UnknownIdentifierException 
        {
        	Symbol symbol = scope.getSymbolTable().getSymbol( identifier , searchParentScopes );
        	if ( symbol.hasType( SymbolType.VARIABLE ) ) {
        		// return symbol.
        	}
            throw new RuntimeException("Internal error, don't know how to handle "+symbol);
        }
    }

    public void setAST(AST ast)
    {
        if (ast == null) {
            throw new IllegalArgumentException("AST must not be NULL");
        }
        reset();

        new ASTValidator().validate( ast ).assertNoErrors();

        this.ast = ast;
        this.instructionPointer = ast.getChildren().iterator();
        this.currentInstruction = nextInstruction();
    }

    public LSystemInterpreter()
    {
    }

    private ASTNode nextInstruction() {
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

    public ASTNode getCurrentInstruction() {
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

    protected void execute(ASTNode insn)
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
            final TermNode rhs = ((TermNode) operatorNode.child(1)).reduce( variables );
            if ( ! rhs.isLiteralValue() ) {
            	throw new InvalidInstructionException("Failed to evalue "+rhs,operatorNode.child(1));
            }
            final TermType type = rhs.getType( variables );
            final Object value;
            switch(type) 
            {
            	case FLOATING_POINT:
            		value = (double) ((NumberNode) rhs).value;
            		break;
            	case INTEGER:
            		value = (long) ((NumberNode) rhs).value;
            		break;
            	case STRING_LITERAL:
            		value = ((StringNode) rhs).value;
            		break;
            	default:
                	throw new RuntimeException("Failed to extract value from "+rhs);
            }
            
            final IdentifierNode identifierNode = (IdentifierNode) operatorNode.child(0);
			final Identifier var = identifierNode.value;
            debug("setting variable "+var+" to "+rhs+" ( "+rhs.getType( variables ) );
            
            final Scope scope = identifierNode.getDefinitionScope();
			final SymbolTable symbolTable = scope.getSymbolTable();
            
			boolean updateRequired = false;
            VariableSymbol symbol = null;
            if ( ! symbolTable.hasSymbol( var , true ) ) 
            {
            	// setup new symbol
            	updateRequired = true;
            } else {
            	// symbol already exists , type check expression against current type
            	Symbol tmp = symbolTable.getSymbol( var );
            	if ( ! (tmp instanceof VariableSymbol) ) {
            		throw new RuntimeException("Internal error,not a variable: "+var);
            	}
            	symbol = (VariableSymbol) tmp;
            	if ( symbol.getVariableType() == TermType.UNKNOWN ) {
                	updateRequired = true;
            	} else if ( symbol.getVariableType() != type ) {
            		throw new RuntimeException("Internal error,existing variable '"+symbol.getAbsoluteName()+"' has type "+symbol.getVariableType()+" while new type is "+type);
            	}
            }
            
            if ( updateRequired ) {
            	symbol = new VariableSymbol(var , scope , operatorNode , type );
            	symbolTable.addOrUpdateSymbol( symbol );
            }
            
            if ( variables.isDefined( symbol.getName() ) ) {
            	MyObject existing = variables.get( symbol.getName() );
            	if ( ! existing.isPrimitive() ) {
            		throw new RuntimeException("Internal error, not a primitive object "+existing);
            	}
            	existing.setPrimitiveValue( value );
            } else {
            	MyObject obj = new MyObject(type,true ); 
            	variables.set( symbol , obj );
            }
            return;
        }
        if ( ! isDereferenceOperator( operatorNode ) ) {
            throw new InvalidInstructionException( "Invalid LHS of assignment: "+operatorNode , operatorNode );
        }

        // TODO: Handle a.b = c case
        throw new RuntimeException("Dereference operator not implemented yet");
    }

    protected boolean isVariableIdentifier(ASTNode node) {
        return node instanceof IdentifierNode;
    }

    protected OperatorNode toOperator(ASTNode node)
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

    protected boolean isDereferenceOperator(ASTNode node)
    {
        if ( node instanceof OperatorNode ) {
            final OperatorNode op = (OperatorNode) node;
            if ( op.hasType(ExpressionNode.Operator.DEREFERENCE ) ) {
                return true;
            }
        }
        return false;
    }

    protected boolean isAssignment(ASTNode someNode)
    {
        ASTNode node = someNode;
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