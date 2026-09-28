package dev.yewintnaing.expreval;

import dev.yewintnaing.expreval.core.CalculationResult;
import dev.yewintnaing.expreval.core.ExpressionEvaluator;
import dev.yewintnaing.expreval.core.RpnExpressionEvaluator;
import dev.yewintnaing.expreval.exception.InvalidExpressionException;
import dev.yewintnaing.expreval.expression.ExpressionParser;
import dev.yewintnaing.expreval.expression.ExpressionValidator;
import dev.yewintnaing.expreval.function.FunctionRegistry;
import dev.yewintnaing.expreval.function.MathFunction;
import dev.yewintnaing.expreval.storage.InMemoryOperandStorage;
import dev.yewintnaing.expreval.storage.MapOperandStorage;
import dev.yewintnaing.expreval.storage.OperandStorage;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The primary entry point for the ExprEval library.
 * This class provides a thread-safe way to evaluate mathematical expressions with
 * configurable storage, evaluation engines, function registries, and security limits.
 *
 * <p>Example usage:</p>
 * <pre>
 * ExpressionCalculator calculator = new ExpressionCalculator.Builder()
 *         .autoSave(true)
 *         .build();
 * CalculationResult result = calculator.calculate("1 + 2", "sum");
 * </pre>
 */
public final class ExpressionCalculator {

    private final boolean autoSave;
    private final ExpressionEvaluator evaluator;
    private final OperandStorage storage;
    private final CalculatorConfig config;
    private final FunctionRegistry functionRegistry;

    private ExpressionCalculator(boolean autoSave, ExpressionEvaluator evaluator, OperandStorage storage,
            CalculatorConfig config, FunctionRegistry functionRegistry) {
        this.autoSave = autoSave;
        this.evaluator = evaluator;
        this.storage = storage;
        this.config = config;
        this.functionRegistry = functionRegistry;
    }

    /**
     * Pre-compiles an expression for high-performance, repeated evaluation.
     *
     * @param expression The mathematical expression to compile
     * @return A {@link CompiledExpression} instance ready for evaluation
     * @throws InvalidExpressionException if the expression violates validation rules or token limits
     */
    public CompiledExpression compile(String expression) {
        ExpressionValidator.validate(expression, config);
        List<String> postfix = ExpressionParser.infixToPostfix(expression, functionRegistry);

        if (postfix.size() > config.maxTokenCount()) {
            throw new InvalidExpressionException(
                    "Expression exceeds maximum token count of " + config.maxTokenCount());
        }

        return new DefaultCompiledExpression(expression, postfix, evaluator, storage, autoSave);
    }

    /**
     * Executes the calculation of the given expression using the default result key ("Result").
     *
     * @param expression The mathematical expression to evaluate
     * @return The resulting {@link CalculationResult}
     */
    public CalculationResult calculate(String expression) {
        return calculate(expression, "Result");
    }

    /**
     * Executes the calculation of the given expression with a specified result key.
     *
     * @param expression The mathematical expression to evaluate
     * @param resultKey  The key to use for the result operand
     * @return The resulting {@link CalculationResult}
     */
    public CalculationResult calculate(String expression, String resultKey) {
        return compile(expression).evaluate(storage, resultKey);
    }

    /**
     * Evaluates an expression against a transient map of variables using default result key ("Result").
     *
     * @param expression The mathematical expression to evaluate
     * @param variables  Map of variable names to their values
     * @return The resulting {@link CalculationResult}
     */
    public CalculationResult calculateWith(String expression, Map<String, BigDecimal> variables) {
        return calculateWith(expression, variables, "Result");
    }

    /**
     * Evaluates an expression against a transient map of variables with a specified result key.
     *
     * @param expression The mathematical expression to evaluate
     * @param variables  Map of variable names to their values
     * @param resultKey  The key to use for the result operand
     * @return The resulting {@link CalculationResult}
     */
    public CalculationResult calculateWith(String expression, Map<String, BigDecimal> variables, String resultKey) {
        OperandStorage mapStorage = new MapOperandStorage(variables, storage);
        return compile(expression).evaluate(mapStorage, resultKey);
    }

    /**
     * Gets the operand storage associated with this calculator.
     *
     * @return the {@link OperandStorage} instance
     */
    public OperandStorage getStorage() {
        return storage;
    }

    /**
     * Gets the function registry associated with this calculator.
     *
     * @return the {@link FunctionRegistry} instance
     */
    public FunctionRegistry getFunctionRegistry() {
        return functionRegistry;
    }

    /**
     * Builder for configuring and creating instances of {@link ExpressionCalculator}.
     */
    public static class Builder {
        private boolean autoSave = false;
        private ExpressionEvaluator evaluator;
        private OperandStorage storage = new InMemoryOperandStorage();
        private CalculatorConfig config = CalculatorConfig.DEFAULT;
        private FunctionRegistry functionRegistry = FunctionRegistry.createDefault();

        /**
         * Sets whether calculated results should be automatically saved to storage.
         *
         * @param autoSave true to auto-save, false otherwise
         * @return this builder
         */
        public Builder autoSave(boolean autoSave) {
            this.autoSave = autoSave;
            return this;
        }

        /**
         * Sets a custom evaluation engine.
         *
         * @param evaluator the ExpressionEvaluator to use
         * @return this builder
         */
        public Builder evaluator(ExpressionEvaluator evaluator) {
            this.evaluator = Objects.requireNonNull(evaluator, "Evaluator cannot be null");
            return this;
        }

        /**
         * Sets the operand storage implementation.
         *
         * @param storage the OperandStorage to use
         * @return this builder
         */
        public Builder storage(OperandStorage storage) {
            this.storage = Objects.requireNonNull(storage, "Storage cannot be null");
            return this;
        }

        /**
         * Sets the configuration options and security limits.
         *
         * @param config the CalculatorConfig to use
         * @return this builder
         */
        public Builder config(CalculatorConfig config) {
            this.config = Objects.requireNonNull(config, "Config cannot be null");
            return this;
        }

        /**
         * Sets a custom function registry.
         *
         * @param functionRegistry the FunctionRegistry to use
         * @return this builder
         */
        public Builder functionRegistry(FunctionRegistry functionRegistry) {
            this.functionRegistry = Objects.requireNonNull(functionRegistry, "FunctionRegistry cannot be null");
            return this;
        }

        /**
         * Registers a custom mathematical function.
         *
         * @param function the MathFunction to register
         * @return this builder
         */
        public Builder registerFunction(MathFunction function) {
            Objects.requireNonNull(function, "Function cannot be null");
            this.functionRegistry.register(function);
            return this;
        }

        /**
         * Builds a new {@link ExpressionCalculator} instance with the configured settings.
         *
         * @return a new ExpressionCalculator
         */
        public ExpressionCalculator build() {
            ExpressionEvaluator eval = this.evaluator != null
                    ? this.evaluator
                    : new RpnExpressionEvaluator(4, java.math.RoundingMode.HALF_EVEN, this.functionRegistry);
            return new ExpressionCalculator(autoSave, eval, storage, config, functionRegistry);
        }
    }
}
