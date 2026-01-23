package dev.yewintnaing.expreval;

import dev.yewintnaing.expreval.core.CalculationResult;
import dev.yewintnaing.expreval.core.Operand;
import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {

        ExpressionCalculator calculator = new ExpressionCalculator.Builder()
                .autoSave(true)
                .build();

        calculator.getStorage().add(new Operand("Test_Data", new BigDecimal("100")));
        calculator.getStorage().add(new Operand("Test_Data2", new BigDecimal("1.5")));

        // Note: New calculator supports simple variable names.
        // For the old [Test_Data] format, we added support for '[' and '$' in variable
        // names.
        String expression = "Test_Data/Test_Data2";

        CalculationResult result = calculator.calculate(expression, "Result");
        System.out.println("Result: " + result);

        // More complex expression
        String complexExpr = "Test_Data * 100 + (Test_Data2 - 20)^2 * Result^2 - 1";
        CalculationResult complexResult = calculator.calculate(complexExpr, "Testing_Result");

        System.out.println("Storage Content: " + calculator.getStorage().getAll());
        System.out.println("Complex Result: " + complexResult);
    }
}
