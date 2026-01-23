package dev.yewintnaing.expreval.storage;

import dev.yewintnaing.expreval.core.Operand;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An in-memory, thread-safe implementation of OperandStorage.
 */
public class InMemoryOperandStorage implements OperandStorage {

    private final Map<String, BigDecimal> storage = new ConcurrentHashMap<>();

    @Override
    public void add(Operand operand) {
        if (operand == null) {
            throw new IllegalArgumentException("Operand cannot be null");
        }
        storage.put(operand.key(), operand.value());
    }

    @Override
    public Optional<Operand> get(String key) {
        if (key == null) {
            return Optional.empty();
        }
        BigDecimal value = storage.get(key);
        return value != null ? Optional.of(new Operand(key, value)) : Optional.empty();
    }

    @Override
    public List<Operand> getAll() {
        List<Operand> operandList = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : storage.entrySet()) {
            operandList.add(new Operand(entry.getKey(), entry.getValue()));
        }
        return operandList;
    }

    @Override
    public boolean contains(String key) {
        return key != null && storage.containsKey(key);
    }

    @Override
    public void remove(String key) {
        if (key != null) {
            storage.remove(key);
        }
    }

    @Override
    public void clear() {
        storage.clear();
    }
}
