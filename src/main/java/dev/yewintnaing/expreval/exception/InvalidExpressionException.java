package dev.yewintnaing.expreval.exception;

/**
 * Thrown when an expression has invalid syntax.
 */
public class InvalidExpressionException extends ExpressionException {

    /**
     * Constructs an InvalidExpressionException with the given detail message.
     *
     * @param message the detail error message
     */
    public InvalidExpressionException(String message) {
        super(message);
    }
}
