package dev.yewintnaing.expreval.expression;

import dev.yewintnaing.expreval.CalculatorConfig;
import dev.yewintnaing.expreval.exception.InvalidExpressionException;
import java.util.Stack;

/**
 * Validates mathematical expressions before they are evaluated.
 */
public class ExpressionValidator {

    /**
     * Validates that the expression has balanced parentheses and respects security
     * limits.
     *
     * @param expression The expression to validate
     * @param config     The configuration with limits
     * @throws InvalidExpressionException if the expression is invalid or exceeds
     *                                    limits
     */
    public static void validate(String expression, CalculatorConfig config) {
        if (expression == null || expression.isBlank()) {
            throw new InvalidExpressionException("Expression cannot be empty");
        }

        if (expression.length() > config.maxExpressionLength()) {
            throw new InvalidExpressionException(
                    "Expression exceeds maximum length of " + config.maxExpressionLength());
        }

        checkParenthesesAndDepth(expression, config.maxDepth());
    }

    private static void checkParenthesesAndDepth(String expression, int maxDepth) {
        Stack<Integer> stack = new Stack<>();
        int currentDepth = 0;
        int maxSeenDepth = 0;

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') {
                stack.push(i);
                currentDepth++;
                maxSeenDepth = Math.max(maxSeenDepth, currentDepth);
                if (maxSeenDepth > maxDepth) {
                    throw new InvalidExpressionException("Expression nesting exceeds maximum depth of " + maxDepth);
                }
            } else if (c == ')') {
                if (stack.isEmpty()) {
                    throw new InvalidExpressionException("Unmatched closing parenthesis at position " + i);
                }
                stack.pop();
                currentDepth--;
            }
        }
        if (!stack.isEmpty()) {
            throw new InvalidExpressionException("Unmatched opening parenthesis at position " + stack.pop());
        }
    }
}
