package mm.com.ywn;

import java.util.List;

public interface IOperandStorage {

    void add(Operand operand);

    Operand get(String key);

    List<Operand> getAll();

}
