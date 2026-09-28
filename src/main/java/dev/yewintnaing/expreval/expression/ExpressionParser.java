package dev.yewintnaing.expreval.expression;

import dev.yewintnaing.expreval.exception.InvalidExpressionException;
import dev.yewintnaing.expreval.function.FunctionRegistry;
import dev.yewintnaing.expreval.function.StandardMathFunctions;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Utility for parsing mathematical expressions, tokenizing, and converting infix notation
 * to Reverse Polish Notation (RPN).
 */
public final class ExpressionParser {

    private ExpressionParser() {
        // Utility class constructor
    }

    /**
     * Checks if a character can validly start an operand or identifier.
     *
     * @param c the character to check
     * @return true if valid start of operand, false otherwise
     */
    public static boolean isValidOperandStart(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '[' || c == '$';
    }

    /**
     * Tokenizes an expression string into individual tokens including numbers (with scientific
     * notation support), identifiers, bracketed names, operators, commas, and parentheses.
     *
     * @param expression the raw expression string
     * @return a list of tokens
     * @throws InvalidExpressionException if an unclosed bracket or invalid character is encountered
     */
    public static List<String> tokenize(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new InvalidExpressionException("Expression cannot be null or empty");
        }

        List<String> tokens = new ArrayList<>();
        int len = expression.length();
        int i = 0;

        while (i < len) {
            char c = expression.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // Bracketed operands such as [Test_Data]
            if (c == '[') {
                int closeIndex = expression.indexOf(']', i);
                if (closeIndex == -1) {
                    throw new InvalidExpressionException("Unclosed bracket starting at position " + i);
                }
                tokens.add(expression.substring(i, closeIndex + 1));
                i = closeIndex + 1;
                continue;
            }

            // Numbers: digits or leading decimal dot followed by digit
            if (Character.isDigit(c) || (c == '.' && i + 1 < len && Character.isDigit(expression.charAt(i + 1)))) {
                int start = i;
                boolean hasDot = (c == '.');
                i++;
                while (i < len) {
                    char ch = expression.charAt(i);
                    if (Character.isDigit(ch)) {
                        i++;
                    } else if (ch == '.' && !hasDot) {
                        hasDot = true;
                        i++;
                    } else if (ch == 'e' || ch == 'E') {
                        // Scientific notation
                        if (i + 1 < len) {
                            char next = expression.charAt(i + 1);
                            if ((next == '+' || next == '-') && i + 2 < len
                                    && Character.isDigit(expression.charAt(i + 2))) {
                                i += 3;
                                while (i < len && Character.isDigit(expression.charAt(i))) {
                                    i++;
                                }
                                break;
                            } else if (Character.isDigit(next)) {
                                i += 2;
                                while (i < len && Character.isDigit(expression.charAt(i))) {
                                    i++;
                                }
                                break;
                            }
                        }
                        break;
                    } else {
                        break;
                    }
                }
                tokens.add(expression.substring(start, i));
                continue;
            }

            // Identifiers (variable names or function names)
            if (Character.isLetter(c) || c == '_' || c == '$') {
                int start = i;
                while (i < len) {
                    char ch = expression.charAt(i);
                    if (Character.isLetterOrDigit(ch) || ch == '_' || ch == '$') {
                        i++;
                    } else {
                        break;
                    }
                }
                tokens.add(expression.substring(start, i));
                continue;
            }

            // Operators and punctuation
            if (isPunctuationOrOperator(c)) {
                tokens.add(String.valueOf(c));
                i++;
                continue;
            }

            throw new InvalidExpressionException("Unexpected character '" + c + "' at position " + i);
        }

