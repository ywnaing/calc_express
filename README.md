# ExprEval

ExprEval is a lightweight, high-performance mathematical expression evaluation library for Java 17+. It supports arithmetic operations, multi-argument and custom functions, expression pre-compilation, named operands, and configurable security limits.

## Features

- **Java 17+**: Uses modern language features like records, pattern matching, and switch expressions.
- **Reverse Polish Notation (RPN)**: Robust, high-precision evaluation engine backed by `BigDecimal`.
- **Extensible Function Registry**: Register custom unary, binary, ternary, nullary, or variadic functions with custom business logic.
- **Compiled Expressions**: Pre-compile static formulas once (`CompiledExpression`) and evaluate them repeatedly in microseconds across changing variable sets.
- **Multi-Argument & Scientific Math**: Built-in support for `min`, `max`, `pow`, `clamp`, `round`, `log`, `ln`, `exp`, `sin`, `cos`, `tan`, and mathematical constants (`pi`, `e`).
- **Transient Variable Maps**: Directly evaluate expressions against `Map<String, BigDecimal>` without mutating global storage.
- **Security Limits**: Defend against DoS with configurable limits on expression length, token count, and nesting depth.
- **Thread-safe**: Designed for concurrent use.

## Installation

### Maven

Add the JitPack repository and dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.jdev-yewintnaing</groupId>
        <artifactId>calc_express</artifactId>
        <version>1.1.0</version>
    </dependency>
</dependencies>
```

### Gradle

Add the JitPack repository and dependency to your `build.gradle`:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.jdev-yewintnaing:calc_express:1.1.0'
}
```

## Quick Start

### 1. Basic Evaluation with Operand Storage

```java
// Initialize the calculator
ExpressionCalculator calculator = new ExpressionCalculator.Builder()
        .autoSave(true)
        .build();

// Add operands to storage
calculator.getStorage().add(new Operand("tax_rate", new BigDecimal("0.15")));
calculator.getStorage().add(new Operand("price", new BigDecimal("100")));

// Calculate
CalculationResult result = calculator.calculate("price * (1 + tax_rate)", "total_price");

System.out.println("Result: " + result.value()); // 115.0000
System.out.println("Execution time: " + result.durationMs() + "ms");
```

### 2. High-Performance Pre-compiled Expressions

For high-throughput calculations (e.g. batch jobs or streaming rows), compile the expression once and evaluate with transient maps:

```java
ExpressionCalculator calculator = new ExpressionCalculator.Builder().build();

CompiledExpression formula = calculator.compile("price * (1 + tax_rate) - discount");

// Evaluate repeatedly with zero re-parsing overhead
CalculationResult r1 = formula.evaluateWith(Map.of(
    "price", new BigDecimal("100"),
    "tax_rate", new BigDecimal("0.10"),
    "discount", new BigDecimal("5")
));
System.out.println("Order 1: " + r1.value()); // 105.0000

CalculationResult r2 = formula.evaluateWith(Map.of(
    "price", new BigDecimal("250"),
    "tax_rate", new BigDecimal("0.08"),
    "discount", new BigDecimal("20")
));
System.out.println("Order 2: " + r2.value()); // 250.0000
```

### 3. Custom Domain Functions

Register custom functions directly via the builder:

```java
ExpressionCalculator calculator = new ExpressionCalculator.Builder()
    .registerFunction(MathFunction.binary("tax", (income, rate) -> income.multiply(rate)))
    .registerFunction(MathFunction.variadic("sum", 2, args -> 
        args.stream().reduce(BigDecimal.ZERO, BigDecimal::add)))
    .build();

CalculationResult result = calculator.calculate("tax(1000, 0.2) + sum(10, 20, 30)");
System.out.println("Result: " + result.value()); // 260.0000
```

### 4. Scientific Calculator Chaining (`ans`)

Easily chain consecutive calculations like a physical scientific calculator using the built-in `ans` variable:

