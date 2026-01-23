package dev.yewintnaing.expreval.core;

import dev.yewintnaing.expreval.storage.OperandStorage;
import java.util.List;

/**
 * Defines the contract for evaluating a list of tokens representing a
 * mathematical expression.
 */
public interface ExpressionEvaluator {

    /**
     * Evaluates the expression using the provided operand storage.
     *
     * @param tokens         The list of tokens (in postfix notation) to evaluate
     * @param operandStorage The storage to retrieve operand values from
     * @param resultKey      The key to associate with the resulting operand
     * @return The resulting Operand
     */
    Operand evaluate(List<String> tokens, OperandStorage operandStorage, String resultKey);
}
