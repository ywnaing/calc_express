package dev.yewintnaing.expreval;

import dev.yewintnaing.expreval.CalculatorConfig;
import dev.yewintnaing.expreval.core.CalculationResult;
import dev.yewintnaing.expreval.core.Operand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;

class ExpressionCalculatorTest {

    @Test
    @DisplayName("Should evaluate complex expression correctly")
    void shouldEvaluateComplexExpression() {
        // Arrange
        var exp = "a+b*(b-c)^2";
        var a = BigDecimal.valueOf(200);
        var b = BigDecimal.valueOf(100);
        var c = BigDecimal.valueOf(50);

        // (200 + 100 * (100 - 50)^2) = 200 + 100 * 2500 = 200 + 250000 = 250200
        var expectedValue = a.add(b.multiply(b.subtract(c).pow(2))).setScale(4, RoundingMode.HALF_EVEN);

        ExpressionCalculator calculator = new ExpressionCalculator.Builder()
                .autoSave(true)
                .build();

        calculator.getStorage().add(new Operand("a", a));
        calculator.getStorage().add(new Operand("b", b));
        calculator.getStorage().add(new Operand("c", c));

        // Act
        CalculationResult result = calculator.calculate(exp, "Result");

        // Assert
        Assertions.assertEquals(expectedValue, result.value());
        Assertions.assertEquals("Result", result.key());
        Assertions.assertTrue(calculator.getStorage().get("Result").isPresent());
    }

    @Test
    @DisplayName("Should handle basic operations")
    void shouldHandleBasicOperations() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder().build();

        Assertions.assertEquals(new BigDecimal("10.0000"), calculator.calculate("5+5", null).value());
        Assertions.assertEquals(new BigDecimal("0.0000"), calculator.calculate("5-5", null).value());
        Assertions.assertEquals(new BigDecimal("25.0000"), calculator.calculate("5*5", null).value());
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("5/5", null).value());
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("5^0", null).value());
        Assertions.assertEquals(new BigDecimal("0.0000"), calculator.calculate("5%5", null).value());
    }

    @Test
    @DisplayName("Should evaluate math functions")
    void shouldEvaluateMathFunctions() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder().build();

        Assertions.assertEquals(new BigDecimal("5.0000"), calculator.calculate("sqrt(25)", null).value());
        Assertions.assertEquals(new BigDecimal("10.0000"), calculator.calculate("abs(-10)", null).value());
        Assertions.assertEquals(new BigDecimal("-5.0000"), calculator.calculate("neg(5)", null).value());
        Assertions.assertEquals(new BigDecimal("3.0000"), calculator.calculate("round(2.6)", null).value());
        Assertions.assertEquals(new BigDecimal("2.0000"), calculator.calculate("floor(2.9)", null).value());
        Assertions.assertEquals(new BigDecimal("3.0000"), calculator.calculate("ceil(2.1)", null).value());
        Assertions.assertEquals(new BigDecimal("100.0000"), calculator.calculate("pow10(2)", null).value());

        // Nested functions
        Assertions.assertEquals(new BigDecimal("3.0000"), calculator.calculate("sqrt(abs(-9))", null).value());
    }

    @Test
    @DisplayName("Should respect security limits")
    void shouldRespectSecurityLimits() {
        // Test max length
        CalculatorConfig shortConfig = new CalculatorConfig(5, 100, 10);
        ExpressionCalculator calculator = new ExpressionCalculator.Builder()
                .config(shortConfig)
                .build();

        Assertions.assertThrows(dev.yewintnaing.expreval.exception.InvalidExpressionException.class,
                () -> calculator.calculate("100+100", null));

        // Test max tokens
        CalculatorConfig fewTokens = new CalculatorConfig(100, 2, 10);
        ExpressionCalculator calculator2 = new ExpressionCalculator.Builder()
                .config(fewTokens)
                .build();
        Assertions.assertThrows(dev.yewintnaing.expreval.exception.InvalidExpressionException.class,
                () -> calculator2.calculate("1+2+3", null));

        // Test max depth
        CalculatorConfig shallow = new CalculatorConfig(100, 100, 1);
        ExpressionCalculator calculator3 = new ExpressionCalculator.Builder()
                .config(shallow)
                .build();
        Assertions.assertThrows(dev.yewintnaing.expreval.exception.InvalidExpressionException.class,
                () -> calculator3.calculate("(1+(2))", null));
    }
}
