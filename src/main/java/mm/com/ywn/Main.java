package mm.com.ywn;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {

        CalcManager.CalcManagerBuilder builder = new CalcManager.CalcManagerBuilder();

        CalcManager calcManager = builder
                .enableSaveInStorage(true)
                .build();
        calcManager.getOperandStorage().add(new Operand("Test_Data", new BigDecimal("100")));
        calcManager.getOperandStorage().add(new Operand("Test_Data2", new BigDecimal("1.5")));

        String expression = "[Test_Data]/[Test_Data2]";

        var operand = calcManager.execute(expression, null);

        String ex = "[Test_Data]*100$+([Test_Data2]-20$)^2$*[Result]^2$-1$";


        System.out.println(calcManager.getOperandStorage().getAll());
        var  operand1 = calcManager.execute(ex, "Testing_Result");

        System.out.println(operand1);

    }
}