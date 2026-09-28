package dev.yewintnaing.expreval;

import dev.yewintnaing.expreval.core.CalculationResult;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for scientific calculator specific functions and features.
 */
class ScientificCalculatorFeaturesTest {

    @Test
    @DisplayName("Should evaluate factorials, permutations, and combinations")
    void shouldEvaluateCombinatorics() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder().build();

        // Factorials
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("fact(0)").value());
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("fact(1)").value());
        Assertions.assertEquals(new BigDecimal("120.0000"), calculator.calculate("fact(5)").value());
        Assertions.assertEquals(new BigDecimal("3628800.0000"), calculator.calculate("fact(10)").value());

        // Permutations nPr
        Assertions.assertEquals(new BigDecimal("20.0000"), calculator.calculate("npr(5, 2)").value());
        Assertions.assertEquals(new BigDecimal("120.0000"), calculator.calculate("npr(5, 5)").value());
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("npr(5, 0)").value());

        // Combinations nCr
        Assertions.assertEquals(new BigDecimal("10.0000"), calculator.calculate("ncr(5, 2)").value());
        Assertions.assertEquals(new BigDecimal("120.0000"), calculator.calculate("ncr(10, 3)").value());
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("ncr(5, 5)").value());
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("ncr(5, 0)").value());

        // Error cases
        Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate("fact(-1)"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate("fact(2.5)"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate("npr(3, 5)"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate("ncr(3, 5)"));
    }

    @Test
    @DisplayName("Should evaluate trigonometric functions in degrees and angle conversions")
    void shouldEvaluateDegreesTrigonometry() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder().build();

        // Degree trig functions
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("sind(90)").value());
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("cosd(0)").value());
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("round(tand(45), 4)").value());
        Assertions.assertEquals(new BigDecimal("90.0000"), calculator.calculate("asind(1)").value());
        Assertions.assertEquals(new BigDecimal("0.0000"), calculator.calculate("acosd(1)").value());
        Assertions.assertEquals(new BigDecimal("45.0000"), calculator.calculate("round(atand(1), 4)").value());

        // Angle conversions
        Assertions.assertEquals(new BigDecimal("180.0000"), calculator.calculate("round(deg(pi), 4)").value());
        Assertions.assertEquals(new BigDecimal("180.0000"), calculator.calculate("round(toDegrees(pi), 4)").value());
        Assertions.assertEquals(new BigDecimal("3.1416"), calculator.calculate("round(rad(180), 4)").value());
        Assertions.assertEquals(new BigDecimal("3.1416"), calculator.calculate("round(toRadians(180), 4)").value());
    }

    @Test
    @DisplayName("Should evaluate hyperbolic functions, roots, and hypotenuse")
    void shouldEvaluateHyperbolicAndRoots() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder().build();

        // Hyperbolic functions
        Assertions.assertEquals(new BigDecimal("0.0000"), calculator.calculate("sinh(0)").value());
        Assertions.assertEquals(new BigDecimal("1.0000"), calculator.calculate("cosh(0)").value());
        Assertions.assertEquals(new BigDecimal("0.0000"), calculator.calculate("tanh(0)").value());

        // Cube root and arbitrary root
        Assertions.assertEquals(new BigDecimal("3.0000"), calculator.calculate("cbrt(27)").value());
        Assertions.assertEquals(new BigDecimal("2.0000"), calculator.calculate("root(16, 4)").value());
        Assertions.assertEquals(new BigDecimal("5.0000"), calculator.calculate("hypot(3, 4)").value());

        // Log base 2
        Assertions.assertEquals(new BigDecimal("10.0000"), calculator.calculate("log2(1024)").value());
    }

    @Test
    @DisplayName("Should track and chain previous calculation answers via ans")
    void shouldTrackPreviousAnswerViaAns() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder()
                .autoSave(true)
                .build();

        // If ans is referenced without prior calculation, it defaults to 0
        CalculationResult first = calculator.calculate("ans + 10");
        Assertions.assertEquals(new BigDecimal("10.0000"), first.value());

        // Chain with ans
        CalculationResult second = calculator.calculate("ans * 3");
        Assertions.assertEquals(new BigDecimal("30.0000"), second.value());

        // Chain again
        CalculationResult third = calculator.calculate("sqrt(ans + 6)");
        Assertions.assertEquals(new BigDecimal("6.0000"), third.value());

        // Case insensitivity
        CalculationResult fourth = calculator.calculate("ANS / 2");
        Assertions.assertEquals(new BigDecimal("3.0000"), fourth.value());
    }
}
