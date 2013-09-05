package de.codesourcery.lsystems.dsl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public enum ParsedTokenType
{
    /*
     * Symbols and literal values.
     */
    IDENTIFIER,
    NUMBER, // integer/floating point literal
    DOT, // .
    COLON, // :
    OPERATOR, // + - * /
    ASSIGNMENT, // =
    UNPARSED, // anything that did not match any other token
    PARENS_OPEN, // (
    PARENS_CLOSE, // )
    ARROW, // ->
    /*
     * Keywords.
     */
    SET, // AXIOM
    MAP,  // map
    RULE, // rule
    /*
     * Whitespace
     */
    EOL, // end-of-line , either '\r\n' or '\n'
    WHITESPACE; // any other whitespace characters except EOL    
}
