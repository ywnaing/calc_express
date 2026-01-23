package dev.yewintnaing.expreval.core;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a numerical operand in the system.
 * This is an immutable record that associates a unique key with a value.
 *
 * @param key   The unique identifier for this operand (e.g. "x",
 *              "PRICE_TOTAL").
 *              Must not be null.
 * @param value The numerical value associated with the key.
 *              Must not be null.
 */
public record Operand(
        String key,
        BigDecimal value) {
    public Operand {
        Objects.requireNonNull(key, "Operand key cannot be null");
        Objects.requireNonNull(value, "Operand value cannot be null");
    }
}
