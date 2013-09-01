package de.codesourcery.lsystems.dsl;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 6:31 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ParsedTokenType {
    IDENTIFIER,
    MAP,  // map
    RULE, // rule
    NUMBER, // integer literal
    DOT, // .
    OPERATOR,
    UNPARSED,
    PARENS_OPEN,
    PARENS_CLOSE,
    AXIOM
}
