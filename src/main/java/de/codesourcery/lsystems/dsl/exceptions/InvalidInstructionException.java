package de.codesourcery.lsystems.dsl.exceptions;

import de.codesourcery.lsystems.dsl.nodes.IASTNode;

/**
 * @author Tobias.Gierke@code-sourcery.de
 */
public class InvalidInstructionException extends RuntimeException {

    private final IASTNode offendingInstruction;

    public InvalidInstructionException(String msg, IASTNode offendingInstruction) {
        super(msg);
        this.offendingInstruction = offendingInstruction;
    }

    public IASTNode getOffendingInstruction() {
        return offendingInstruction;
    }
}
