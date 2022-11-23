package mm.com.ywn;

import java.util.List;

public interface ICalcExpress {

    Operand calculate(List<String> data, IOperandStorage operandStorage, String key);
}
