package dev.yewintnaing.expreval.function;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe registry for mathematical functions used during expression parsing and evaluation.
 */
public class FunctionRegistry {

    private final Map<String, MathFunction> functions = new ConcurrentHashMap<>();

    /**
     * Constructs an empty FunctionRegistry.
     */
    public FunctionRegistry() {
        // Default constructor
    }

    /**
     * Creates a FunctionRegistry pre-populated with standard mathematical functions.
     *
     * @return a default FunctionRegistry instance
     */
    public static FunctionRegistry createDefault() {
        FunctionRegistry registry = new FunctionRegistry();
        StandardMathFunctions.registerAll(registry);
        return registry;
    }

    /**
     * Registers a mathematical function.
     *
     * @param function the function to register
     */
    public void register(MathFunction function) {
        Objects.requireNonNull(function, "Function cannot be null");
        functions.put(function.getName().toLowerCase(Locale.ROOT), function);
    }

    /**
     * Retrieves a function by name (case-insensitive).
     *
     * @param name the function name
     * @return an Optional containing the function if found, or empty otherwise
     */
    public Optional<MathFunction> get(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(functions.get(name.toLowerCase(Locale.ROOT)));
    }

    /**
     * Checks if a function with the given name is registered.
     *
     * @param name the function name
     * @return true if registered, false otherwise
     */
    public boolean contains(String name) {
        if (name == null) {
            return false;
        }
        return functions.containsKey(name.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns an unmodifiable view of all registered functions.
     *
     * @return map of function names to MathFunction instances
     */
    public Map<String, MathFunction> getAll() {
        return Collections.unmodifiableMap(functions);
    }

    /**
     * Creates a shallow copy of this registry.
     *
     * @return a new FunctionRegistry with the same functions registered
     */
    public FunctionRegistry copy() {
        FunctionRegistry copy = new FunctionRegistry();
        copy.functions.putAll(this.functions);
        return copy;
    }
}
