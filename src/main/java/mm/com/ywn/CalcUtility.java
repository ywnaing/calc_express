package mm.com.ywn;

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

    protected static List<String> infixToRpn(String expression) {
        // Initialising an empty String
        // (for output) and an empty stack
        Stack<String> stack = new Stack<>();

        // Initially empty list for output
        List<String> outputs = new ArrayList<>();


        // Iterating over tokens using inbuilt
        // .length() function
        for (int i = 0; i < expression.length(); ) {
            // Finding character at 'i'th index
            char c = expression.charAt(i);

            // If the scanned Token is an
            // operand, add it to output
            if (letterOrDigit(c)) {

                if (String.valueOf(c).matches("([0-9]*[.])?[0-9]+")) {
                    System.out.println("digit");
//                    System.out.println(c);
                    int index = expression.indexOf('$', i);
//                    System.out.println(index);
                    String variable = expression.substring(i, index);

                    i = index + 1;

                    outputs.add(variable);
                }
            }

            // If the scanned Token is an '('
            // push it to the stack
            else if (c == '(') {
                stack.push(c + "");
                i++;
            }

            // If the scanned Token is an ')' pop and append
            // it to output from the stack until an '(' is
            // encountered
            else if (c == ')') {
                while (!stack.isEmpty()
                        && !stack.peek().equals("(")) {
                    outputs.add(stack.pop());
                }

                stack.pop();
                i++;
            }


            else {

                if (c == '[') {
                    int index = expression.indexOf(']', i);
                    String variable = expression.substring(i + 1, index);

                    i = index + 1;
                    outputs.add(variable);

                } else {
                    while (
                            !stack.isEmpty()
                                    && getPrecedence(c)
                                    <= getPrecedence(stack.peek().charAt(0))
                                    && hasLeftAssociativity(c)) {

                        outputs.add(stack.pop());
                    }
                    stack.push(c + "");
                    i++;
                }
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
