// File: LoxTest.scala
object LoxTest {
  def main(args: Array[String]): Unit = {
    println("===== Lox Interpreter Test =====")

    // Test 1: Simple arithmetic
    val program1 =
      """
        |print 1 + 2 * 3;
      """.stripMargin
    println("Test 1: Simple arithmetic")
    Lox.run(program1)

    // Test 2: Variable declaration and usage
    val program2 =
      """
        |var x = 10;
        |print x + 5;
      """.stripMargin
    println("Test 2: Variables")
    Lox.run(program2)

    // Test 3: If statement
    val program3 =
      """
        |var y = 20;
        |if (y > 10) {
        |  print "Greater than 10";
        |} else {
        |  print "Less than or equal to 10";
        |}
      """.stripMargin
    println("Test 3: If statement")
    Lox.run(program3)

    // Test 4: While loop
    val program4 =
      """
        |var count = 0;
        |while (count < 3) {
        |  print count;
        |  count = count + 1;
        |}
      """.stripMargin
    println("Test 4: While loop")
    Lox.run(program4)

    // Test 5: Function declaration and call
    val program5 =
      """
        |fun add(a, b) {
        |  return a + b;
        |}
        |print add(5, 7);
      """.stripMargin
    println("Test 5: Function call")
    Lox.run(program5)

    // Test 6: Class and method
    val program6 =
      """
        |class Foo {
        |  greet(name) {
        |    print "Hello, " + name;
        |  }
        |}
        |var f = Foo();
        |f.greet("World");
      """.stripMargin
    println("Test 6: Class and method")
    Lox.run(program6)

    println("===== Tests Completed =====")
  }
}
