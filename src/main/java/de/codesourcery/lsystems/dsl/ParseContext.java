package de.codesourcery.lsystems.dsl;

/**
 * Created with IntelliJ IDEA.
 * User: tobi
 * Date: 9/1/13
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ParseContext {

    boolean eof();

    ParsedToken peek();

    ParsedToken next();

    void fail(String msg) throws RuntimeException;

    ParsedToken next(ParsedTokenType number) throws RuntimeException;

    boolean peek(ParsedTokenType type);
}
