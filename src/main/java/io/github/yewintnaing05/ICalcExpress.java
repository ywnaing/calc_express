package io.github.yewintnaing05;

import java.util.List;

public interface ICalcExpress {

    Operand calculate(List<String> data, IOperandStorage operandStorage, String key);
}
