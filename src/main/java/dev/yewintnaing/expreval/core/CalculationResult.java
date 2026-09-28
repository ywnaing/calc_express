package dev.yewintnaing.expreval.core;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents the outcome of an expression calculation.
 * Contains the final value, metadata about the original expression, and execution time.
 *
 * @param expression The original mathematical expression string.
 * @param operand    The resulting operand (key and value).
 * @param durationMs The time taken to evaluate the expression in milliseconds.
 */
public record CalculationResult(
        String expression,
        Operand operand,
        long durationMs) {

    /**
     * Constructs a validated CalculationResult.
     *
     * @param expression original expression string
     * @param operand    resulting operand
     * @param durationMs evaluation time in milliseconds
     */
    public CalculationResult {
        Objects.requireNonNull(expression, "Expression cannot be null");
        Objects.requireNonNull(operand, "Operand cannot be null");
    }

    /**
     * Retrieves the calculated numerical value.
     *
     * @return the calculated BigDecimal value
     */
    public BigDecimal value() {
        return operand.value();
    }

    /**
     * Retrieves the result key name.
     *
     * @return the key string
     */
    public String key() {
        return operand.key();
    }
}
