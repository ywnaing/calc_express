package dev.yewintnaing.expreval;

import dev.yewintnaing.expreval.core.CalculationResult;
import dev.yewintnaing.expreval.core.ExpressionEvaluator;
import dev.yewintnaing.expreval.core.Operand;
import dev.yewintnaing.expreval.storage.MapOperandStorage;
import dev.yewintnaing.expreval.storage.OperandStorage;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Default immutable implementation of {@link CompiledExpression}.
 */
final class DefaultCompiledExpression implements CompiledExpression {

    private final String expression;
    private final List<String> postfixTokens;
    private final ExpressionEvaluator evaluator;
    private final OperandStorage defaultStorage;
    private final boolean autoSave;

    DefaultCompiledExpression(String expression, List<String> postfixTokens, ExpressionEvaluator evaluator,
                              OperandStorage defaultStorage, boolean autoSave) {
        this.expression = Objects.requireNonNull(expression, "Expression cannot be null");
        this.postfixTokens = List.copyOf(Objects.requireNonNull(postfixTokens, "Tokens cannot be null"));
        this.evaluator = Objects.requireNonNull(evaluator, "Evaluator cannot be null");
        this.defaultStorage = Objects.requireNonNull(defaultStorage, "Storage cannot be null");
        this.autoSave = autoSave;
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public CalculationResult evaluate() {
        return evaluate(defaultStorage, "Result");
    }

    @Override
    public CalculationResult evaluate(String resultKey) {
        return evaluate(defaultStorage, resultKey);
    }

    @Override
    public CalculationResult evaluate(OperandStorage storage, String resultKey) {
        long startTime = System.currentTimeMillis();
        Operand operand = evaluator.evaluate(postfixTokens, storage, resultKey);
        if (autoSave && storage != null) {
            storage.add(operand);
        }
        if (storage != null) {
            storage.add(new Operand("ans", operand.value()));
        }
        long duration = System.currentTimeMillis() - startTime;
        return new CalculationResult(expression, operand, duration);
    }

    @Override
    public CalculationResult evaluateWith(Map<String, BigDecimal> variables) {
        return evaluateWith(variables, "Result");
    }

    @Override
    public CalculationResult evaluateWith(Map<String, BigDecimal> variables, String resultKey) {
        OperandStorage mapStorage = new MapOperandStorage(variables, defaultStorage);
        return evaluate(mapStorage, resultKey);
    }
}
