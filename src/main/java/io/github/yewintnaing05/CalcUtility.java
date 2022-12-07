package io.github.yewintnaing05;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CalcUtility {

    protected static boolean letterOrDigit(char c) {
        return Character.isLetterOrDigit(c);
    }

    protected static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    // get precedence for operator
    protected static int getPrecedence(char ch) {

        if (ch == '+' || ch == '-') return 1;
        else if (ch == '*' || ch == '/') return 2;
        else if (ch == '^') return 3;
        else return -1;
    }

    private static boolean hasLeftAssociativity(char ch) {
        return ch == '+' || ch == '-' || ch == '/' || ch == '*';
    }

    // reference from  https://www.geeksforgeeks.org/infix-to-prefix-conversion-using-two-stacks/
    protected static List<String> infixToRpn(String expression) {
        // Initialising an empty String
        // (for output) and an empty stack
        Stack<String> stack = new Stack<>();

        // Initially empty list for output
        List<String> outputs = new ArrayList<>();
        String regex = "(?<=[(\\-+*/^)])|(?=[(\\-+*/^)])";
        String[] expressionList = expression.split(regex);

        for (String exp :
                expressionList) {
            if (letterOrDigit(exp.charAt(0))) {
                outputs.add(exp);
            } else if (exp.equalsIgnoreCase("(")) {
                stack.push(exp + "");
            } else if (exp.equalsIgnoreCase(")")) {
                while (!stack.isEmpty()
                        && !stack.peek().equals("(")) {
                    outputs.add(stack.pop());
                }

                stack.pop();
            } else {

                while (
                        !stack.isEmpty()
                                && getPrecedence(exp.charAt(0))
                                <= getPrecedence(stack.peek().charAt(0))
                                && hasLeftAssociativity(exp.charAt(0))) {

                    outputs.add(stack.pop());
                }
                stack.push(exp + "");

            }
        }

        while (!stack.isEmpty()) {
            if (stack.peek().equals("("))
                throw new IllegalStateException("Invalid Expression!");
            outputs.add(stack.pop());
        }
        return outputs;
    }
}
