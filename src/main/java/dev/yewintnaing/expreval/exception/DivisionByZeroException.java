package dev.yewintnaing.expreval.exception;

/**
 * Thrown when division by zero is attempted.
 */
public class DivisionByZeroException extends ExpressionException {
    public DivisionByZeroException() {
        super("Division by zero");
    }
}
