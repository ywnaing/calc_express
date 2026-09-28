package dev.yewintnaing.expreval.function;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * Represents a mathematical function that can be evaluated within an expression.
 * Functions can have fixed arity (e.g. unary, binary, ternary) or variable arity (varargs).
 */
public interface MathFunction {

    /**
     * Returns the case-insensitive name of the function (e.g., "sqrt", "min").
     *
     * @return the function name
     */
    String getName();

    /**
     * Returns the minimum number of arguments required by this function.
     *
     * @return minimum argument count
     */
    int getMinArgs();

    /**
     * Returns the maximum number of arguments accepted by this function.
     * Return Integer.MAX_VALUE for variadic functions.
     *
     * @return maximum argument count
     */
    int getMaxArgs();

    /**
     * Executes the function with the given arguments.
     *
     * @param arguments the list of numerical arguments
     * @param mathContext the math context for calculations
     * @return the calculated BigDecimal value
     */
    BigDecimal execute(List<BigDecimal> arguments, MathContext mathContext);

    /**
     * Checks if the given number of arguments is valid for this function.
     *
     * @param argCount the number of arguments
     * @return true if valid, false otherwise
     */
    default boolean acceptsArgCount(int argCount) {
        return argCount >= getMinArgs() && argCount <= getMaxArgs();
    }

    /**
     * Factory method for creating a 1-argument unary function.
     *
     * @param name function name
     * @param func unary operation
     * @return MathFunction instance
     */
    static MathFunction unary(String name, Function<BigDecimal, BigDecimal> func) {
        Objects.requireNonNull(name, "Function name cannot be null");
        Objects.requireNonNull(func, "Function implementation cannot be null");
        return new MathFunction() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getMinArgs() {
                return 1;
            }

            @Override
            public int getMaxArgs() {
                return 1;
            }

            @Override
            public BigDecimal execute(List<BigDecimal> arguments, MathContext mathContext) {
                if (arguments.size() != 1) {
                    throw new IllegalArgumentException("Function " + name + " expects 1 argument, but got "
                            + arguments.size());
                }
                return func.apply(arguments.get(0));
            }
        };
    }

    /**
     * Factory method for creating a 2-argument binary function.
     *
     * @param name function name
     * @param op binary operation
     * @return MathFunction instance
     */
    static MathFunction binary(String name, BinaryOperator<BigDecimal> op) {
        Objects.requireNonNull(name, "Function name cannot be null");
        Objects.requireNonNull(op, "Binary operator cannot be null");
        return new MathFunction() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getMinArgs() {
                return 2;
            }

            @Override
            public int getMaxArgs() {
                return 2;
            }

            @Override
            public BigDecimal execute(List<BigDecimal> arguments, MathContext mathContext) {
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Function " + name + " expects 2 arguments, but got "
                            + arguments.size());
                }
                return op.apply(arguments.get(0), arguments.get(1));
            }
        };
    }

    /**
     * Factory method for creating a variadic function accepting at least minArgs.
     *
     * @param name function name
     * @param minArgs minimum arguments required
     * @param handler function handler
     * @return MathFunction instance
     */
    static MathFunction variadic(String name, int minArgs, Function<List<BigDecimal>, BigDecimal> handler) {
        Objects.requireNonNull(name, "Function name cannot be null");
        Objects.requireNonNull(handler, "Function handler cannot be null");
        return new MathFunction() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getMinArgs() {
                return minArgs;
            }

            @Override
            public int getMaxArgs() {
                return Integer.MAX_VALUE;
            }

            @Override
            public BigDecimal execute(List<BigDecimal> arguments, MathContext mathContext) {
                if (arguments.size() < minArgs) {
                    throw new IllegalArgumentException("Function " + name + " expects at least " + minArgs
                            + " arguments, but got " + arguments.size());
                }
                return handler.apply(arguments);
            }
        };
    }

    /**
     * Factory method for creating a 0-argument nullary function.
     *
     * @param name function name
     * @param supplier supplier providing the value
     * @return MathFunction instance
     */
    static MathFunction nullary(String name, java.util.function.Supplier<BigDecimal> supplier) {
        Objects.requireNonNull(name, "Function name cannot be null");
        Objects.requireNonNull(supplier, "Supplier cannot be null");
        return new MathFunction() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getMinArgs() {
                return 0;
            }

            @Override
            public int getMaxArgs() {
                return 0;
            }

            @Override
            public BigDecimal execute(List<BigDecimal> arguments, MathContext mathContext) {
                if (!arguments.isEmpty()) {
                    throw new IllegalArgumentException("Function " + name + " expects 0 arguments, but got "
                            + arguments.size());
                }
                return supplier.get();
            }
        };
    }
}