```java
ExpressionCalculator calculator = new ExpressionCalculator.Builder()
        .autoSave(true)
        .build();

calculator.calculate("10 + 20");          // Result: 30.0000
calculator.calculate("ans * 2");          // Result: 60.0000
calculator.calculate("sqrt(ans + 4)");    // Result: 8.0000
calculator.calculate("fact(5) + ans");    // Result: 128.0000 (120 + 8)
```

## Built-in Mathematical Functions

| Function | Arity | Description | Example |
|---|---|---|---|
| `sqrt(x)` | 1 | Square root | `sqrt(25)` -> 5.0000 |
| `abs(x)` | 1 | Absolute value | `abs(-10)` -> 10.0000 |
| `neg(x)` | 1 | Negation | `neg(5)` -> -5.0000 |
| `min(a, b, ...)` | $\ge 2$ | Minimum of arguments | `min(10, 5, 20)` -> 5.0000 |
| `max(a, b, ...)` | $\ge 2$ | Maximum of arguments | `max(10, 5, 20)` -> 20.0000 |
| `pow(base, exp)` | 2 | Power function | `pow(2, 3)` -> 8.0000 |
| `clamp(val, min, max)` | 3 | Bounds value between min and max | `clamp(15, 0, 10)` -> 10.0000 |
| `round(x)` | 1 | Round to integer | `round(2.6)` -> 3.0000 |
| `round(x, scale)` | 2 | Round to decimal scale | `round(3.14159, 2)` -> 3.1400 |
| `floor(x)` | 1 | Round down | `floor(2.9)` -> 2.0000 |
| `ceil(x)` | 1 | Round up | `ceil(2.1)` -> 3.0000 |
| `signum(x)` | 1 | Signum (-1, 0, 1) | `signum(-45)` -> -1.0000 |
| `log(x)` | 1 | Logarithm base 10 | `log(100)` -> 2.0000 |
| `log(base, x)` | 2 | Logarithm custom base | `log(2, 8)` -> 3.0000 |
| `ln(x)` | 1 | Natural logarithm | `ln(e)` -> 1.0000 |
| `exp(x)` | 1 | Exponential $e^x$ | `exp(0)` -> 1.0000 |
| `sin(x)`, `cos(x)`, `tan(x)` | 1 | Trigonometric functions | `cos(0)` -> 1.0000 |
| `asin(x)`, `acos(x)`, `atan(x)` | 1 | Inverse trigonometric functions | `atan(0)` -> 0.0000 |
| `atan2(y, x)` | 2 | Two-argument arctangent | `atan2(0, 1)` -> 0.0000 |
| `pow10(x)` | 1 | Power of 10 | `pow10(2)` -> 100.0000 |
| `fact(n)` | 1 | Factorial ($n!$) | `fact(5)` -> 120.0000 |
| `npr(n, r)` | 2 | Permutations $P(n, r)$ | `npr(5, 2)` -> 20.0000 |
| `ncr(n, r)` | 2 | Combinations $C(n, r)$ | `ncr(5, 2)` -> 10.0000 |
| `sind(x)`, `cosd(x)`, `tand(x)` | 1 | Trigonometry in degrees | `sind(90)` -> 1.0000 |
| `asind(x)`, `acosd(x)`, `atand(x)` | 1 | Inverse trig in degrees | `atand(1)` -> 45.0000 |
| `deg(rad)`, `toDegrees(rad)` | 1 | Convert radians to degrees | `deg(pi)` -> 180.0000 |
| `rad(deg)`, `toRadians(deg)` | 1 | Convert degrees to radians | `rad(180)` -> 3.1416 |
| `sinh(x)`, `cosh(x)`, `tanh(x)` | 1 | Hyperbolic functions | `cosh(0)` -> 1.0000 |
| `cbrt(x)` | 1 | Cube root | `cbrt(27)` -> 3.0000 |
| `root(x, n)` | 2 | $n$-th root $\sqrt[n]{x}$ | `root(16, 4)` -> 2.0000 |
| `hypot(a, b)` | 2 | Hypotenuse $\sqrt{a^2 + b^2}$ | `hypot(3, 4)` -> 5.0000 |
| `log2(x)` | 1 | Logarithm base 2 | `log2(1024)` -> 10.0000 |
| `pi()`, `pi` | 0 | Mathematical constant $\pi$ | `pi` -> 3.14159... |
| `e()`, `e` | 0 | Mathematical constant $e$ | `e` -> 2.71828... |
| `ans` | Variable | Previous calculation answer | `ans * 2` |

