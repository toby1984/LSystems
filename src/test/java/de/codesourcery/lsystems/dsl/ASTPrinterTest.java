package de.codesourcery.lsystems.dsl;

import de.codesourcery.lsystems.dsl.nodes.AST;
import junit.framework.TestCase;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class ASTPrinterTest extends TestCase {

    public void testPrint() {

        String in = "3 * 2";
        AST ast = new Parser().parse(in);
        String out = new ASTPrinter().print( ast );
        assertEquals( in , out );
    }
}
