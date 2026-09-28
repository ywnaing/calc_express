package dev.yewintnaing.expreval.storage;

import dev.yewintnaing.expreval.core.Operand;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An {@link OperandStorage} implementation backed by a transient map of variables,
 * with optional fallback to a parent storage.
 */
public class MapOperandStorage implements OperandStorage {

    private final Map<String, BigDecimal> map;
    private final OperandStorage parent;

    /**
     * Constructs a storage backed by the given map of variables.
     *
     * @param variables map of variable names to values
     */
    public MapOperandStorage(Map<String, BigDecimal> variables) {
        this(variables, null);
    }

    /**
     * Constructs a storage backed by the given map with a fallback parent storage.
     *
     * @param variables map of variable names to values
     * @param parent    fallback parent storage, may be null
     */
    public MapOperandStorage(Map<String, BigDecimal> variables, OperandStorage parent) {
        this.map = variables != null ? new HashMap<>(variables) : new HashMap<>();
        this.parent = parent;
    }

    @Override
    public void add(Operand operand) {
        if (operand == null) {
            throw new IllegalArgumentException("Operand cannot be null");
        }
        map.put(operand.key(), operand.value());
    }

    @Override
    public Optional<Operand> get(String key) {
        if (key == null) {
            return Optional.empty();
        }
        BigDecimal val = map.get(key);
        if (val != null) {
            return Optional.of(new Operand(key, val));
        }
        if (parent != null) {
            return parent.get(key);
        }
        return Optional.empty();
    }

    @Override
    public List<Operand> getAll() {
        List<Operand> result = new ArrayList<>();
        if (parent != null) {
            result.addAll(parent.getAll());
        }
        for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
            result.add(new Operand(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    @Override
    public boolean contains(String key) {
        if (key == null) {
            return false;
        }
        return map.containsKey(key) || (parent != null && parent.contains(key));
    }

    @Override
    public void remove(String key) {
        if (key != null) {
            map.remove(key);
        }
    }

    @Override
    public void clear() {
        map.clear();
    }
}
