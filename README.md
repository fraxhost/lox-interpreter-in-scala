# Lox Interpreter in Scala

This project is a Scala implementation of the **Lox programming language**, following chapters 4–13 of *Crafting Interpreters* by Robert Nystrom. It supports **parsing, interpreting, and running Lox programs**, including classes, functions, inheritance, and closures.

---

## Features

- **Lexical scanning**: Tokenizes Lox source code.
- **Parsing**: Builds an abstract syntax tree (AST) for Lox expressions and statements.
- **Evaluation / Interpretation**: Supports:
  - Variables and block scopes
  - Functions and closures
  - Classes and inheritance
  - Control flow (`if`, `while`, `for`)
  - Logical expressions, operators, and literals
  - Error handling
- **Interactive REPL**: Run Lox programs interactively.
- **File execution**: Run `.lox` source files.
- **Unit testing**: Test your interpreter with Lox test scripts.

---

## Requirements

- [Scala CLI](https://scala-cli.virtuslab.org/)
- Java 8 or higher

---

## Running the Interpreter

Move to the root directory.

### Interactive Mode (REPL)

```bash
scala-cli src/*.scala --main-class Lox
```

You can type Lox statements interactively.

### Run Lox File

```bash
scala-cli src/*.scala --main-class Lox -- tests/classes.lox
```

Replace `tests/classes.lox` with your Lox source file. Don't forget to give the proper file path with the file name relative to the root folder.

### Run Tests and Print Results to Console

```bash
scala-cli test src
```

### Run Tests and Print Results to Text File

```bash
scala-cli test --color never src 2>&1 | sed -E "s/\x1b\[[0-9;]*m//g" | grep -v "WARNING" > clean_test_results.txt
```

This runs the test suite for our Lox interpreter.

---

## Project Structure

```
.
├── src/
│   ├── Lox.scala          # Main entry point
│   ├── LoxTest.scala      # Test suite
│   ├── Scanner.scala      # Lexical analyzer
│   ├── Parser.scala       # Parser
│   ├── Expr.scala         # Expression AST nodes
│   ├── Stmt.scala         # Statement AST nodes
│   ├── Interpreter.scala  # AST interpreter
│   ├── Resolver.scala     # Variable resolution
│   └── Token.scala        # Tokens and TokenType
│   └── TokenType.scala    # TokenType
│   └── Environment.scala  # Manages variable scopes, bindings, and closures
│   └── LoxCallable.scala  # Trait for callable entities (functions, classes)
│   └── LoxClass.scala     # Runtime representation of Lox classes
│   └── LoxFunction.scala  # Runtime representation of Lox functions
│   └── LoxInstance.scala  # Represents an instance of a Lox class (fields, methods)
│   └── Return.scala       # Special exception used to handle function returns.
│   └── RuntimeError.scala # Exception type for runtime errors (used by Interpreter)
└── tests/
    └── *.lox              # Lox test files
```

---

## Learning Reference

This interpreter follows the book **[Crafting Interpreters](https://craftinginterpreters.com/)**:

- Chapters 4–13:

  - Expressions, Statements
  - Variables, Functions, and Closures
  - Classes, Inheritance, and Methods
  - Control Flow
  - Error Reporting

---

## License

This project is for **educational purposes** and is inspired by *Crafting Interpreters* by Robert Nystrom.

---

## Author

**Ahmed Ryan**
Scala implementation of the Lox language (chapters 3–13)
