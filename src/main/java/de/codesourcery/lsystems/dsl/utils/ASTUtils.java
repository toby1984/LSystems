package de.codesourcery.lsystems.dsl.utils;

import java.util.HashMap;
import java.util.Map;

import de.codesourcery.lsystems.dsl.nodes.AST;
import de.codesourcery.lsystems.dsl.nodes.ASTNode;
import de.codesourcery.lsystems.dsl.nodes.ExpressionContext;
import de.codesourcery.lsystems.dsl.nodes.ExpressionNode;
import de.codesourcery.lsystems.dsl.nodes.NodeVisitor;
import de.codesourcery.lsystems.dsl.nodes.ScopeDefinition;
import de.codesourcery.lsystems.dsl.nodes.TermNode;
import de.codesourcery.lsystems.dsl.symbols.Identifier;
import de.codesourcery.lsystems.dsl.symbols.Scope;
import de.codesourcery.lsystems.dsl.symbols.Symbol;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class ASTUtils {

    public void reduce(AST ast,final ExpressionContext exprContext) {

        ast.visitPostOrder( new NodeVisitor() {
            @Override
            public void visit(ASTNode node, IterationContext context)
            {
                if ( node instanceof ExpressionNode) {

                    TermNode expr = (ExpressionNode) node;
                    final TermNode reduced = expr.reduce(exprContext);
                    System.out.println("Reduced "+expr+" to "+reduced);
                    node.replaceWith( reduced );
                }
            }
        });
    }
    
    public Map<Identifier,Symbol> getAllSymbols(ASTNode ast) 
    {
    	final Map<Identifier,Symbol> result = new HashMap<>();
    	
        ast.visitPostOrder( new NodeVisitor() {
            @Override
            public void visit(ASTNode node, IterationContext context)
            {
                if ( node instanceof ScopeDefinition) 
                {
                	final Scope scope = ((ScopeDefinition) node).getScope();
                	final Identifier scopeName = scope.getAbsoluteName();
                	for ( Map.Entry<Identifier, Symbol> entry: scope.getSymbolTable().getSymbols().entrySet() ) 
                	{
                		final Identifier fqName = scopeName.append( entry.getKey() );
                		result.put( fqName , entry.getValue() );
                	}
                }
            }
        });    	
    	return result;
    }
}
