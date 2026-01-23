package dev.yewintnaing.expreval.core;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents the outcome of an expression calculation.
 * Contains the final value, metadata about the original expression, and
 * execution time.
 *
 * @param expression The original mathematical expression string.
 * @param operand    The resulting operand (key and value).
 * @param durationMs The time taken to evaluate the expression in milliseconds.
 */
public record CalculationResult(
        String expression,
        Operand operand,
        long durationMs) {
    public CalculationResult {
        Objects.requireNonNull(expression);
        Objects.requireNonNull(operand);
    }

    public BigDecimal value() {
        return operand.value();
    }

    public String key() {
        return operand.key();
    }
}
