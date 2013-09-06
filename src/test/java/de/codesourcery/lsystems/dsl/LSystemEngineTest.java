package de.codesourcery.lsystems.dsl;

import junit.framework.TestCase;
import de.codesourcery.lsystems.dsl.execution.LSystemInterpreter;
import de.codesourcery.lsystems.dsl.nodes.AST;
import de.codesourcery.lsystems.dsl.parsing.Parser;
import de.codesourcery.lsystems.dsl.symbols.Identifier;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class LSystemEngineTest extends TestCase {

    public void testIntegerAssignment() {

        String dsl="a=3";
        final AST ast = new Parser().parse( dsl );
        final LSystemInterpreter engine = new LSystemInterpreter();
        engine.setAST( ast );
        System.out.println("Executed " + engine.run() + " instructions");

        final int value = engine.getIntValue(new Identifier("a"));
        assertEquals(3, value);
    }

    public void testStringAssignment() {

        String dsl="a=\"3\"";
        final AST ast = new Parser().parse( dsl );
        final LSystemInterpreter engine = new LSystemInterpreter();
        engine.setAST(ast);
        System.out.println("Executed " + engine.run() + " instructions");

        final String value = engine.getStringValue(new Identifier("a"));
        assertEquals("3", value);
    }
}
