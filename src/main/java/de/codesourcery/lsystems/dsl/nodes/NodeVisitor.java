package de.codesourcery.lsystems.dsl.nodes;

public interface NodeVisitor {

	public interface IterationContext
	{
		public void stop();
		public void dontGoDeeper();

        // part of INTERNAL API, DO NOT USE
        public boolean isStop();
        public boolean isDontGoDeeper();
        public void reset();
	}
	
	public void visit(ASTNode node, IterationContext context);
}
