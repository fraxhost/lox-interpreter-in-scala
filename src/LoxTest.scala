//> using dep "org.scalameta::munit:1.2.1"

import munit.FunSuite

class LoxTest extends FunSuite {

  // Helper to capture the output of Lox.run
  def runLox(code: String): String = {
    val out = new java.io.ByteArrayOutputStream()
    val err = new java.io.ByteArrayOutputStream()
    Console.withOut(out) {
      Console.withErr(err) {
        Lox.run(code)
      }
    }
    out.toString.trim
  }

  // ------------------ Expressions & Arithmetic ------------------

  test("simple addition") {
    val code =
      """
      print 1 + 2;
      """
    assertEquals(runLox(code), "3")
  }

  test("subtraction") {
    val code =
      """
      print 5 - 3;
      """
    assertEquals(runLox(code), "2")
  }

  test("multiplication") {
    val code =
      """
      print 4 * 6;
      """
    assertEquals(runLox(code), "24")
  }

  test("division") {
    val code =
      """
      print 20 / 5;
      """
    assertEquals(runLox(code), "4")
  }

  test("grouping") {
    val code =
      """
      print (2 + 3) * 4;
      """
    assertEquals(runLox(code), "20")
  }

  test("negative number") {
    val code =
      """
      print -10 + 5;
      """
    assertEquals(runLox(code), "-5")
  }

  test("boolean literal") {
    val code =
      """
      print true;
      """
    assertEquals(runLox(code), "true")
  }

  test("boolean negation") {
    val code =
      """
      print !false;
      """
    assertEquals(runLox(code), "true")
  }

  test("equality") {
    val code =
      """
      print 10 == 10;
      """
    assertEquals(runLox(code), "true")
  }

  test("inequality") {
    val code =
      """
      print 10 != 5;
      """
    assertEquals(runLox(code), "true")
  }

  // ------------------ Variables ------------------

  test("variable declaration") {
    val code =
      """
      var a = 10;
      print a;
      """
    assertEquals(runLox(code), "10")
  }

  test("variable update") {
    val code =
      """
      var a = 5;
      a = 7;
      print a;
      """
    assertEquals(runLox(code), "7")
  }

  test("variable with expression") {
    val code =
      """
      var x = 3 + 4;
      print x;
      """
    assertEquals(runLox(code), "7")
  }

  test("multiple variables") {
    val code =
      """
      var a=1;
      var b=2;
      print a+b;
      """
    assertEquals(runLox(code), "3")
  }

  test("expression using variables") {
    val code =
      """
      var a=10;
      var b=20;
      print a+b*2;
      """
    assertEquals(runLox(code), "50")
  }

  test("variable shadowing") {
    val code =
      """
      var a=5;
      {
        var a=10;
        print a;
      }
      print a;
      """
    assertEquals(runLox(code), "10\n5")
  }

  // ------------------ Control Flow ------------------

  test("simple if") {
    val code =
      """
      if (true) print 1;
      """
    assertEquals(runLox(code), "1")
  }

  test("if-else") {
    val code =
      """
      if (false) print 1; else print 2;
      """
    assertEquals(runLox(code), "2")
  }

  test("nested if") {
    val code =
      """
      if (true) {
        if(false) print 1; else print 2;
      }
      """
    assertEquals(runLox(code), "2")
  }

  test("comparison in if") {
    val code =
      """
      if (3 < 5) print 10;
      """
    assertEquals(runLox(code), "10")
  }

  test("logical AND in if") {
    val code =
      """
      if (2<3 && 4>1) print 100;
      """
    assertEquals(runLox(code), "100")
  }

  test("logical OR in if") {
    val code =
      """
      if (false || true) print 50;
      """
    assertEquals(runLox(code), "50")
  }

  // ------------------ Loops ------------------

  test("simple while loop") {
    val code =
      """
      var i=0;
      while(i<3){
        print i;
        i=i+1;
      }
      """
    assertEquals(runLox(code), "0\n1\n2")
  }

  test("while with accumulation") {
    val code =
      """
      var sum=0;
      var i=1;
      while(i<=3){
        sum=sum+i;
        i=i+1;
      }
      print sum;
      """
    assertEquals(runLox(code), "6")
  }

  // ------------------ Functions ------------------

  test("simple function add") {
    val code =
      """
      fun add(a,b){ return a+b; }
      print add(2,3);
      """
    assertEquals(runLox(code), "5")
  }

  test("recursive function factorial") {
    val code =
      """
      fun fact(n){
        if(n==0) return 1;
        return n * fact(n-1);
      }
      print fact(5);
      """
    assertEquals(runLox(code), "120")
  }

  test("function shadowing") {
    val code =
      """
      var a=5;
      fun f(a){ return a+1; }
      print f(10);
      """
    assertEquals(runLox(code), "11")
  }

  test("function returning boolean") {
    val code =
      """
      fun isNumberTwo(n){ return n == 2; }
      print isNumberTwo(2);
      """
    assertEquals(runLox(code), "true")
  }

  // ------------------ Classes & Inheritance ------------------

  test("simple class creation") {
    val code =
      """
      class Foo {}
      var f = Foo();
      print f;
      """
    assertEquals(runLox(code), "Foo instance")
  }

  test("class with method") {
    val code =
      """
      class Foo { fun bar() { print 123; } }
      var f = Foo();
      f.bar();
      """
    assertEquals(runLox(code), "123")
  }

  test("field access in class") {
    val code =
      """
      class Foo { init(x) { this.x = x; } show() { print this.x; } } 
      var f = Foo(10); 
      f.show();
      """
    assertEquals(runLox(code), "10")
  }

  test("field update in class") {
    val code =
      """
      class Foo { init() { this.x = 5; } } 
      var f = Foo(); 
      f.x = 20; 
      print f.x;
      """
    assertEquals(runLox(code), "20")
  }

  test("this keyword in class") {
    val code =
      """
      class Foo { init() { this.x = 5; } get() { print this.x; } }
      var f = Foo();
      f.get();
      """
    assertEquals(runLox(code), "5")
  }

  test("super call with inheritance") {
    val code =
      """
      class A { a() { print 1; } }
      class B < A { a() { super.a(); print 2; } }
      var b = B();
      b.a();
      """
    assertEquals(runLox(code), "1\n2")
  }

  test("multi-level inheritance") {
    val code =
      """
      class A { a() { print 1; } }
      class B < A { b() { print 2; } }
      class C < B {}
      var c = C();
      c.a();
      c.b();
      """
    assertEquals(runLox(code), "1\n2")
  }

  test("multiple operation on class fields") {
    val code =
      """
      class Counter {
        init() {
          this.value = 0;
        }

        increment() {
          this.value = this.value + 1;
        }

        decrement() {
          this.value = this.value - 1;
        }

        show() {
          print this.value;
        }
      }

      var c = Counter();
      c.init();
      c.increment();
      c.increment();
      c.decrement();
      c.show();
      """
    assertEquals(runLox(code), "1")
  }

  test("function and class interaction") {
    val code =
      """
      class Foo { x=5; }
      fun getX(f) { return f.x; }
      var f = Foo();
      print getX(f);
      """
    assertEquals(runLox(code), "5")
  }

  test("full inheritance test") {
    val code =
      """
      class A { a(){ print 1; } }
      class B < A { a(){ super.a(); print 2; } }
      class C < B {}
      var c = C();
      c.a();
      """
    assertEquals(runLox(code), "1\n2")
  }

}
