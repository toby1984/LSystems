package de.codesourcery.lsystems.dsl;

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
    OPERATOR, // + - * /
    UNPARSED, // anything that did not match any other token
    PARENS_OPEN, // (
    PARENS_CLOSE, // )
    ARROW, // ->
    /*
     * Keywords.
     */
    AXIOM, // AXIOM
    MAP,  // map
    RULE, // rule
}
