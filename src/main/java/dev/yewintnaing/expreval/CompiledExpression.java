package dev.yewintnaing.expreval;

import dev.yewintnaing.expreval.core.CalculationResult;
import dev.yewintnaing.expreval.storage.OperandStorage;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Represents a pre-compiled and validated mathematical expression ready for repeated high-throughput evaluation.
 */
public interface CompiledExpression {

    /**
     * Returns the original expression string.
     *
     * @return the raw expression
     */
    String getExpression();

    /**
     * Evaluates the pre-compiled expression using the default storage and a default result key.
     *
     * @return the calculation result
     */
    CalculationResult evaluate();

    /**
     * Evaluates the pre-compiled expression using a custom result key.
     *
     * @param resultKey key name for the calculated operand
     * @return the calculation result
     */
    CalculationResult evaluate(String resultKey);

    /**
     * Evaluates the pre-compiled expression against the provided operand storage.
     *
     * @param storage   operand storage
     * @param resultKey key name for the calculated operand
     * @return the calculation result
     */
    CalculationResult evaluate(OperandStorage storage, String resultKey);

    /**
     * Evaluates the pre-compiled expression against a transient map of variables.
     *
     * @param variables map of variable names to BigDecimal values
     * @return the calculation result
     */
    CalculationResult evaluateWith(Map<String, BigDecimal> variables);

    /**
     * Evaluates the pre-compiled expression against a transient map of variables with a result key.
     *
     * @param variables map of variable names to BigDecimal values
     * @param resultKey key name for the calculated operand
     * @return the calculation result
     */
    CalculationResult evaluateWith(Map<String, BigDecimal> variables, String resultKey);
}
