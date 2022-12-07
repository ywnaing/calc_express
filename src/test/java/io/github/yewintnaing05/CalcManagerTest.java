package io.github.yewintnaing05;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

class CalcManagerTest {

    @Test
    void execute() {
        var exp = "a+b*(b-c)^2";


        var a = BigDecimal.valueOf(200);
        var b = BigDecimal.valueOf(100);
        var c = BigDecimal.valueOf(50);

        var result = a.add(b.multiply(b.subtract(c).pow(2))).setScale(4, RoundingMode.HALF_EVEN);

        CalcManager calcManager = new CalcManager.CalcManagerBuilder()
                .enableSaveInStorage(true)
                .build();

        calcManager.getOperandStorage().add(new Operand("a", new BigDecimal("200")));
        calcManager.getOperandStorage().add(new Operand("b", new BigDecimal("100")));
        calcManager.getOperandStorage().add(new Operand("c", new BigDecimal(50)));

        var operand = calcManager.execute(exp, "Result");

        System.out.println(result.toPlainString());
        System.out.println(operand.value().toPlainString());

        assert operand.value().equals(result);

    }
}