package dev.yewintnaing.expreval.exception;

/**
 * Base exception for all ExprEval related errors.
 */
public class ExpressionException extends RuntimeException {

    /**
     * Constructs an ExpressionException with a detail message.
     *
     * @param message the detail error message
     */
    public ExpressionException(String message) {
        super(message);
    }

    /**
     * Constructs an ExpressionException with a detail message and cause.
     *
     * @param message the detail error message
     * @param cause   the underlying cause
     */
    public ExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
