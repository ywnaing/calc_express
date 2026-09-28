package dev.yewintnaing.expreval;

import dev.yewintnaing.expreval.core.CalculationResult;
import dev.yewintnaing.expreval.core.Operand;
import dev.yewintnaing.expreval.core.RpnExpressionEvaluator;
import dev.yewintnaing.expreval.exception.ExpressionException;
import dev.yewintnaing.expreval.function.FunctionRegistry;
import dev.yewintnaing.expreval.function.MathFunction;
import dev.yewintnaing.expreval.storage.InMemoryOperandStorage;
import dev.yewintnaing.expreval.storage.MapOperandStorage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExpressionCalculatorExtendedTest {

    @Test
    @DisplayName("Should test MapOperandStorage all operations")
    void shouldTestMapOperandStorage() {
        InMemoryOperandStorage parent = new InMemoryOperandStorage();
        parent.add(new Operand("parent_x", BigDecimal.TEN));

        MapOperandStorage storage = new MapOperandStorage(Map.of("x", BigDecimal.ONE), parent);

        Assertions.assertTrue(storage.contains("x"));
        Assertions.assertTrue(storage.contains("parent_x"));
        Assertions.assertFalse(storage.contains("missing"));
        Assertions.assertFalse(storage.contains(null));

        Assertions.assertEquals(BigDecimal.ONE, storage.get("x").get().value());
        Assertions.assertEquals(BigDecimal.TEN, storage.get("parent_x").get().value());
        Assertions.assertTrue(storage.get(null).isEmpty());
        Assertions.assertTrue(storage.get("missing").isEmpty());

        storage.add(new Operand("y", BigDecimal.valueOf(2)));
        Assertions.assertTrue(storage.contains("y"));

        Assertions.assertEquals(3, storage.getAll().size());

        storage.remove("y");
        storage.remove(null);
        Assertions.assertFalse(storage.contains("y"));

        storage.clear();
        Assertions.assertFalse(storage.contains("x"));
        Assertions.assertTrue(storage.contains("parent_x")); // parent remains

        Assertions.assertThrows(IllegalArgumentException.class, () -> storage.add(null));
    }

    @Test
    @DisplayName("Should test InMemoryOperandStorage all operations")
    void shouldTestInMemoryOperandStorage() {
        InMemoryOperandStorage storage = new InMemoryOperandStorage();
        storage.add(new Operand("a", BigDecimal.ONE));

        Assertions.assertTrue(storage.contains("a"));
        Assertions.assertFalse(storage.contains("b"));
        Assertions.assertFalse(storage.contains(null));
        Assertions.assertTrue(storage.get(null).isEmpty());

        Assertions.assertEquals(1, storage.getAll().size());

        storage.remove("a");
        storage.remove(null);
        Assertions.assertFalse(storage.contains("a"));

        storage.add(new Operand("b", BigDecimal.valueOf(2)));
        storage.clear();
        Assertions.assertEquals(0, storage.getAll().size());

        Assertions.assertThrows(IllegalArgumentException.class, () -> storage.add(null));
    }

    @Test
    @DisplayName("Should test FunctionRegistry copy and retrieval")
    void shouldTestFunctionRegistry() {
        FunctionRegistry registry = FunctionRegistry.createDefault();
        Assertions.assertTrue(registry.contains("sqrt"));
        Assertions.assertTrue(registry.contains("MIN"));
        Assertions.assertFalse(registry.contains(null));
        Assertions.assertTrue(registry.get(null).isEmpty());
        Assertions.assertFalse(registry.getAll().isEmpty());

        FunctionRegistry copied = registry.copy();
        Assertions.assertTrue(copied.contains("sqrt"));

        Assertions.assertThrows(NullPointerException.class, () -> registry.register(null));
    }

    @Test
    @DisplayName("Should test CompiledExpression evaluation variants")
    void shouldTestCompiledExpressionVariants() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder()
                .autoSave(true)
                .build();

        calculator.getStorage().add(new Operand("x", new BigDecimal("5")));

        CompiledExpression compiled = calculator.compile("x * 2");
        Assertions.assertEquals("x * 2", compiled.getExpression());

        CalculationResult resDefault = compiled.evaluate();
        Assertions.assertEquals(new BigDecimal("10.0000"), resDefault.value());
        Assertions.assertEquals("Result", resDefault.key());

        CalculationResult resKey = compiled.evaluate("custom_key");
        Assertions.assertEquals("custom_key", resKey.key());

        CalculationResult resStorage = compiled.evaluate(calculator.getStorage(), "res_storage");
        Assertions.assertEquals(new BigDecimal("10.0000"), resStorage.value());

        CalculationResult resMap = compiled.evaluateWith(Map.of("x", new BigDecimal("7")));
        Assertions.assertEquals(new BigDecimal("14.0000"), resMap.value());

        CalculationResult resMapKey = compiled.evaluateWith(Map.of("x", new BigDecimal("8")), "map_key");
        Assertions.assertEquals("map_key", resMapKey.key());
        Assertions.assertEquals(new BigDecimal("16.0000"), resMapKey.value());
    }

    @Test
    @DisplayName("Should test remaining trigonometric and math functions")
    void shouldTestRemainingMathFunctions() {
        ExpressionCalculator calculator = new ExpressionCalculator.Builder().build();

        // asin, acos, atan, atan2
        Assertions.assertEquals(new BigDecimal("0.0000"), calculator.calculate("round(asin(0), 4)").value());
        Assertions.assertEquals(new BigDecimal("0.0000"), calculator.calculate("round(atan(0), 4)").value());
        Assertions.assertEquals(new BigDecimal("0.0000"), calculator.calculate("round(atan2(0, 1), 4)").value());

        // pow with negative or non-integer exponent
        CalculationResult powRes = calculator.calculate("pow(4, 0.5)");
        Assertions.assertEquals(new BigDecimal("2.0000"), powRes.value());
    }

    @Test
    @DisplayName("Should test ExpressionCalculator builder options")
    void shouldTestExpressionCalculatorBuilderOptions() {
        FunctionRegistry registry = FunctionRegistry.createDefault();
        registry.register(MathFunction.unary("triple", v -> v.multiply(new BigDecimal("3"))));

        RpnExpressionEvaluator evaluator = new RpnExpressionEvaluator(2, RoundingMode.DOWN, registry);
        InMemoryOperandStorage storage = new InMemoryOperandStorage();

        ExpressionCalculator calculator = new ExpressionCalculator.Builder()
                .functionRegistry(registry)
                .evaluator(evaluator)
                .storage(storage)
                .build();

        Assertions.assertSame(registry, calculator.getFunctionRegistry());
        Assertions.assertSame(storage, calculator.getStorage());

        CalculationResult res = calculator.calculate("triple(10)");
        Assertions.assertEquals(new BigDecimal("30.00"), res.value());
    }

    @Test
    @DisplayName("Should test ExpressionException with cause")
    void shouldTestExpressionExceptionWithCause() {
        IllegalArgumentException cause = new IllegalArgumentException("Root cause");
        ExpressionException ex = new ExpressionException("Test error", cause);
        Assertions.assertEquals("Test error", ex.getMessage());
        Assertions.assertSame(cause, ex.getCause());
    }

    @Test
    @DisplayName("Should run Main method cleanly")
    void shouldRunMainMethod() {
        Assertions.assertDoesNotThrow(() -> Main.main(new String[0]));
    }
}
