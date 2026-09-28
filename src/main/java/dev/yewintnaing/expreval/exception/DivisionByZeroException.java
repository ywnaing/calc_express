package dev.yewintnaing.expreval.exception;

/**
 * Thrown when division by zero is attempted.
 */
public class DivisionByZeroException extends ExpressionException {

    /**
     * Constructs a new DivisionByZeroException.
     */
    public DivisionByZeroException() {
        super("Division by zero");
    }
}
