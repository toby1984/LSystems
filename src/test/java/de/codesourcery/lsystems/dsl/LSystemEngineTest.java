package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.nodes.AST;
import junit.framework.TestCase;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class LSystemEngineTest extends TestCase {

    public void testIntegerAssignment() {

        String dsl="a=3";
        final AST ast = new Parser().parse( dsl );
        final LSystemEngine engine = new LSystemEngine();
        engine.setAST( ast );
        System.out.println("Executed " + engine.run() + " instructions");

        final int value = engine.getIntValue(new Identifier("a"));
        assertEquals(3, value);
    }

    public void testStringAssignment() {

        String dsl="a=\"3\"";
        final AST ast = new Parser().parse( dsl );
        final LSystemEngine engine = new LSystemEngine();
        engine.setAST(ast);
        System.out.println("Executed " + engine.run() + " instructions");

        final String value = engine.getStringValue(new Identifier("a"));
        assertEquals("3", value);
    }
}
