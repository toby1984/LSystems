package de.codesourcery.lsystems.dsl;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import de.codesourcery.lsystems.dsl.execution.LSystemInterpreter;
import de.codesourcery.lsystems.dsl.execution.MyObject;
import de.codesourcery.lsystems.dsl.nodes.AST;
import de.codesourcery.lsystems.dsl.nodes.ASTNode;
import de.codesourcery.lsystems.dsl.parsing.Parser;
import de.codesourcery.lsystems.dsl.symbols.Identifier;
import de.codesourcery.lsystems.dsl.symbols.Symbol;
import de.codesourcery.lsystems.dsl.utils.ASTPrinter;
import de.codesourcery.lsystems.dsl.utils.ASTUtils;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class LSystemsUI 
{
    private String initialExpression = "1.5*2";

    private AST currentAST;

    // UI widgets
    private final JTextArea expressionInput = new JTextArea( initialExpression );
    private final JTextArea errorMessages = new JTextArea();

    private final JList<String> variablesList = new JList<>();
    
    private final JTable symbols = new JTable();

    private final LSystemInterpreter engine = new LSystemInterpreter();

    private final JTextArea prettyPrintedAST = new JTextArea();
    
    final JTree tree=new JTree();
    
    public static void main(String[] args) {
        new LSystemsUI().run(args);
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
    	} 
    	finally {
	    	if ( ! success ) {
	    		currentAST = new AST();
	    	}
            prettyPrintedAST.setText( new ASTPrinter().print( currentAST ) );
            populateSymbolTable( currentAST );
    	}
    }
    
    private void populateSymbolTable(AST ast) 
    {
    	final Map<Identifier, Symbol> allSymbols = new ASTUtils().getAllSymbols( ast );
    	
    	final List<Symbol> list = new ArrayList<>();
    	list.addAll( allSymbols.values() );
    	
    	symbols.setModel( new AbstractTableModel() {
			
    		@Override
    		public Class<?> getColumnClass(int columnIndex) {
    			return String.class;
    		}
    		
    		@Override
    		public String getColumnName(int columnIndex) {
    			switch( columnIndex ) 
				{
					case 0:
						return "Absolute name";
					case 1:
						return "Name";
					case 2:
						return "Type";
					default:
						throw new IllegalArgumentException("Unhandled column: "+columnIndex);
				}
    		}
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) 
			{
				switch( columnIndex ) 
				{
					case 0:
						return list.get(rowIndex).getAbsoluteName().toString();
					case 1:
						return list.get(rowIndex).getName().toString();
					case 2:
						return list.get(rowIndex).type.toString();
					default:
						throw new IllegalArgumentException("Unhandled column: "+columnIndex);
				}
			}
			
			@Override
			public int getRowCount() {
				return list.size();
			}
			
			@Override
			public int getColumnCount() {
				return 3;
			}
		});
    }

    private void execute(AST ast)
    {
        this.engine.setAST( ast );
        engine.run();

        final List<String> vars = new ArrayList<>();
        for ( Entry<Symbol, MyObject> entry : engine.getVariables().entrySet() ) {
        	vars.add( entry.getKey().getAbsoluteName().toString()+" => "+entry.getValue().toString() );
        }

        variablesList.setModel( new AbstractListModel<String>() {

            @Override
            public int getSize()
            {
                return vars.size();
            }

            @Override
            public String getElementAt(int index)
            {
                return vars.get(index);
            }
        });
    }

    public LSystemsUI() {
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
        final JFrame frame = new JFrame();

        errorMessages.setBorder( BorderFactory.createTitledBorder("Errors" ) );
        tree.setModel( parse( initialExpression ) );
        tree.setRootVisible(true);

        final  JPanel panel = new JPanel();
        panel.setLayout( new GridBagLayout() );

        final int rows = 4;
        final double weightY = 1.0d / (double) rows;

        // add text area to enter expressions
        expressionInput.setText( initialExpression );
        expressionInput.setColumns(25);
        expressionInput.setRows(5);
        expressionInput.setBorder( BorderFactory.createTitledBorder("Input" ) );

        GridBagConstraints cnstrs = new GridBagConstraints();
        cnstrs.weightx = 0.7;
        cnstrs.weighty = weightY;
        cnstrs.fill = GridBagConstraints.HORIZONTAL;
        cnstrs.gridwidth=1;
        cnstrs.gridheight=1;
        cnstrs.gridx = 0;
        cnstrs.gridy = 0;
        panel.add( expressionInput , cnstrs );

        // add button panel
        cnstrs = new GridBagConstraints();
        cnstrs.weightx = 0.3;
        cnstrs.weighty = weightY;
        cnstrs.fill = GridBagConstraints.HORIZONTAL;
        cnstrs.gridwidth=1;
        cnstrs.gridheight=1;
        cnstrs.gridx = 1;
        cnstrs.gridy = 0;

        panel.add( createButtonPanel() , cnstrs );

        // add text area to display error messages
        errorMessages.setColumns(25);
        errorMessages.setRows(5);
        errorMessages.setEditable( false );

        cnstrs = new GridBagConstraints();
        cnstrs.weightx = 0.5;
        cnstrs.weighty = weightY;
        cnstrs.fill = GridBagConstraints.HORIZONTAL;
        cnstrs.gridwidth=1;
        cnstrs.gridheight=1;
        cnstrs.gridx = 0;
        cnstrs.gridy = 1;
        panel.add( errorMessages , cnstrs );

        // add JList that shows variables
        cnstrs = new GridBagConstraints();
        cnstrs.weightx = 0.5;
        cnstrs.weighty = weightY;
        cnstrs.fill = GridBagConstraints.HORIZONTAL;
        cnstrs.gridwidth=1;
        cnstrs.gridheight=1;
        cnstrs.gridx = 1;
        cnstrs.gridy = 1;

        final JScrollPane listPane = new JScrollPane(variablesList);
        panel.add(listPane, cnstrs );

        // add parse tree view
        final  JScrollPane pane=new JScrollPane( tree );
        pane.setPreferredSize(new Dimension(200,200));

        cnstrs = new GridBagConstraints();
        cnstrs.weightx = 1;
        cnstrs.weighty = weightY;
        cnstrs.fill = GridBagConstraints.BOTH;
        cnstrs.gridwidth=1;
        cnstrs.gridheight=1;
        cnstrs.gridx = 0;
        cnstrs.gridy = 2;
        panel.add( pane , cnstrs );
        
        // add symbols view
        final  JScrollPane symbolsPane=new JScrollPane( symbols );
        symbolsPane.setPreferredSize(new Dimension(200,200));

        cnstrs = new GridBagConstraints();
        cnstrs.weightx = 1;
        cnstrs.weighty = weightY;
        cnstrs.fill = GridBagConstraints.BOTH;
        cnstrs.gridwidth=1;
        cnstrs.gridheight=1;
        cnstrs.gridx = 1;
        cnstrs.gridy = 2;
        panel.add( symbolsPane , cnstrs );        

        // add pretty-printed AST view
        final  JScrollPane pane2=new JScrollPane( prettyPrintedAST );
        pane2.setPreferredSize(new Dimension(200,200));

        cnstrs = new GridBagConstraints();
        cnstrs.weightx = 1;
        cnstrs.weighty = weightY;
        cnstrs.fill = GridBagConstraints.BOTH;
        cnstrs.gridwidth=2;
        cnstrs.gridheight=1;
        cnstrs.gridx = 0;
        cnstrs.gridy = 3;
        panel.add( pane2  , cnstrs );
        
        // setup frame
        frame.getContentPane().add( panel );

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible( true );
    }

    private JPanel createButtonPanel()
    {
        int y = 0;

        final JPanel panel = new JPanel();
        panel.setLayout( new GridBagLayout() );

        // add parse button
        addButton( panel , "Parse" , y , new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                parseUserInput();
            }
        });
        y++;

        // add reduce button
        addButton( panel , "Reduce" , y , new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                reduceUserInput();
            }
        });
        y++;

        // add execute button
        addButton( panel , "Execute" , y , new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                executeUserInput();
            }
        });
        y++;

        return panel;
    }

    private void executeUserInput() {
        try
        {
            parseUserInput();
            execute( currentAST );
            errorMessages.setText( "" );
        }
        catch (Exception ex) {
            errorMessages.setText("ERROR: "+ex.getMessage() );
            ex.printStackTrace();
        }
    }

    private void reduceUserInput()
    {
        try
        {
            parseUserInput();

            if ( currentAST != null )
            {
                execute( currentAST );
                new ASTUtils().reduce( currentAST , engine.getCurrentContext() );

                System.out.println("REDUCED: "+currentAST);
                tree.setModel(new DefaultTreeModel(new NodeWrapper(currentAST)));
            } else {
                System.out.println("Nothing to reduce");
            }
            errorMessages.setText( "" );
        }
        catch (Exception ex) {
            errorMessages.setText("ERROR: "+ex.getMessage() );
            ex.printStackTrace();
        } finally {
            prettyPrintedAST.setText( new ASTPrinter().print( currentAST ) );
        }
    }

    private void addButton(JPanel panel,String label , int y , ActionListener listener) 
    {
        final JButton button = new JButton( label );
        button.addActionListener(listener);

        final GridBagConstraints cnstrs = new GridBagConstraints();
        cnstrs.weightx = 0.1;
        cnstrs.weighty = 0;
        cnstrs.fill = GridBagConstraints.HORIZONTAL;
        cnstrs.gridwidth=1;
        cnstrs.gridheight=1;
        cnstrs.gridx = 0;
        cnstrs.gridy = y;
        panel.add( button , cnstrs );
    }

    protected static final class NodeWrapper implements TreeNode 
    {
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
        public Enumeration<Object> children()
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