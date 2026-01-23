# ExprEval

ExprEval is a lightweight, high-performance mathematical expression evaluation library for Java 23+. It supports basic arithmetic, functions, and named operands with configurable security limits.

## Features

- **Java 23+**: Uses modern language features like records and switch expressions.
- **Reverse Polish Notation (RPN)**: Robust evaluation engine.
- **Named Operands**: Use variables in your expressions.
- **Math Functions**: Built-in support for `sqrt`, `abs`, `neg`, `round`, `floor`, `ceil`, and `pow10`.
- **Security Limits**: Defend against DoS with limits on expression length, token count, and nesting depth.
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
        <version>1.0.0</version>
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
    implementation 'com.github.jdev-yewintnaing:calc_express:1.0.0'
}
```

## Quick Start

```java
// Initialize the calculator
ExpressionCalculator calculator = new ExpressionCalculator.Builder()
        .autoSave(true)
        .build();

// Add some data to storage
calculator.getStorage().add(new Operand("tax_rate", new BigDecimal("0.15")));
calculator.getStorage().add(new Operand("price", new BigDecimal("100")));

// Calculate
CalculationResult result = calculator.calculate("price * (1 + tax_rate)", "total_price");

System.out.println("Result: " + result.value()); // 115.0000
System.out.println("Execution time: " + result.durationMs() + "ms");
```

## Security

ExprEval allows you to configure security limits to prevent resource exhaustion:

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

## Mathematical Functions

| Function | Description | Example |
|---|---|---|
| `sqrt(x)` | Square root | `sqrt(25)` -> 5 |
| `abs(x)` | Absolute value | `abs(-10)` -> 10 |
| `neg(x)` | Negation | `neg(5)` -> -5 |
| `round(x)` | Round to nearest integer | `round(2.6)` -> 3 |
| `floor(x)` | Round down | `floor(2.9)` -> 2 |
| `ceil(x)` | Round up | `ceil(2.1)` -> 3 |
| `pow10(x)`| Power of 10 | `pow10(2)` -> 100 |

## License

MIT
