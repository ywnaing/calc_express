package dev.yewintnaing.expreval.function;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

/**
 * Provides standard mathematical functions and handles registering built-in functions
 * into the {@link FunctionRegistry}.
 */
public enum StandardMathFunctions {
    /** Square root function. */
    SQRT("sqrt", v -> v.sqrt(MathContext.DECIMAL128)),
    /** Absolute value function. */
    ABS("abs", BigDecimal::abs),
    /** Negation function. */
    NEG("neg", BigDecimal::negate),
    /** Round to integer function. */
    ROUND("round", v -> v.setScale(0, RoundingMode.HALF_UP)),
    /** Floor function. */
    FLOOR("floor", v -> v.setScale(0, RoundingMode.FLOOR)),
    /** Ceiling function. */
    CEIL("ceil", v -> v.setScale(0, RoundingMode.CEILING)),
    /** Power of 10 function. */
    POW10("pow10", v -> BigDecimal.TEN.pow(v.intValue()));

    private static final Set<String> EXTENDED_FUNCTION_NAMES = Set.of(
            "min", "max", "pow", "clamp", "signum", "log", "ln", "exp",
            "sin", "cos", "tan", "asin", "acos", "atan", "atan2", "pi", "e",
            "fact", "npr", "ncr", "deg", "todegrees", "rad", "toradians",
            "sind", "cosd", "tand", "asind", "acosd", "atand",
            "sinh", "cosh", "tanh", "cbrt", "root", "hypot", "log2"
    );

    private final String name;
    private final Function<BigDecimal, BigDecimal> function;

    StandardMathFunctions(String name, Function<BigDecimal, BigDecimal> function) {
        this.name = name;
        this.function = function;
    }

    /**
     * Gets the identifier name of this function.
     *
     * @return the function name
     */
    public String getName() {
        return name;
    }

    /**
     * Evaluates this function on a single BigDecimal operand.
     *
     * @param value the input value
     * @return the calculated result
     */
    public BigDecimal apply(BigDecimal value) {
        return function.apply(value);
    }

    /**
     * Checks if the given name matches any built-in standard function.
     *
     * @param name the function name to check
     * @return true if standard function, false otherwise
     */
    public static boolean isFunction(String name) {
        if (name == null) {
            return false;
        }
        String lower = name.toLowerCase(Locale.ROOT);
        return fromName(lower) != null || EXTENDED_FUNCTION_NAMES.contains(lower);
    }

