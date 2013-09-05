package de.codesourcery.lsystems.dsl.nodes;

import org.omg.CORBA.UNKNOWN;

/**
 *
 * @author Tobias.Gierke@code-sourcery.de
 */
public interface TermNode extends IASTNode
{
    public static enum TermType
    {
        INTEGER {
            @Override
            public boolean isNumeric() {
                return true;
            }
            public boolean isInteger() {
                return true;
            }
        },
        FLOATING_POINT
        {
            @Override
            public boolean isNumeric() {
                return true;
            }
        },
        STRING_LITERAL,
        VOID,
        UNKNOWN;

        public boolean isNumeric() {
            return false;
        }

        public boolean isInteger() {
            return false;
        }
    }

    TermNode evaluate(ExpressionContext context);

    TermNode reduce(ExpressionContext context);

    public TermType getType(ExpressionContext context);

    public boolean isLiteralValue();
}
