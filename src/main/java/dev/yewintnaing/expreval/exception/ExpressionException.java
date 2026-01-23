package dev.yewintnaing.expreval.exception;

/**
 * Base exception for all ExprEval related errors.
 */
public class ExpressionException extends RuntimeException {
    public ExpressionException(String message) {
        super(message);
    }

    public ExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
