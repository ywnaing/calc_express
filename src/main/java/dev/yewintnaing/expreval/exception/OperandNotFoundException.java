package dev.yewintnaing.expreval.exception;

/**
 * Thrown when an operand is not found in the storage.
 */
public class OperandNotFoundException extends ExpressionException {

    /**
     * Constructs an OperandNotFoundException for the missing key.
     *
     * @param key the missing operand key
     */
    public OperandNotFoundException(String key) {
        super("Operand with key '" + key + "' not found in storage");
    }
}
