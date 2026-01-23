package dev.yewintnaing.expreval;

import dev.yewintnaing.expreval.core.CalculationResult;
import dev.yewintnaing.expreval.core.ExpressionEvaluator;
import dev.yewintnaing.expreval.core.Operand;
import dev.yewintnaing.expreval.core.RpnExpressionEvaluator;
import dev.yewintnaing.expreval.expression.ExpressionParser;
import dev.yewintnaing.expreval.expression.ExpressionValidator;
import dev.yewintnaing.expreval.storage.InMemoryOperandStorage;
import dev.yewintnaing.expreval.storage.OperandStorage;
import java.util.List;
import java.util.Objects;

/**
 * The primary entry point for the ExprEval library.
 * This class provides a thread-safe way to evaluate mathematical expressions
 * with
 * configurable storage, evaluation engines, and security limits.
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * ExpressionCalculator calculator = new ExpressionCalculator.Builder()
 *         .autoSave(true)
 *         .build();
 * CalculationResult result = calculator.calculate("1 + 2", "sum");
 * </pre>
 */
public class ExpressionCalculator {

    private final boolean autoSave;
    private final ExpressionEvaluator evaluator;
    private final OperandStorage storage;
    private final CalculatorConfig config;

    private ExpressionCalculator(boolean autoSave, ExpressionEvaluator evaluator, OperandStorage storage,
            CalculatorConfig config) {
        this.autoSave = autoSave;
        this.evaluator = evaluator;
        this.storage = storage;
        this.config = config;
    }

    /**
     * Executes the calculation of the given expression.
     *
     * @param expression The mathematical expression to evaluate
     * @param resultKey  The key to use for the result
     * @return The resulting Operand
     */
    public CalculationResult calculate(String expression, String resultKey) {
        long startTime = System.currentTimeMillis();

        ExpressionValidator.validate(expression, config);
        List<String> postfix = ExpressionParser.infixToPostfix(expression);

        if (postfix.size() > config.maxTokenCount()) {
            throw new dev.yewintnaing.expreval.exception.InvalidExpressionException(
                    "Expression exceeds maximum token count of " + config.maxTokenCount());
        }

        Operand operand = evaluator.evaluate(postfix, storage, resultKey);

        if (autoSave) {
            storage.add(operand);
        }

        long duration = System.currentTimeMillis() - startTime;
        return new CalculationResult(expression, operand, duration);
    }

    public OperandStorage getStorage() {
        return storage;
    }

    public static class Builder {
        private boolean autoSave = false;
        private ExpressionEvaluator evaluator = new RpnExpressionEvaluator();
        private OperandStorage storage = new InMemoryOperandStorage();
        private CalculatorConfig config = CalculatorConfig.DEFAULT;

        public Builder autoSave(boolean autoSave) {
            this.autoSave = autoSave;
            return this;
        }

        public Builder evaluator(ExpressionEvaluator evaluator) {
            this.evaluator = Objects.requireNonNull(evaluator);
            return this;
        }

        public Builder storage(OperandStorage storage) {
            this.storage = Objects.requireNonNull(storage);
            return this;
        }

        public Builder config(CalculatorConfig config) {
            this.config = Objects.requireNonNull(config);
            return this;
        }

        public ExpressionCalculator build() {
            return new ExpressionCalculator(autoSave, evaluator, storage, config);
        }
    }
}
