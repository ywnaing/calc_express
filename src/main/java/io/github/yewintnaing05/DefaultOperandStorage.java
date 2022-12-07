package io.github.yewintnaing05;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DefaultOperandStorage implements IOperandStorage {

    private static final HashMap<String, BigDecimal> OPERAND_STORAGE = new HashMap<>();


    @Override
    public void add(Operand operand) {
        if (operand == null) {
            throw new IllegalArgumentException("Operand is null!");
        }

        OPERAND_STORAGE.put(operand.key(), operand.value());
    }

    @Override
    public Operand get(String key) {

        if (!OPERAND_STORAGE.containsKey(key)) {
            throw new IllegalArgumentException("key doesn't exist!");
        }

        return new Operand(key, OPERAND_STORAGE.get(key));

    }

    @Override
    public List<Operand> getAll() {

        var operandList = new ArrayList<Operand>();

        for (Map.Entry<String, BigDecimal> entry : OPERAND_STORAGE.entrySet()) {
            operandList.add(new Operand(entry.getKey(), entry.getValue()));
        }

        return operandList;
    }
}