        return tokens;
    }

    /**
     * Converts an infix expression string into a list of tokens in postfix (RPN) order
     * using the default function registry.
     *
     * @param expression The mathematical expression in infix notation
     * @return A list of tokens in postfix notation
     * @throws InvalidExpressionException if the expression is malformed
     */
    public static List<String> infixToPostfix(String expression) {
        return infixToPostfix(expression, FunctionRegistry.createDefault());
    }

    /**
     * Converts an infix expression string into a list of tokens in postfix (RPN) order
     * using a specified function registry.
     *
     * @param expression The mathematical expression in infix notation
     * @param registry   The registry used to identify functions
     * @return A list of tokens in postfix notation
     * @throws InvalidExpressionException if the expression is malformed
     */
    public static List<String> infixToPostfix(String expression, FunctionRegistry registry) {
        if (expression == null || expression.isBlank()) {
            throw new InvalidExpressionException("Expression cannot be null or empty");
        }

        List<String> tokens = tokenize(expression);
        Stack<String> operatorStack = new Stack<>();
        Stack<Boolean> isFunctionParenStack = new Stack<>();
        Stack<Integer> argCountStack = new Stack<>();
        List<String> outputs = new ArrayList<>();

        String prevToken = null;
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            if (token.equals("-") && isUnaryPosition(prevToken)) {
                operatorStack.push("neg");
            } else if (token.equals("+") && isUnaryPosition(prevToken)) {
                // Unary plus is a no-op
                continue;
            } else if (isFunction(token, registry) && i + 1 < tokens.size() && tokens.get(i + 1).equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(",")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    outputs.add(operatorStack.pop());
                }
                if (operatorStack.isEmpty() || argCountStack.isEmpty()) {
                    throw new InvalidExpressionException("Misplaced comma or mismatched parentheses");
                }
                int currentCount = argCountStack.pop();
                argCountStack.push(currentCount + 1);
            } else if (token.equals("(")) {
                boolean isFunc = (prevToken != null && isFunction(prevToken, registry));
                isFunctionParenStack.push(isFunc);
                if (isFunc) {
                    if (i + 1 < tokens.size() && tokens.get(i + 1).equals(")")) {
                        argCountStack.push(0);
                    } else {
                        argCountStack.push(1);
                    }
                }
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    outputs.add(operatorStack.pop());
                }
                if (operatorStack.isEmpty() || isFunctionParenStack.isEmpty()) {
                    throw new InvalidExpressionException("Mismatched parentheses in expression");
                }
                operatorStack.pop(); // Pop "("
                boolean wasFunc = isFunctionParenStack.pop();

                if (wasFunc) {
                    if (operatorStack.isEmpty()) {
                        throw new InvalidExpressionException("Expected function on stack");
                    }
                    String funcName = operatorStack.pop();
                    int argCount = argCountStack.pop();
                    outputs.add(funcName + "#" + argCount);
                }
            } else if (isOperator(token)) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")
                        && shouldPopOperator(token, operatorStack.peek())) {
                    outputs.add(operatorStack.pop());
                }
                operatorStack.push(token);
            } else {
                outputs.add(token);
            }

            prevToken = token;
        }

        while (!operatorStack.isEmpty()) {
            String op = operatorStack.pop();
            if (op.equals("(") || op.equals(")")) {
                throw new InvalidExpressionException("Mismatched parentheses in expression");
            }
            outputs.add(op);
        }

        return outputs;
    }

    private static boolean isUnaryPosition(String prevToken) {
        return prevToken == null || prevToken.equals("(") || prevToken.equals(",") || isOperator(prevToken);
    }

    private static boolean isFunction(String token, FunctionRegistry registry) {
        if (registry != null && registry.contains(token)) {
            return true;
        }
        return StandardMathFunctions.isFunction(token);
    }

    private static boolean isPunctuationOrOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '%' || c == '(' || c == ')'
                || c == ',';
    }

    private static boolean isOperator(String token) {
        if (token == null) {
            return false;
        }
        if (token.equals("neg")) {
            return true;
        }
        if (token.length() != 1) {
            return false;
        }
        char ch = token.charAt(0);
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^' || ch == '%';
    }

    private static boolean shouldPopOperator(String currentOp, String stackOp) {
        int currentPrec = getPrecedence(currentOp);
        int stackPrec = getPrecedence(stackOp);
        if (hasLeftAssociativity(currentOp)) {
            return currentPrec <= stackPrec;
        } else {
            return currentPrec < stackPrec;
        }
    }

    private static int getPrecedence(String token) {
        if (token.equals("neg")) {
            return 4;
        }
        if (token.length() == 1) {
            return switch (token.charAt(0)) {
                case '+', '-' -> 1;
                case '*', '/', '%' -> 2;
                case '^' -> 3;
                default -> -1;
            };
        }
        return -1;
    }

    private static boolean hasLeftAssociativity(String token) {
        if (token.equals("neg") || token.equals("^")) {
            return false;
        }
        return true;
    }
}
