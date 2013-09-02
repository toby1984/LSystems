package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;
import de.codesourcery.lsystems.dsl.nodes.*;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public class LexerTest {

    private String initialExpression = "1.5*2";
    private ExpressionContext context;

    private AST currentAST;

    public static void main(String[] args) {
        new LexerTest().run(args);
    }

    private DefaultTreeModel parse(String expression)
    {
        final AST ast = new Parser().parse( expression );
        currentAST = ast;
        return new DefaultTreeModel(new NodeWrapper( ast ) );
    }

    public LexerTest() {

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

        parse( initialExpression );

        final JFrame frame = new JFrame();

        final JTree tree=new JTree( parse( initialExpression ) );
        tree.setRootVisible(true);

        final  JPanel panel = new JPanel();
        panel.setLayout( new GridBagLayout() );

        // add text area
        final JTextArea textArea = new JTextArea( initialExpression );
        textArea.setColumns(25);
        textArea.setRows(5);

        GridBagConstraints cnstrs = new GridBagConstraints();
        cnstrs.weightx = 0.9;
        cnstrs.weighty = 0;
        cnstrs.fill = GridBagConstraints.HORIZONTAL;
        cnstrs.gridwidth=1;
        cnstrs.gridheight=2;
        cnstrs.gridx = 0;
        cnstrs.gridy = 0;
        panel.add( textArea , cnstrs );

        // add parse button
        final JButton parseButton = new JButton("Parse");
        parseButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try {
                    tree.setModel(parse(textArea.getText()));
                }
                catch (Exception ex) {
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
                    parse( textArea.getText() );

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
                }
                catch (Exception ex) {
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
        cnstrs.gridy = 2;
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