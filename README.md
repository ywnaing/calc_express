# Calculation Math Expression
calc_express is to calculate the dynamic mathematical expressions string in java.


## Usage
Firstly, create the CalcManager using Builder Class.

        CalcManager calcManager = new CalcManager.CalcManagerBuilder()
                .build();

Default does not save in the storage after calculation. You can enable in builder as below.
        
        CalcManager calcManager = new CalcManager.CalcManagerBuilder()
                .enableSaveInStorage(true)
                .build();

        var exp = "a+b*(b-c)^2";

For the above exp, value of a, b, c need to be added in the storage.
        
        calcManager.getOperandStorage().add(new Operand("a", new BigDecimal("200")));
        calcManager.getOperandStorage().add(new Operand("b", new BigDecimal("100")));
        calcManager.getOperandStorage().add(new Operand("c", new BigDecimal(50)));

Final step is to call the execute method.
    
        var operand = calcManager.execute(exp, "Result");
        // value = operand.value()
        // key = operand.key()

## License

```
Copyright 2022 Ye Wint Naing

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


  


