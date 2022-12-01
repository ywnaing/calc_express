# Calculation Math Expression
calc_express is to calculate the dynamic mathematical expressions string in java.

## Expression Rule
- Variable must be between [ and ]. Eg. var exp = "[a]+[b]";
- for the digit, we need to add $ in the end. Eg. var exp = "[a]*2$"

## Usage
First is we need to create the CalcManager using Builder Class.

        CalcManager calcManager = new CalcManager.CalcManagerBuilder()
                .build();

Default does not save in the storage after calculation. You can enable in builder as below.
        
        CalcManager calcManager = new CalcManager.CalcManagerBuilder()
                .enableSaveInStorage(true)
                .build();

        var exp = "[a]+[b]*([b]-[c])^2$";

For the above exp, value of a, b, c need to be added in the storage.
        
        calcManager.getOperandStorage().add(new Operand("a", new BigDecimal("200")));
        calcManager.getOperandStorage().add(new Operand("b", new BigDecimal("100")));
        calcManager.getOperandStorage().add(new Operand("c", new BigDecimal(50)));

Final step is to call the execute method.
    
        var operand = calcManager.execute(exp, "Result");
        // value = operand.value()
        // key = operand.key()

  


