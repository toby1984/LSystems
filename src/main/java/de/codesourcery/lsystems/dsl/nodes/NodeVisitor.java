package de.codesourcery.lsystems.dsl.nodes;

public interface NodeVisitor {

	public interface IterationContext 
	{
		public void stop();
		public void dontGoDeeper();
	}
	
	public void visit(ASTNode node, IterationContext context);
}
