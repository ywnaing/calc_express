package mm.com.ywn;

import java.util.List;

public class CalcManager {

    private final boolean isSave;
    private final ICalcExpress calcExpress;
    private final IOperandStorage operandStorage;


    private CalcManager(boolean isSave, ICalcExpress calcExpress, IOperandStorage operandStorage) {
        this.isSave = isSave;
        this.calcExpress = calcExpress;
        this.operandStorage = operandStorage;
    }


    Operand execute(String expression, String outputKey) {
        List<String> postFix = CalcUtility.infixToRpn(expression);

        return calculate(postFix, outputKey);
    }

    public IOperandStorage getOperandStorage() {
        return operandStorage;
    }

    private Operand calculate(List<String> expression, String key) {
        Operand operand = this.calcExpress.calculate(expression, operandStorage, key == null ? "Result" : key);

        if (isSave) {
            this.operandStorage.add(operand);
        }

        return operand;
    }

    public static class CalcManagerBuilder {
        private boolean saveOperandInStorage;
        private ICalcExpress calcExpress;

        private IOperandStorage operandStorage;

        public CalcManagerBuilder() {
            this.saveOperandInStorage = false;
            this.calcExpress = new DefaultCalcExpress();
            this.operandStorage = new DefaultOperandStorage();
        }

        public CalcManagerBuilder enableSaveInStorage(boolean indicator) {
            this.saveOperandInStorage = indicator;
            return this;
        }

        public CalcManagerBuilder setCalcExpress(ICalcExpress calcExpress) {
            this.calcExpress = calcExpress;

            return this;
        }

        public CalcManagerBuilder setOperandStorage(IOperandStorage operandStorage) {
            this.operandStorage = operandStorage;
            return this;
        }

        public CalcManager build() {
            return new CalcManager(saveOperandInStorage, calcExpress, operandStorage);
        }
    }


}
