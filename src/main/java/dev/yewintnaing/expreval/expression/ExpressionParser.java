package dev.yewintnaing.expreval.expression;

import dev.yewintnaing.expreval.exception.InvalidExpressionException;
import dev.yewintnaing.expreval.function.StandardMathFunctions;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Utility for parsing mathematical expressions and converting them to Reverse
 * Polish Notation (RPN).
 */
public class ExpressionParser {

    private ExpressionParser() {
        // Private constructor to prevent instantiation
    }

    public static boolean isValidOperandStart(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '[' || c == '$';
    }

    /**
     * Converts an infix expression string into a list of tokens in postfix (RPN)
     * order.
     *
     * @param expression The mathematical expression in infix notation
     * @return A list of tokens in postfix notation
     * @throws InvalidExpressionException if the expression is malformed
     */
    public static List<String> infixToPostfix(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new InvalidExpressionException("Expression cannot be null or empty");
        }

        Stack<String> stack = new Stack<>();
        List<String> outputs = new ArrayList<>();

        // Improved regex to handle operands with brackets or special chars, including %
        String regex = "(?<=[\\(\\-\\+\\*/\\^%\\)])|(?=[\\(\\-\\+\\*/\\^%\\)])";
        String[] expressionList = expression.split(regex);

        String prevToken = null;
        for (String exp : expressionList) {
            String token = exp.trim();
            if (token.isEmpty())
                continue;

            if (token.equals("-") && (prevToken == null || prevToken.equals("(") || isOperator(prevToken))) {
                // Treat as unary minus (neg function)
                stack.push("neg");
            } else if (StandardMathFunctions.isFunction(token)) {
                stack.push(token);
            } else if (isValidOperandStart(token.charAt(0))) {
                outputs.add(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    outputs.add(stack.pop());
                }
                if (stack.isEmpty() || !stack.peek().equals("(")) {
                    throw new InvalidExpressionException("Mismatched parentheses in expression");
                }
                stack.pop(); // Pop "("

                // If there is a function token at the top of the stack, pop it
                if (!stack.isEmpty() && StandardMathFunctions.isFunction(stack.peek())) {
                    outputs.add(stack.pop());
                }
            } else {
                while (!stack.isEmpty() && !stack.peek().equals("(")
                        && getPrecedence(token.charAt(0)) <= getPrecedence(stack.peek().charAt(0))
                        && hasLeftAssociativity(token.charAt(0))) {
                    outputs.add(stack.pop());
                }
                stack.push(token);
            }
            prevToken = token;
        }

        while (!stack.isEmpty()) {
            if (stack.peek().equals("(")) {
                throw new InvalidExpressionException("Mismatched parentheses in expression");
            }
            outputs.add(stack.pop());
        }

        return outputs;
    }

    private static boolean isOperator(String token) {
        if (token == null || token.length() != 1)
            return false;
        char ch = token.charAt(0);
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^' || ch == '%';
    }

    private static int getPrecedence(char ch) {
        return switch (ch) {
            case '+', '-' -> 1;
            case '*', '/', '%' -> 2;
            case '^' -> 3;
            default -> -1;
        };
    }

    private static boolean hasLeftAssociativity(char ch) {
        return ch == '+' || ch == '-' || ch == '/' || ch == '*' || ch == '%';
    }
}
