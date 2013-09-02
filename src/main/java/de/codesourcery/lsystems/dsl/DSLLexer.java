package de.codesourcery.lsystems.dsl;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import de.codesourcery.lsystems.dsl.exceptions.UnknownIdentifierException;
import de.codesourcery.lsystems.dsl.nodes.*;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class DSLLexer {

    private final Scanner scanner;

    private final List<ParsedToken> tokens = new ArrayList<>();

    private final StringBuffer buffer = new StringBuffer();

    public DSLLexer(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean eof() {
        if (tokens.isEmpty()) {
            parseTokens();
        }
        return tokens.isEmpty();
    }

    public ParsedToken peek() {
        if ( eof() ) {
            throw new IllegalStateException("Premature end of input");
        }
        return tokens.get(0);
    }

    public ParsedToken next() {
        if ( eof() ) {
            throw new IllegalStateException("Premature end of input");
        }
        return tokens.remove(0);
    }

    private void parseTokens() {

        if (scanner.eof()) {
            return;
        }

        while (!scanner.eof() && Character.isWhitespace(scanner.peek())) {
            scanner.next();
        }

        if (scanner.eof()) {
            return;
        }

        buffer.setLength(0);
        int offset = scanner.currentOffset();
        char c = scanner.peek();
        while (!scanner.eof()) {
            c = scanner.peek();
            switch (c) {
                case '(':
                    addUnparsed(offset);
                    tokens.add(new ParsedToken(ParsedTokenType.PARENS_OPEN, scanner.next(), scanner.currentOffset()));
                    return;
                case ')':
                    addUnparsed(offset);
                    tokens.add(new ParsedToken(ParsedTokenType.PARENS_CLOSE, scanner.next(), scanner.currentOffset()));
                    return;
                case '-':
                    scanner.next();
                    if ( scanner.peek() == '>' ) { // found '->'
                        tokens.add(new ParsedToken(ParsedTokenType.ARROW, "->", scanner.currentOffset()-1 ) );
                        return;
                    }
                    scanner.pushBack();
                case '.':
                    addUnparsed(offset);
                    tokens.add(new ParsedToken(ParsedTokenType.DOT, scanner.next(), scanner.currentOffset()));
                    return;
            }

            if (OperatorNode.isValidOperator(c)) {
                addUnparsed(offset);
                tokens.add(new ParsedToken(ParsedTokenType.OPERATOR, scanner.next(), scanner.currentOffset()));
                return;
            }

            if (Character.isDigit(c)) {
                addUnparsed(offset);
                offset = scanner.currentOffset();
                buffer.append(scanner.next());
                while (!scanner.eof() && NumberNode.isValidNumber(buffer.toString() + scanner.peek())) {
                    buffer.append(scanner.next());
                }
                tokens.add(new ParsedToken(ParsedTokenType.NUMBER, buffer.toString(), offset));
                return;
            }
            buffer.append(scanner.next());
        }
        addUnparsed(offset);
    }

    private void addUnparsed(int offset)
    {
        if (buffer.length() > 0)
        {
            final String s = buffer.toString();
            if (Identifier.isValidIdentifier(s)) {
                tokens.add(new ParsedToken(ParsedTokenType.IDENTIFIER, s, offset));
            } else {
                tokens.add(new ParsedToken(ParsedTokenType.UNPARSED, s, offset));
            }
            buffer.setLength(0);
        }
    }

    public static void main(String[] args)
    {
        final String expression = "1.5*2";
        final AST ast = new Parser().parse( expression );

        final Map<Identifier,Double> variables = new HashMap<>();
        variables.put(new Identifier("a"), 4.0d );

        final ExpressionContext context = new ExpressionContext()
        {
            @Override
            public ASTNode lookup(Identifier identifier) throws UnknownIdentifierException
            {
                final Double value = variables.get(identifier);
                if ( value == null ) {
                    throw new UnknownIdentifierException(identifier);
                }
                return new NumberNode( value );
            }
        };

        final ExpressionNode expr = (ExpressionNode) ast.child(0);

        final JFrame frame = new JFrame();

        final JTree tree=new JTree(new NodeWrapper( expr.reduce( context ) ) );
        tree.setRootVisible(true);

        final  JPanel panel = new JPanel();
        panel.setLayout( new GridBagLayout() );

        // add text area
        final JTextArea textArea = new JTextArea( expression );
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
                    AST ast = new Parser().parse(textArea.getText());
                    tree.setModel( new DefaultTreeModel(new NodeWrapper( ast ) ) );
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
                    if ( ast.hasChildren() && ast.child(0) instanceof TermNode) {
                        final ASTNode reduced = ((TermNode) ast.child(0)).reduce(context);
                        System.out.println("REDUCED: "+reduced);
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

    public ParsedToken next(ParsedTokenType type) {
        if ( ! peek(type ) ) {
            throw new RuntimeException("Expected "+type+" but got "+peek());
        }
        return next();
    }

    public boolean peek(ParsedTokenType type)
    {
        return peek().type == type;
    }
}