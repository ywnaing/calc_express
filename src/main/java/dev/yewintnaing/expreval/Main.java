package dev.yewintnaing.expreval;

import dev.yewintnaing.expreval.core.CalculationResult;
import dev.yewintnaing.expreval.function.MathFunction;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Example entry point showcasing real-world mathematical applications of the ExprEval library.
 */
public final class Main {

    private Main() {
        // Utility main class
    }

    /**
     * Main method running example expression calculations across finance, physics, and statistics.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder()
                .registerFunction(MathFunction.binary("tax", (income, rate) -> income.multiply(rate)))
                .build();

        System.out.println("=================================================");
        System.out.println("   ExprEval - Real-World Mathematical Examples   ");
        System.out.println("=================================================\n");

        // 1. Financial Math: Loan Monthly Payment (Amortization)
        // Formula: M = P * (r * (1 + r)^n) / ((1 + r)^n - 1)
        CompiledExpression loanPayment = calculator.compile(
                "P * (r * pow(1 + r, n)) / (pow(1 + r, n) - 1)");

        CalculationResult mortgage = loanPayment.evaluateWith(Map.of(
                "P", new BigDecimal("300000"), // $300,000 loan principal
                "r", new BigDecimal("0.005"),  // 0.5% monthly interest (6% annual)
                "n", new BigDecimal("360")     // 30 years (360 months)
        ));
        System.out.println("1. Financial Math (Mortgage Payment for $300k @ 6%):");
        System.out.println("   Formula: P * (r * pow(1 + r, n)) / (pow(1 + r, n) - 1)");
        System.out.println("   Monthly Payment: $" + mortgage.value() + "\n");

        // 2. Geometry & Physics: 3D Euclidean Distance
        // Formula: d = sqrt((x2 - x1)^2 + (y2 - y1)^2 + (z2 - z1)^2)
        CompiledExpression distanceFormula = calculator.compile(
                "sqrt((x2 - x1)^2 + (y2 - y1)^2 + (z2 - z1)^2)");

        CalculationResult distance = distanceFormula.evaluateWith(Map.of(
                "x1", new BigDecimal("1"), "y1", new BigDecimal("2"), "z1", new BigDecimal("3"),
                "x2", new BigDecimal("4"), "y2", new BigDecimal("6"), "z2", new BigDecimal("8")
        ));
        System.out.println("2. 3D Euclidean Distance between (1,2,3) and (4,6,8):");
        System.out.println("   Formula: sqrt((x2 - x1)^2 + (y2 - y1)^2 + (z2 - z1)^2)");
        System.out.println("   Distance: " + distance.value() + "\n");

        // 3. Statistics & Science: Gaussian Normal Distribution PDF
        // Formula: f(x) = (1 / (sigma * sqrt(2 * pi))) * exp(neg(0.5) * ((x - mu) / sigma)^2)
        CompiledExpression bellCurve = calculator.compile(
                "(1 / (sigma * sqrt(2 * pi))) * exp(neg(0.5) * ((x - mu) / sigma)^2)");

        CalculationResult probDensity = bellCurve.evaluateWith(Map.of(
                "x", new BigDecimal("1.5"),   // Evaluation point
                "mu", new BigDecimal("0"),    // Mean (center)
                "sigma", new BigDecimal("1")  // Standard deviation
        ));
        System.out.println("3. Statistics: Gaussian Normal Distribution PDF at x = 1.5 (mu=0, sigma=1):");
        System.out.println("   Formula: (1 / (sigma * sqrt(2 * pi))) * exp(neg(0.5) * ((x - mu) / sigma)^2)");
        System.out.println("   Density: " + probDensity.value() + "\n");

        // 4. Business Rules: Bounded Pricing with Custom Function
        CalculationResult priceResult = calculator.calculateWith(
                "clamp(tax(base_price, 0.18) + shipping, 15, 100)",
                Map.of(
                        "base_price", new BigDecimal("50"),
                        "shipping", new BigDecimal("8.50")
                ),
                "final_invoice"
        );
        System.out.println("4. Business Pricing (Tax + Shipping with Clamp bounds):");
        System.out.println("   Formula: clamp(tax(base_price, 0.18) + shipping, 15, 100)");
        System.out.println("   Result: $" + priceResult.value() + "\n");
    }
}
