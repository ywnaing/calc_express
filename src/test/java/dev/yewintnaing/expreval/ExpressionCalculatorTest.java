package dev.yewintnaing.expreval;

import dev.yewintnaing.expreval.core.CalculationResult;
import dev.yewintnaing.expreval.core.Operand;
import dev.yewintnaing.expreval.exception.DivisionByZeroException;
import dev.yewintnaing.expreval.exception.InvalidExpressionException;
import dev.yewintnaing.expreval.exception.OperandNotFoundException;
import dev.yewintnaing.expreval.function.MathFunction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExpressionCalculatorTest {

    @Test
    @DisplayName("Should evaluate complex expression correctly")
    void shouldEvaluateComplexExpression() {
        var exp = "a+b*(b-c)^2";
        var a = BigDecimal.valueOf(200);
        var b = BigDecimal.valueOf(100);
        var c = BigDecimal.valueOf(50);

        var expectedValue = a.add(b.multiply(b.subtract(c).pow(2))).setScale(4, RoundingMode.HALF_EVEN);

        ExpressionCalculator calculator = new ExpressionCalculator.Builder()
                .autoSave(true)
                .build();

        calculator.getStorage().add(new Operand("a", a));
        calculator.getStorage().add(new Operand("b", b));
        calculator.getStorage().add(new Operand("c", c));

        CalculationResult result = calculator.calculate(exp, "Result");

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
        // Convenient 1-arg overload
        Assertions.assertEquals(new BigDecimal("15.0000"), calculator.calculate("10+5").value());
    }

    @Test
    @DisplayName("Should evaluate legacy math functions")
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
    @DisplayName("Should evaluate multi-argument standard math functions")
    void shouldEvaluateMultiArgumentMathFunctions() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder().build();

        // min and max (variadic >= 2)
        Assertions.assertEquals(new BigDecimal("5.0000"), calculator.calculate("min(10, 5, 20)").value());
        Assertions.assertEquals(new BigDecimal("20.0000"), calculator.calculate("max(10, 5, 20)").value());

        // pow(base, exp)
        Assertions.assertEquals(new BigDecimal("8.0000"), calculator.calculate("pow(2, 3)").value());

        // clamp(val, min, max)
        Assertions.assertEquals(new BigDecimal("10.0000"), calculator.calculate("clamp(15, 0, 10)").value());
        Assertions.assertEquals(new BigDecimal("0.0000"), calculator.calculate("clamp(-5, 0, 10)").value());
        Assertions.assertEquals(new BigDecimal("7.0000"), calculator.calculate("clamp(7, 0, 10)").value());

        // round with scale
        Assertions.assertEquals(new BigDecimal("3.1400"), calculator.calculate("round(3.14159, 2)").value());
        Assertions.assertEquals(new BigDecimal("3.0000"), calculator.calculate("round(3.14159)").value());

        // Nested functions with arithmetic inside
        Assertions.assertEquals(new BigDecimal("7.0000"),
                calculator.calculate("min(max(1, 10), (3 + 4), 15)").value());
    }

    @Test
    @DisplayName("Should evaluate scientific math functions and constants")
    void shouldEvaluateScientificFunctionsAndConstants() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder().build();

        Assertions.assertEquals(new BigDecimal("0.0000"), calculator.calculate("sin(0)").value());
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("cos(0)").value());
        Assertions.assertEquals(new BigDecimal("0.0000"), calculator.calculate("tan(0)").value());
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("exp(0)").value());
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("signum(50)").value());
        Assertions.assertEquals(new BigDecimal("-1.0000"), calculator.calculate("signum(-25)").value());
        Assertions.assertEquals(new BigDecimal("2.0000"), calculator.calculate("log(100)").value());
        Assertions.assertEquals(new BigDecimal("3.0000"), calculator.calculate("log(2, 8)").value());
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("round(ln(e()), 0)").value());

        // Built-in constants PI and E
        Assertions.assertEquals(new BigDecimal("3.1416"), calculator.calculate("round(pi(), 4)").value());
        Assertions.assertEquals(new BigDecimal("3.1416"), calculator.calculate("round(pi, 4)").value());
        Assertions.assertEquals(new BigDecimal("2.7183"), calculator.calculate("round(e, 4)").value());
    }

    @Test
    @DisplayName("Should support custom user-defined functions")
    void shouldSupportCustomFunctions() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder()
                .registerFunction(MathFunction.binary("tax", (income, rate) -> income.multiply(rate)))
                .registerFunction(MathFunction.variadic("sum", 2, args ->
                        args.stream().reduce(BigDecimal.ZERO, BigDecimal::add)))
                .registerFunction(MathFunction.nullary("magic", () -> new BigDecimal("42")))
                .build();

        CalculationResult taxRes = calculator.calculate("tax(1000, 0.2)");
        Assertions.assertEquals(new BigDecimal("200.0000"), taxRes.value());

        CalculationResult sumRes = calculator.calculate("sum(1, 2, 3, 4)");
        Assertions.assertEquals(new BigDecimal("10.0000"), sumRes.value());

        CalculationResult magicRes = calculator.calculate("magic() + 8");
        Assertions.assertEquals(new BigDecimal("50.0000"), magicRes.value());
    }

    @Test
    @DisplayName("Should handle scientific notation and unary minus accurately")
    void shouldHandleScientificNotationAndUnaryMinus() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder().build();

        // Scientific notation numbers
        Assertions.assertEquals(new BigDecimal("105.0000"), calculator.calculate("1e2 + 5").value());
        Assertions.assertEquals(new BigDecimal("0.0250"), calculator.calculate("2.5e-2").value());

        // Unary minus after operators and inside functions
        Assertions.assertEquals(new BigDecimal("-10.0000"), calculator.calculate("5 * -2").value());
        Assertions.assertEquals(new BigDecimal("-10.0000"), calculator.calculate("min(-5, -10)").value());
        Assertions.assertEquals(new BigDecimal("-5.0000"), calculator.calculate("-10 + 5").value());
    }

    @Test
    @DisplayName("Should support pre-compilation via CompiledExpression")
    void shouldSupportCompiledExpression() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder().build();

        CompiledExpression compiled = calculator.compile("price * (1 + tax_rate) - discount");
        Assertions.assertEquals("price * (1 + tax_rate) - discount", compiled.getExpression());

        // First evaluation
        CalculationResult res1 = compiled.evaluateWith(Map.of(
                "price", new BigDecimal("100"),
                "tax_rate", new BigDecimal("0.10"),
                "discount", new BigDecimal("5")), "order1");
        Assertions.assertEquals(new BigDecimal("105.0000"), res1.value());
        Assertions.assertEquals("order1", res1.key());

        // Second evaluation with different inputs
        CalculationResult res2 = compiled.evaluateWith(Map.of(
                "price", new BigDecimal("200"),
                "tax_rate", new BigDecimal("0.20"),
                "discount", new BigDecimal("40")));
        Assertions.assertEquals(new BigDecimal("200.0000"), res2.value());
    }

    @Test
    @DisplayName("Should support calculateWith directly with transient variable maps")
    void shouldSupportCalculateWithMap() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder().build();

        CalculationResult res = calculator.calculateWith("a * b + c", Map.of(
                "a", new BigDecimal("7"),
                "b", new BigDecimal("6"),
                "c", new BigDecimal("8")));
        Assertions.assertEquals(new BigDecimal("50.0000"), res.value());
    }

    @Test
    @DisplayName("Should validate errors and throw proper exceptions")
    void shouldValidateErrorsAndThrowProperExceptions() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder().build();

        // Division by zero
        Assertions.assertThrows(DivisionByZeroException.class, () -> calculator.calculate("10 / 0"));

        // Missing operand
        Assertions.assertThrows(OperandNotFoundException.class, () -> calculator.calculate("nonexistent + 1"));

        // Comma outside parentheses
        Assertions.assertThrows(InvalidExpressionException.class, () -> calculator.calculate("1, 2"));

        // Unclosed bracket
        Assertions.assertThrows(InvalidExpressionException.class, () -> calculator.calculate("[Unclosed_Data + 1"));

        // Unknown function
        Assertions.assertThrows(InvalidExpressionException.class, () -> calculator.calculate("unknownFunc(1, 2)"));

        // Insufficient arguments for function
        Assertions.assertThrows(InvalidExpressionException.class, () -> calculator.calculate("min(5)"));

        // Clamp min > max error
        Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate("clamp(5, 10, 2)"));
    }

    @Test
    @DisplayName("Should respect security limits")
    void shouldRespectSecurityLimits() {
        CalculatorConfig shortConfig = new CalculatorConfig(5, 100, 10);
        ExpressionCalculator calculator = new ExpressionCalculator.Builder()
                .config(shortConfig)
                .build();

        Assertions.assertThrows(InvalidExpressionException.class,
                () -> calculator.calculate("100+100", null));

        CalculatorConfig fewTokens = new CalculatorConfig(100, 2, 10);
        ExpressionCalculator calculator2 = new ExpressionCalculator.Builder()
                .config(fewTokens)
                .build();
        Assertions.assertThrows(InvalidExpressionException.class,
                () -> calculator2.calculate("1+2+3", null));

        CalculatorConfig shallow = new CalculatorConfig(100, 100, 1);
        ExpressionCalculator calculator3 = new ExpressionCalculator.Builder()
                .config(shallow)
                .build();
        Assertions.assertThrows(InvalidExpressionException.class,
                () -> calculator3.calculate("(1+(2))", null));
    }
}
