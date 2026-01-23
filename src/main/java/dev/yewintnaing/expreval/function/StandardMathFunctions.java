package dev.yewintnaing.expreval.function;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;

/**
 * Provides standard mathematical functions for the evaluator.
 */
public enum StandardMathFunctions {
    SQRT("sqrt", v -> v.sqrt(MathContext.DECIMAL128)),
    ABS("abs", BigDecimal::abs),
    NEG("neg", BigDecimal::negate),
    ROUND("round", v -> v.setScale(0, RoundingMode.HALF_UP)),
    FLOOR("floor", v -> v.setScale(0, RoundingMode.FLOOR)),
    CEIL("ceil", v -> v.setScale(0, RoundingMode.CEILING)),
    POW10("pow10", v -> BigDecimal.TEN.pow(v.intValue()));

    private final String name;
    private final Function<BigDecimal, BigDecimal> function;

    StandardMathFunctions(String name, Function<BigDecimal, BigDecimal> function) {
        this.name = name;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public BigDecimal apply(BigDecimal value) {
        return function.apply(value);
    }

    public static boolean isFunction(String name) {
        return fromName(name) != null;
    }

    public static StandardMathFunctions fromName(String name) {
        for (StandardMathFunctions f : values()) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }
}