## Real-World Formula Examples

ExprEval easily handles real-world business, scientific, and engineering formulas:

### 1. Financial Engineering: Loan Monthly Payment (Amortization)
Calculate monthly payments with compound interest ($M = P \frac{r(1+r)^n}{(1+r)^n - 1}$):

```java
CompiledExpression loanPayment = calculator.compile(
    "P * (r * pow(1 + r, n)) / (pow(1 + r, n) - 1)");

CalculationResult mortgage = loanPayment.evaluateWith(Map.of(
    "P", new BigDecimal("300000"), // $300,000 principal
    "r", new BigDecimal("0.005"),  // 0.5% monthly interest (6% annual)
    "n", new BigDecimal("360")     // 30 years (360 months)
));
System.out.println("Monthly Payment: $" + mortgage.value()); // $1798.6516
```

### 2. Geometry & Physics: 3D Euclidean Distance
Compute Euclidean distance between two 3D coordinates ($d = \sqrt{(x_2 - x_1)^2 + (y_2 - y_1)^2 + (z_2 - z_1)^2}$):

```java
CalculationResult distance = calculator.calculateWith(
    "sqrt((x2 - x1)^2 + (y2 - y1)^2 + (z2 - z1)^2)",
    Map.of(
        "x1", new BigDecimal("1"), "y1", new BigDecimal("2"), "z1", new BigDecimal("3"),
        "x2", new BigDecimal("4"), "y2", new BigDecimal("6"), "z2", new BigDecimal("8")
    )
);
System.out.println("Distance: " + distance.value()); // 7.0711
```

### 3. Statistics & Science: Gaussian Normal Distribution PDF
Compute the standard normal distribution bell curve ($f(x) = \frac{1}{\sigma \sqrt{2\pi}} e^{-\frac{1}{2}\left(\frac{x - \mu}{\sigma}\right)^2}$):

```java
CompiledExpression bellCurve = calculator.compile(
    "(1 / (sigma * sqrt(2 * pi))) * exp(neg(0.5) * ((x - mu) / sigma)^2)");

CalculationResult density = bellCurve.evaluateWith(Map.of(
    "x", new BigDecimal("1.5"),   // evaluation point
    "mu", new BigDecimal("0"),    // mean
    "sigma", new BigDecimal("1")  // standard deviation
));
System.out.println("Probability Density: " + density.value()); // 0.1295
```

### 4. Bounded E-Commerce Pricing with Custom Functions
Combine custom tax rules with bounding constraints (`clamp`):

```java
CalculationResult invoice = calculator.calculateWith(
    "clamp(tax(base_price, 0.18) + shipping, 15, 100)",
    Map.of(
        "base_price", new BigDecimal("50"),
        "shipping", new BigDecimal("8.50")
    )
);
System.out.println("Invoice Total: $" + invoice.value()); // $17.5000
```

## Security Limits

ExprEval allows configuring limits to defend against denial-of-service from user-supplied formulas:

```java
CalculatorConfig config = new CalculatorConfig(
    500,  // maxExpressionLength
    50,   // maxTokenCount
    5     // maxDepth
);

ExpressionCalculator calculator = new ExpressionCalculator.Builder()
    .config(config)
    .build();
```

## License

MIT
