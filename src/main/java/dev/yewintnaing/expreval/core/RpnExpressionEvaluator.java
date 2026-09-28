package dev.yewintnaing.expreval.core;

import dev.yewintnaing.expreval.exception.DivisionByZeroException;
import dev.yewintnaing.expreval.exception.InvalidExpressionException;
import dev.yewintnaing.expreval.exception.OperandNotFoundException;
import dev.yewintnaing.expreval.expression.ExpressionParser;
import dev.yewintnaing.expreval.function.FunctionRegistry;
import dev.yewintnaing.expreval.function.MathFunction;
import dev.yewintnaing.expreval.function.StandardMathFunctions;
import dev.yewintnaing.expreval.storage.OperandStorage;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * An implementation of {@link ExpressionEvaluator} that evaluates Reverse Polish
 * Notation (RPN) tokens with precision scaling, rounding, and function registry support.
 */
public class RpnExpressionEvaluator implements ExpressionEvaluator {

    private final int scale;
    private final RoundingMode roundingMode;
    private final FunctionRegistry functionRegistry;

    /**
     * Constructs a default RpnExpressionEvaluator with scale 4, HALF_EVEN rounding,
     * and the default standard function registry.
     */
    public RpnExpressionEvaluator() {
        this(4, RoundingMode.HALF_EVEN, FunctionRegistry.createDefault());
    }

    /**
     * Constructs an RpnExpressionEvaluator with specified scale and rounding mode,
     * using the default standard function registry.
     *
     * @param scale        the scale for division and results
     * @param roundingMode the rounding mode to apply
     */
    public RpnExpressionEvaluator(int scale, RoundingMode roundingMode) {
        this(scale, roundingMode, FunctionRegistry.createDefault());
    }

    /**
     * Constructs an RpnExpressionEvaluator with specified scale, rounding mode,
     * and a custom function registry.
     *
     * @param scale            the scale for division and results
     * @param roundingMode     the rounding mode to apply
     * @param functionRegistry the function registry for resolving functions
     */
    public RpnExpressionEvaluator(int scale, RoundingMode roundingMode, FunctionRegistry functionRegistry) {
        this.scale = scale;
        this.roundingMode = Objects.requireNonNull(roundingMode, "RoundingMode cannot be null");
        this.functionRegistry = functionRegistry != null ? functionRegistry : FunctionRegistry.createDefault();
    }

    @Override
    public Operand evaluate(List<String> tokens, OperandStorage operandStorage, String resultKey) {
        Stack<BigDecimal> operandStack = new Stack<>();

        for (String token : tokens) {
            if (token.equals("neg")) {
                if (operandStack.isEmpty()) {
                    throw new InvalidExpressionException("Insufficient operand for negation");
                }
                operandStack.push(operandStack.pop().negate());
            } else if (token.contains("#")) {
                int hashIdx = token.indexOf('#');
                String fnName = token.substring(0, hashIdx);
                int arity = Integer.parseInt(token.substring(hashIdx + 1));

                if (operandStack.size() < arity) {
                    throw new InvalidExpressionException(
                            "Insufficient operands for function: " + fnName + " (expected " + arity + ")");
                }

                List<BigDecimal> args = new ArrayList<>(arity);
                for (int a = 0; a < arity; a++) {
                    args.add(operandStack.pop());
                }
                Collections.reverse(args);

                MathFunction fn = functionRegistry.get(fnName)
                        .orElseThrow(() -> new InvalidExpressionException("Unknown function: " + fnName));

                if (!fn.acceptsArgCount(arity)) {
                    throw new InvalidExpressionException(
                            "Function " + fnName + " does not accept " + arity + " argument(s)");
                }

                BigDecimal result = fn.execute(args, new MathContext(16));
                operandStack.push(result);
            } else if (token.equalsIgnoreCase("pi")) {
                operandStack.push(operandStorage != null && operandStorage.contains(token)
                        ? operandStorage.get(token).get().value()
                        : BigDecimal.valueOf(Math.PI));
            } else if (token.equalsIgnoreCase("e")) {
                operandStack.push(operandStorage != null && operandStorage.contains(token)
                        ? operandStorage.get(token).get().value()
                        : BigDecimal.valueOf(Math.E));
            } else if (token.equalsIgnoreCase("ans")) {
                BigDecimal ansVal = BigDecimal.ZERO;
                if (operandStorage != null) {
                    if (operandStorage.contains("ans")) {
                        ansVal = operandStorage.get("ans").get().value();
                    } else if (operandStorage.contains(token)) {
                        ansVal = operandStorage.get(token).get().value();
                    }
                }
                operandStack.push(ansVal);
            } else if (functionRegistry.contains(token) || StandardMathFunctions.isFunction(token)) {
                MathFunction fn = functionRegistry.get(token).orElse(null);
                if (fn != null && fn.getMinArgs() == 0) {
                    operandStack.push(fn.execute(List.of(), new MathContext(16)));
                } else {
                    if (operandStack.isEmpty()) {
                        throw new InvalidExpressionException("Insufficient operand for function: " + token);
                    }
                    BigDecimal val = operandStack.pop();
                    BigDecimal result;
                    if (fn != null) {
                        result = fn.execute(List.of(val), new MathContext(16));
                    } else {
                        result = StandardMathFunctions.fromName(token).apply(val);
                    }
                    operandStack.push(result);
                }
            } else if (ExpressionParser.isValidOperandStart(token.charAt(0))) {
                if (isNumeric(token)) {
                    operandStack.push(new BigDecimal(token));
                } else {
                    if (operandStorage == null) {
                        throw new OperandNotFoundException(token);
                    }
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
        if (str == null) {
            return false;
        }
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
