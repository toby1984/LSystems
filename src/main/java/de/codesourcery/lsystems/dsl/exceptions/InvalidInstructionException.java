package de.codesourcery.lsystems.dsl.exceptions;

import de.codesourcery.lsystems.dsl.nodes.ASTNode;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class InvalidInstructionException extends RuntimeException {

    private final ASTNode offendingInstruction;

    public InvalidInstructionException(String msg, ASTNode offendingInstruction) {
        super(msg);
        this.offendingInstruction = offendingInstruction;
    }

    public ASTNode getOffendingInstruction() {
        return offendingInstruction;
    }
}
