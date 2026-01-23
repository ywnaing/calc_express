package dev.yewintnaing.expreval.core;

import dev.yewintnaing.expreval.exception.DivisionByZeroException;
import dev.yewintnaing.expreval.exception.InvalidExpressionException;
import dev.yewintnaing.expreval.exception.OperandNotFoundException;
import dev.yewintnaing.expreval.expression.ExpressionParser;
import dev.yewintnaing.expreval.function.StandardMathFunctions;
import dev.yewintnaing.expreval.storage.OperandStorage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Stack;

/**
 * An implementation of ExpressionEvaluator that uses the Reverse Polish
 * Notation (RPN) algorithm.
 */
public class RpnExpressionEvaluator implements ExpressionEvaluator {

    private final int scale;
    private final RoundingMode roundingMode;

    public RpnExpressionEvaluator() {
        this(4, RoundingMode.HALF_EVEN);
    }

    public RpnExpressionEvaluator(int scale, RoundingMode roundingMode) {
        this.scale = scale;
        this.roundingMode = roundingMode;
    }

    @Override
    public Operand evaluate(List<String> tokens, OperandStorage operandStorage, String resultKey) {
        Stack<BigDecimal> operandStack = new Stack<>();

        for (String token : tokens) {
            if (StandardMathFunctions.isFunction(token)) {
                if (operandStack.isEmpty()) {
                    throw new InvalidExpressionException("Insufficient operand for function: " + token);
                }
                BigDecimal val = operandStack.pop();
                operandStack.push(StandardMathFunctions.fromName(token).apply(val));
            } else if (ExpressionParser.isValidOperandStart(token.charAt(0))) {
                if (isNumeric(token)) {
                    operandStack.push(new BigDecimal(token));
                } else {
                    operandStack.push(operandStorage.get(token)
                            .map(Operand::value)
                            .orElseThrow(() -> new OperandNotFoundException(token)));
                }
            } else {
                if (operandStack.size() < 2) {
                    throw new InvalidExpressionException(
                            "Invalid expression: insufficient operands for operator " + token);
                }
                BigDecimal op2 = operandStack.pop();
                BigDecimal op1 = operandStack.pop();

                BigDecimal result = switch (token) {
                    case "+" -> op1.add(op2);
                    case "-" -> op1.subtract(op2);
                    case "*" -> op1.multiply(op2);
                    case "/" -> {
                        if (op2.compareTo(BigDecimal.ZERO) == 0) {
                            throw new DivisionByZeroException();
                        }
                        yield op1.divide(op2, scale, roundingMode);
                    }
                    case "^" -> op1.pow(op2.intValue());
                    case "%" -> op1.remainder(op2);
                    default -> throw new InvalidExpressionException("Unsupported operator: " + token);
                };
                operandStack.push(result);
            }
        }

        if (operandStack.size() != 1) {
            throw new InvalidExpressionException("Invalid expression: too many operands or missing operator");
        }

        return new Operand(resultKey == null ? "Result" : resultKey,
                operandStack.pop().setScale(scale, roundingMode));
    }

    private boolean isNumeric(String str) {
        if (str == null)
            return false;
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
