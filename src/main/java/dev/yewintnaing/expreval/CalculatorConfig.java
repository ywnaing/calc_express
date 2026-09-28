package dev.yewintnaing.expreval;

/**
 * Configuration for ExpressionCalculator, including security limits.
 *
 * @param maxExpressionLength Maximum allowed length of the expression string
 * @param maxTokenCount       Maximum allowed number of tokens in the expression
 * @param maxDepth            Maximum allowed nesting depth (parentheses)
 */
public record CalculatorConfig(
        int maxExpressionLength,
        int maxTokenCount,
        int maxDepth) {

    /** Default configuration with length 1000, 100 tokens, and depth 10. */
    public static final CalculatorConfig DEFAULT = new CalculatorConfig(1000, 100, 10);

    /**
     * Validates configuration parameters.
     *
     * @param maxExpressionLength Maximum allowed length of the expression string
     * @param maxTokenCount       Maximum allowed number of tokens in the expression
     * @param maxDepth            Maximum allowed nesting depth (parentheses)
     */
    public CalculatorConfig {
        if (maxExpressionLength <= 0) {
            throw new IllegalArgumentException("maxExpressionLength must be positive");
        }
        if (maxTokenCount <= 0) {
            throw new IllegalArgumentException("maxTokenCount must be positive");
        }
        if (maxDepth <= 0) {
            throw new IllegalArgumentException("maxDepth must be positive");
        }
    }
}
