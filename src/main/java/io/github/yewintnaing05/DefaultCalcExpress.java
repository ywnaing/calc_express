package io.github.yewintnaing05;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Stack;

class DefaultCalcExpress implements ICalcExpress {


    @Override
    public Operand calculate(List<String> dataList, IOperandStorage operandStorage, String key) {

        Stack<BigDecimal> operandStack = new Stack<>();

        for (String token :
                dataList) {


            if (CalcUtility.letterOrDigit(token.charAt(0))) {

                if (token.matches("([0-9]*[.])?[0-9]+")) {
                    operandStack.push(new BigDecimal(token));
                } else {
                    operandStack.push(operandStorage.get(token.trim()).value());
                }
            } else {
                // if it's an operator, get the 2 elements at the top and perform the right operation.
                // you should remind that is always op2 (operator) op1
                if (operandStack.size() > 1) {
                    BigDecimal op1 = operandStack.pop();
                    BigDecimal op2 = operandStack.pop();
                    switch (token) {
                        case "+" -> operandStack.push(op2.add(op1));
                        case "-" -> operandStack.push(op2.subtract(op1));
                        case "*" -> operandStack.push(op2.multiply(op1));
                        case "/" -> {
                            if (op1.equals(BigDecimal.ZERO)) {
                                throw new ArithmeticException("Division by 0");
                            }
                            operandStack.push(op2.divide(op1, 4, RoundingMode.HALF_EVEN));
                        }
                        case "^" -> operandStack.push(op2.pow(op1.intValue()));
                        default -> throw new IllegalArgumentException(token + " is not an operator or is not handled");
                    }
                }

            }


        }

        if (operandStack.empty() || operandStack.size() > 1) {
            throw new IllegalArgumentException("Invalid expression, could not find a result. An operator seems to be absent");
        }

        return new Operand(key, operandStack.peek().setScale(4, RoundingMode.HALF_EVEN));
    }
}
