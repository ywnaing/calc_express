package dev.yewintnaing.expreval.exception;

/**
 * Thrown when an expression has invalid syntax.
 */
public class InvalidExpressionException extends ExpressionException {
    public InvalidExpressionException(String message) {
        super(message);
    }
}