    /**
     * Finds a legacy enum instance by name.
     *
     * @param name the function name
     * @return matching enum constant, or null if not found
     */
    public static StandardMathFunctions fromName(String name) {
        for (StandardMathFunctions f : values()) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Registers all standard and extended math functions into the given registry.
     *
     * @param registry the function registry to populate
     */
    public static void registerAll(FunctionRegistry registry) {
        registerCore(registry);
        registerTrigonometric(registry);
        registerLogarithmicAndExponential(registry);
        registerCombinatoricsAndRoots(registry);
    }

    private static void registerCore(FunctionRegistry registry) {
        for (StandardMathFunctions sf : values()) {
            if (!sf.getName().equals("round")) {
                registry.register(MathFunction.unary(sf.getName(), sf::apply));
            }
        }

        registry.register(new MathFunction() {
            @Override
            public String getName() {
                return "round";
            }

            @Override
            public int getMinArgs() {
                return 1;
            }

            @Override
            public int getMaxArgs() {
                return 2;
            }

            @Override
            public BigDecimal execute(List<BigDecimal> args, MathContext mathContext) {
                if (args.size() == 1) {
                    return args.get(0).setScale(0, RoundingMode.HALF_UP);
                } else if (args.size() == 2) {
                    return args.get(0).setScale(args.get(1).intValue(), RoundingMode.HALF_UP);
                }
                throw new IllegalArgumentException("round expects 1 or 2 arguments, got " + args.size());
            }
        });

        registry.register(MathFunction.variadic("min", 2,
                args -> args.stream().min(BigDecimal::compareTo).orElseThrow()));
        registry.register(MathFunction.variadic("max", 2,
                args -> args.stream().max(BigDecimal::compareTo).orElseThrow()));

        registry.register(MathFunction.binary("pow", (base, exp) -> {
            try {
                int intExp = exp.intValueExact();
                if (intExp >= 0) {
                    return base.pow(intExp);
                }
            } catch (ArithmeticException ignored) {
                // Non-integer exponent fallback
            }
            return BigDecimal.valueOf(Math.pow(base.doubleValue(), exp.doubleValue()));
        }));

        registry.register(new MathFunction() {
            @Override
            public String getName() {
                return "clamp";
            }

            @Override
            public int getMinArgs() {
                return 3;
            }

            @Override
            public int getMaxArgs() {
                return 3;
            }

            @Override
            public BigDecimal execute(List<BigDecimal> args, MathContext mathContext) {
                BigDecimal val = args.get(0);
                BigDecimal min = args.get(1);
                BigDecimal max = args.get(2);
                if (min.compareTo(max) > 0) {
                    throw new IllegalArgumentException("clamp min (" + min + ") cannot be greater than max ("
                            + max + ")");
                }
                if (val.compareTo(min) < 0) {
                    return min;
                }
                if (val.compareTo(max) > 0) {
                    return max;
                }
                return val;
            }
        });

        registry.register(MathFunction.unary("signum", v -> BigDecimal.valueOf(v.signum())));
        registry.register(MathFunction.nullary("pi", () -> BigDecimal.valueOf(Math.PI)));
        registry.register(MathFunction.nullary("e", () -> BigDecimal.valueOf(Math.E)));
    }

    private static void registerTrigonometric(FunctionRegistry registry) {
        // Radians mode trigonometry
        registry.register(MathFunction.unary("sin", v -> BigDecimal.valueOf(Math.sin(v.doubleValue()))));
        registry.register(MathFunction.unary("cos", v -> BigDecimal.valueOf(Math.cos(v.doubleValue()))));
        registry.register(MathFunction.unary("tan", v -> BigDecimal.valueOf(Math.tan(v.doubleValue()))));
        registry.register(MathFunction.unary("asin", v -> BigDecimal.valueOf(Math.asin(v.doubleValue()))));
        registry.register(MathFunction.unary("acos", v -> BigDecimal.valueOf(Math.acos(v.doubleValue()))));
        registry.register(MathFunction.unary("atan", v -> BigDecimal.valueOf(Math.atan(v.doubleValue()))));
        registry.register(MathFunction.binary("atan2", (y, x) ->
                BigDecimal.valueOf(Math.atan2(y.doubleValue(), x.doubleValue()))));

        // Degrees mode trigonometry
        registry.register(MathFunction.unary("sind", deg ->
                BigDecimal.valueOf(Math.sin(Math.toRadians(deg.doubleValue())))));
        registry.register(MathFunction.unary("cosd", deg ->
                BigDecimal.valueOf(Math.cos(Math.toRadians(deg.doubleValue())))));
        registry.register(MathFunction.unary("tand", deg ->
                BigDecimal.valueOf(Math.tan(Math.toRadians(deg.doubleValue())))));
        registry.register(MathFunction.unary("asind", v ->
                BigDecimal.valueOf(Math.toDegrees(Math.asin(v.doubleValue())))));
        registry.register(MathFunction.unary("acosd", v ->
                BigDecimal.valueOf(Math.toDegrees(Math.acos(v.doubleValue())))));
        registry.register(MathFunction.unary("atand", v ->
                BigDecimal.valueOf(Math.toDegrees(Math.atan(v.doubleValue())))));

        // Angle conversions
        registry.register(MathFunction.unary("deg", rad ->
                BigDecimal.valueOf(Math.toDegrees(rad.doubleValue()))));
        registry.register(MathFunction.unary("todegrees", rad ->
                BigDecimal.valueOf(Math.toDegrees(rad.doubleValue()))));
        registry.register(MathFunction.unary("rad", deg ->
                BigDecimal.valueOf(Math.toRadians(deg.doubleValue()))));
        registry.register(MathFunction.unary("toradians", deg ->
                BigDecimal.valueOf(Math.toRadians(deg.doubleValue()))));

        // Hyperbolic functions
        registry.register(MathFunction.unary("sinh", v -> BigDecimal.valueOf(Math.sinh(v.doubleValue()))));
        registry.register(MathFunction.unary("cosh", v -> BigDecimal.valueOf(Math.cosh(v.doubleValue()))));
        registry.register(MathFunction.unary("tanh", v -> BigDecimal.valueOf(Math.tanh(v.doubleValue()))));
    }

    private static void registerLogarithmicAndExponential(FunctionRegistry registry) {
        registry.register(new MathFunction() {
            @Override
            public String getName() {
                return "log";
            }

            @Override
            public int getMinArgs() {
                return 1;
            }

            @Override
            public int getMaxArgs() {
                return 2;
            }

            @Override
            public BigDecimal execute(List<BigDecimal> args, MathContext mathContext) {
                if (args.size() == 1) {
                    return BigDecimal.valueOf(Math.log10(args.get(0).doubleValue()));
                } else if (args.size() == 2) {
                    BigDecimal base = args.get(0);
                    BigDecimal val = args.get(1);
                    return BigDecimal.valueOf(Math.log(val.doubleValue()) / Math.log(base.doubleValue()));
                }
                throw new IllegalArgumentException("log expects 1 or 2 arguments, got " + args.size());
            }
        });

        registry.register(MathFunction.unary("log2", v ->
                BigDecimal.valueOf(Math.log(v.doubleValue()) / Math.log(2.0))));
        registry.register(MathFunction.unary("ln", v -> BigDecimal.valueOf(Math.log(v.doubleValue()))));
        registry.register(MathFunction.unary("exp", v -> BigDecimal.valueOf(Math.exp(v.doubleValue()))));
    }

    private static void registerCombinatoricsAndRoots(FunctionRegistry registry) {
        registry.register(MathFunction.unary("fact", StandardMathFunctions::factorial));
        registry.register(MathFunction.binary("npr", StandardMathFunctions::permutations));
        registry.register(MathFunction.binary("ncr", StandardMathFunctions::combinations));

        registry.register(MathFunction.unary("cbrt", v -> BigDecimal.valueOf(Math.cbrt(v.doubleValue()))));
        registry.register(MathFunction.binary("root", (x, n) ->
                BigDecimal.valueOf(Math.pow(x.doubleValue(), 1.0 / n.doubleValue()))));
        registry.register(MathFunction.binary("hypot", (a, b) ->
                BigDecimal.valueOf(Math.hypot(a.doubleValue(), b.doubleValue()))));
    }

    private static BigDecimal factorial(BigDecimal n) {
        if (n.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Factorial is not defined for negative numbers: " + n);
        }
        int intVal;
        try {
            intVal = n.intValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Factorial requires an integer, got: " + n, e);
        }
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= intVal; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return new BigDecimal(result);
    }

    private static BigDecimal permutations(BigDecimal n, BigDecimal r) {
        int nVal = n.intValueExact();
        int rVal = r.intValueExact();
        if (nVal < 0 || rVal < 0 || rVal > nVal) {
            throw new IllegalArgumentException("nPr requires 0 <= r <= n, got n=" + nVal + ", r=" + rVal);
        }
        BigInteger result = BigInteger.ONE;
        for (int i = nVal; i > nVal - rVal; i--) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return new BigDecimal(result);
    }

    private static BigDecimal combinations(BigDecimal n, BigDecimal r) {
        int nVal = n.intValueExact();
        int rVal = r.intValueExact();
        if (nVal < 0 || rVal < 0 || rVal > nVal) {
            throw new IllegalArgumentException("nCr requires 0 <= r <= n, got n=" + nVal + ", r=" + rVal);
        }
        if (rVal == 0 || rVal == nVal) {
            return BigDecimal.ONE;
        }
        int k = Math.min(rVal, nVal - rVal);
        BigInteger num = BigInteger.ONE;
        BigInteger den = BigInteger.ONE;
        for (int i = 1; i <= k; i++) {
            num = num.multiply(BigInteger.valueOf(nVal - i + 1));
            den = den.multiply(BigInteger.valueOf(i));
        }
        return new BigDecimal(num.divide(den));
    }
}
