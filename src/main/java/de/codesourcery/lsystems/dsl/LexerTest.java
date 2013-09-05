package de.codesourcery.lsystems.dsl;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;
import de.codesourcery.lsystems.dsl.nodes.AST;
import de.codesourcery.lsystems.dsl.nodes.ASTNode;
import de.codesourcery.lsystems.dsl.nodes.ExpressionContext;
import de.codesourcery.lsystems.dsl.nodes.NumberNode;
import de.codesourcery.lsystems.dsl.nodes.TermNode;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class LexerTest {

    private String initialExpression = "1.5*2";
    private ExpressionContext context;

    private AST currentAST;
    
    // UI widgets
    final JTextArea expressionInput = new JTextArea( initialExpression );
    final JTextArea errorMessages = new JTextArea();
    final JTree tree=new JTree();
    
    public static void main(String[] args) {
        new LexerTest().run(args);
    }

    private DefaultTreeModel parse(String expression)
    {
    	boolean success = false;
    	try 
    	{
	        final AST ast = new Parser().parse( expression );
	        currentAST = ast;
	        success = true;
	        return new DefaultTreeModel(new NodeWrapper( ast ) );
    	} finally {
	    	if ( ! success ) {
	    		currentAST = new AST();
	    	}
    	}
    }

    public LexerTest() {
    }
    
    private void error(String message,Throwable t) 
    {
    	errorMessages.setText( "ERROR: "+message );
    	if ( t != null ) {
    		t.printStackTrace();
    	}
    }
    
    private void clearErrors() {
    	errorMessages.setText(null);
    }

    private void parseUserInput() 
    {
        try 
        {
            tree.setModel(parse(expressionInput.getText()));
            clearErrors();
        }
        catch (Exception ex) 
        {
        	error( ex.getMessage() , ex );
            ex.printStackTrace();
	        tree.setModel( new DefaultTreeModel(new NodeWrapper( currentAST ) ) );
        }    	
    }
    public void run(String[] args)
    {
        final Map<Identifier,Double> variables = new HashMap<>();
        variables.put(new Identifier("a"), 4.0d );

        context = new ExpressionContext()
        {
            @Override
            public ASTNode lookup(Identifier identifier) throws UnknownIdentifierException
            {
                final Double value = variables.get(identifier);
                if ( value == null ) {
                    throw new UnknownIdentifierException(identifier);
                }
                return new NumberNode( value , TermNode.TermType.FLOATING_POINT );
            }
        };

        final JFrame frame = new JFrame();

        errorMessages.setBorder( BorderFactory.createTitledBorder("Errors" ) );
        tree.setModel( parse( initialExpression ) );
        tree.setRootVisible(true);

        final  JPanel panel = new JPanel();
        panel.setLayout( new GridBagLayout() );

        // add text area to enter expressions
        expressionInput.setText( initialExpression );
        expressionInput.setColumns(25);
        expressionInput.setRows(5);
        expressionInput.setBorder( BorderFactory.createTitledBorder("Input" ) );

        GridBagConstraints cnstrs = new GridBagConstraints();
        cnstrs.weightx = 0.9;
        cnstrs.weighty = 0;
        cnstrs.fill = GridBagConstraints.HORIZONTAL;
        cnstrs.gridwidth=1;
        cnstrs.gridheight=2;
        cnstrs.gridx = 0;
        cnstrs.gridy = 0;
        panel.add( expressionInput , cnstrs );

        // add parse button
        final JButton parseButton = new JButton("Parse");
        parseButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
            	parseUserInput();
            }
        });
        cnstrs = new GridBagConstraints();
        cnstrs.weightx = 0.1;
        cnstrs.weighty = 0;
        cnstrs.fill = GridBagConstraints.HORIZONTAL;
        cnstrs.gridwidth=1;
        cnstrs.gridheight=1;
        cnstrs.gridx = 1;
        cnstrs.gridy = 0;
        panel.add( parseButton , cnstrs );

        // add reduce button
        final JButton reduceButton = new JButton("Reduce");
        reduceButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    parseUserInput();

                    if ( currentAST != null & currentAST.hasChildren() && currentAST.child(0) instanceof TermNode) {
                        final ASTNode reduced = ((TermNode) currentAST.child(0)).reduce(context);
                        System.out.println("REDUCED: "+reduced);
                        if ( reduced instanceof TermNode) {
                            System.out.println("TYPE: "+((TermNode) reduced).getType(context));
                        }
                        tree.setModel(new DefaultTreeModel(new NodeWrapper(reduced)));
                    } else {
                        System.out.println("Nothing to reduce");
                    }
                    errorMessages.setText( "" );
                }
                catch (Exception ex) {
                	errorMessages.setText("ERROR: "+ex.getMessage() );                	
                    ex.printStackTrace();
                }
            }
        });
        cnstrs = new GridBagConstraints();
        cnstrs.weightx = 0.1;
        cnstrs.weighty = 0;
        cnstrs.fill = GridBagConstraints.HORIZONTAL;
        cnstrs.gridwidth=1;
        cnstrs.gridheight=1;
        cnstrs.gridx = 1;
        cnstrs.gridy = 1;
        panel.add( reduceButton , cnstrs );
        
        // add text area to enter expressions
        errorMessages.setColumns(25);
        errorMessages.setRows(5);
        errorMessages.setEditable( false );

        cnstrs = new GridBagConstraints();
        cnstrs.weightx = 1;
        cnstrs.weighty = 1;
        cnstrs.fill = GridBagConstraints.HORIZONTAL;
        cnstrs.gridwidth=2;
        cnstrs.gridheight=1;
        cnstrs.gridx = 0;
        cnstrs.gridy = 2;
        panel.add( errorMessages , cnstrs );              

        // add parse tree view
        final  JScrollPane pane=new JScrollPane( tree );
        pane.setPreferredSize(new Dimension(200,200));

        cnstrs = new GridBagConstraints();
        cnstrs.weightx = 1;
        cnstrs.weighty = 1;
        cnstrs.fill = GridBagConstraints.BOTH;
        cnstrs.gridwidth=2;
        cnstrs.gridheight=1;
        cnstrs.gridx = 0;
        cnstrs.gridy = 3;
        panel.add( pane , cnstrs );
        
        // setup frame
        frame.getContentPane().add( panel );

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible( true );
    }

    protected static final class NodeWrapper implements TreeNode {

        private final ASTNode node;

        public NodeWrapper(ASTNode node) {
            if ( node == null ) {
                throw new IllegalArgumentException("Node cannot be null");
            }
            this.node = node;
        }

        @Override
        public TreeNode getChildAt(int childIndex) {
            return new NodeWrapper( node.child( childIndex ) );
        }

        @Override
        public int getChildCount() {
            return node.getChildren().size();
        }

        @Override
        public String toString() {
            return node.toDebugString();
        }

        @Override
        public TreeNode getParent() {
            return node.hasParent() ? new NodeWrapper( node.getParent() ) : null;
        }

        @Override
        public int getIndex(TreeNode node) {
            return this.node.getChildren().indexOf(((NodeWrapper) node).node);
        }

        @Override
        public boolean getAllowsChildren() {
            return true;
        }

        @Override
        public boolean isLeaf() {
            return ! node.hasChildren();
        }

        @Override
        public Enumeration children()
        {
            final Iterator<ASTNode> it = node.getChildren().iterator();

            return new Enumeration<Object>(){

                @Override
                public boolean hasMoreElements() {
                    return it.hasNext();
                }

                @Override
                public Object nextElement() {
                    return it.next();
                }
            };
        }
    }
}