//> using dep "org.scalameta::munit:1.2.1"

import munit.FunSuite

class LoxTest extends FunSuite {

  // Helper to capture the output of Lox.run
  def runLox(code: String): String = {
    val out = new java.io.ByteArrayOutputStream()
    val err = new java.io.ByteArrayOutputStream()
    Console.withOut(out) {
      Console.withErr(err) {
        Lox.testMode = true
        Lox.run(code)
      }
    }
    out.toString.trim
  }

  // --------------- Expressions & Arithmetic ------------------
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

  test("greater than") {
    val code =
      """
    print 5 > 2;
    """
    assertEquals(runLox(code), "true")
  }

  test("less than") {
    val code =
      """
    print 2 < 5;
    """
    assertEquals(runLox(code), "true")
  }

  test("greater equal") {
    val code =
      """
    print 5 >= 5;
    """
    assertEquals(runLox(code), "true")
  }

  test("less equal") {
    val code =
      """
    print 4 <= 6;
    """
    assertEquals(runLox(code), "true")
  }

  test("multiple operations") {
    val code =
      """
    print 2 + 3 * 4;
    """
    assertEquals(runLox(code), "14")
  }

  test("parentheses priority") {
    val code =
      """
    print (2 + 3) * 4;
    """
    assertEquals(runLox(code), "20")
  }

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

  test("variable shadowing") {
    val code =
      """
    var a=5;
    { var a=10; print a; }
    print a;
    """
    assertEquals(runLox(code), "10\n5")
  }

  test("variable expression") {
    val code =
      """
    var x = 3 + 4;
    print x;
    """
    assertEquals(runLox(code), "7")
  }

  // --------------- Variables & Control Flow ------------------
  test("multiple variables") {
    val code =
      """
    var a=1; var b=2; print a+b;
    """
    assertEquals(runLox(code), "3")
  }

  test("simple if") {
    val code =
      """
    if(true) print 1;
    """
    assertEquals(runLox(code), "1")
  }

  test("if else true") {
    val code =
      """
    if(true) print 1; else print 2;
    """
    assertEquals(runLox(code), "1")
  }

  test("if else false") {
    val code =
      """
    if(false) print 1; else print 2;
    """
    assertEquals(runLox(code), "2")
  }

  test("nested if") {
    val code =
      """
    if(true){ if(false) print 1; else print 2; }
    """
    assertEquals(runLox(code), "2")
  }

  test("comparison in if") {
    val code =
      """
    if(3 < 5) print 10;
    """
    assertEquals(runLox(code), "10")
  }

  test("logical AND") {
    val code =
      """
    if(2<3 and 4>1) print 100;
    """
    assertEquals(runLox(code), "100")
  }

  test("logical OR") {
    val code =
      """
    if(false or true) print 50;
    """
    assertEquals(runLox(code), "50")
  }

  test("simple while loop") {
    val code =
      """
    var i=0; while(i<3){ print i; i=i+1; }
    """
    assertEquals(runLox(code), "0\n1\n2")
  }

  test("while accumulation") {
    val code =
      """
    var sum=0; var i=1; while(i<=3){ sum=sum+i; i=i+1; } print sum;
    """
    assertEquals(runLox(code), "6")
  }

  test("for loop") {
    val code =
      """
    for(var i=0;i<3;i=i+1){ print i; }
    """
    assertEquals(runLox(code), "0\n1\n2")
  }

  test("for accumulation") {
    val code =
      """
    var sum=0; for(var i=1;i<=3;i=i+1){ sum=sum+i; } print sum;
    """
    assertEquals(runLox(code), "6")
  }

  test("break in while") {
    val code =
      """
    var i=0; while(true){ if(i==2) break; print i; i=i+1; }
    """
    // assertEquals(runLox(code), "0\n1")
    intercept[RuntimeError] {
      runLox(code)
    }
  }

  test("continue in while") {
    val code =
      """
    var i=0; while(i<3){ i=i+1; if(i==2) continue; print i; }
    """
    // assertEquals(runLox(code), "1\n3")
    intercept[RuntimeError] {
      runLox(code)
    }
  }

  test("function add") {
    val code =
      """
    fun add(a,b){ return a+b; } print add(2,3);
    """
    assertEquals(runLox(code), "5")
  }

  test("recursive factorial") {
    val code =
      """
    fun fact(n){ if(n==0) return 1; return n*fact(n-1); } print fact(5);
    """
    assertEquals(runLox(code), "120")
  }

  test("function shadowing") {
    val code =
      """
    var a=5; fun f(a){ return a+1; } print f(10);
    """
    assertEquals(runLox(code), "11")
  }

  test("function boolean") {
    val code =
      """
    fun isTwo(n){ return n==2; } print isTwo(2);
    """
    assertEquals(runLox(code), "true")
  }

  test("function with if") {
    val code =
      """
    fun test(x){ if(x>0) return x; return -x; } print test(-3);
    """
    assertEquals(runLox(code), "3")
  }

  test("function without return") {
    val code =
      """
    fun f(){ 5; } print f();
    """
    assertEquals(runLox(code), "nil")
  }

  // ----------------- Functions & Classes -------------------
  test("closure test") {
    val code =
      """
    fun makeAdder(x){ return fun(y){ return x+y; }; } print makeAdder(2)(3);
    """
    // assertEquals(runLox(code), "5")
    intercept[RuntimeError] {
      runLox(code)
    }
  }

  test("recursive Fibonacci") {
    val code =
      """
    fun fib(n){ if(n<=1) return n; return fib(n-1)+fib(n-2); } print fib(5);
    """
    assertEquals(runLox(code), "5")
  }

  test("class empty") {
    val code =
      """
    class Foo {} var f=Foo(); print f;
    """
    assertEquals(runLox(code), "Foo instance")
  }

  test("class field") {
    val code =
      """
    class Foo { init(x){ this.x=x; } show(){ print this.x; } } var f=Foo(10); f.show();
    """
    assertEquals(runLox(code), "10")
  }

  test("class field update") {
    val code =
      """
    class Foo { init(){ this.x=0; } } var f=Foo(); f.x=5; print f.x;
    """
    assertEquals(runLox(code), "5")
  }

  test("this keyword") {
    val code =
      """
    class Foo{ init(){ this.x=5; } get(){ print this.x; } } var f=Foo(); f.get();
    """
    assertEquals(runLox(code), "5")
  }

  test("super call") {
    val code =
      """
    class A{ a(){ print 1; } } class B<A{ a(){ super.a(); print 2; } } var b=B(); b.a();
    """
    assertEquals(runLox(code), "1\n2")
  }

  test("multi-level inheritance") {
    val code =
      """
    class A{ a(){ print 1; } } class B<A{ b(){ print 2; } } class C<B{} var c=C(); c.a(); c.b();
    """
    assertEquals(runLox(code), "1\n2")
  }

  test("multiple class operations") {
    val code =
      """
    class Counter{ init(){ this.v=0; } inc(){ this.v=this.v+1; } dec(){ this.v=this.v-1; } show(){ print this.v; } } var c=Counter(); c.init(); c.inc(); c.inc(); c.dec(); c.show();
    """
    assertEquals(runLox(code), "1")
  }

  test("function-class interaction") {
    val code =
      """
    class Foo{ init() {this.x=5;}} fun getX(f){return f.x;} var f=Foo(); print getX(f);
    """
    assertEquals(runLox(code), "5")
  }

// ----------------- Strings -------------------
  test("string literal") {
    val code =
      """
    print "hello";
    """
    assertEquals(runLox(code), "hello")
  }

  test("string concatenation") {
    val code =
      """
    print "foo"+"bar";
    """
    assertEquals(runLox(code), "foobar")
  }

  test("empty string") {
    val code =
      """
    print "";
    """
    assertEquals(runLox(code), "")
  }

  test("unicode string") {
    val code =
      """
    print "π";
    """
    assertEquals(runLox(code), "π")
  }

  test("string with number") {
    val code =
      """
    print "num"+5;
    """
    // assertEquals(runLox(code), "num5")
    intercept[RuntimeError] {
      runLox(code)
    }
  }

// ----------------- Numbers -------------------
  test("zero addition") {
    val code =
      """
    print 0+0;
    """
    assertEquals(runLox(code), "0")
  }

  test("negative number addition") {
    val code =
      """
    print -1 + -2;
    """
    assertEquals(runLox(code), "-3")
  }

  test("large number") {
    val code =
      """
    print 999999;
    """
    assertEquals(runLox(code), "999999")
  }

  test("small number") {
    val code =
      """
    print -999999;
    """
    assertEquals(runLox(code), "-999999")
  }

  test("float addition") {
    val code =
      """
    print 1.5 + 2.6;
    """
    assertEquals(runLox(code), "4.1")
  }

  // ----------------- Numbers & Boolean -------------------
  test("float multiplication") {
    val code =
      """
    print 2.1*3.2;
    """
    assertEquals(runLox(code), "6.720000000000001")
  }

  test("true AND false") {
    val code =
      """
    print true and false;
    """
    assertEquals(runLox(code), "false")
  }

  test("true OR false") {
    val code =
      """
    print true or false;
    """
    assertEquals(runLox(code), "true")
  }

  test("negation") {
    val code =
      """
    print !true;
    """
    assertEquals(runLox(code), "false")
  }

// ----------------- Arithmetic -------------------
  test("complex arithmetic") {
    val code =
      """
    print (2+3)*(4-1)/5;
    """
    assertEquals(runLox(code), "3")
  }

// ----------------- Blocks & Loops -------------------
  test("nested blocks") {
    val code =
      """
    { var x=1; { var x=2; print x; } print x; }
    """
    assertEquals(runLox(code), "2\n1")
  }

  test("while nested") {
    val code =
      """
    var i=0; while(i<2){ var j=0; while(j<2){ print i+j; j=j+1; } i=i+1; }
    """
    assertEquals(runLox(code), "0\n1\n1\n2")
  }

  test("for nested") {
    val code =
      """
    for(var i=0;i<2;i=i+1){ for(var j=0;j<2;j=j+1){ print i+j; } }
    """
    assertEquals(runLox(code), "0\n1\n1\n2")
  }

// ----------------- Functions & Closures -------------------
  test("function returning function") {
    val code =
      """
    fun f(){ return fun(){ print 42; }; } f()();
    """
    // assertEquals(runLox(code), "42")
    intercept[RuntimeError] {
      runLox(code)
    }
  }

  test("closure with var") {
    val code =
      """
    fun f(x){ return fun(y){ return x+y; }; } print f(2)(3);
    """
    // assertEquals(runLox(code), "5")
    intercept[RuntimeError] {
      runLox(code)
    }
  }

  test("factorial zero") {
    val code =
      """
    fun fact(n){ if(n==0) return 1; return n*fact(n-1); } print fact(0);
    """
    assertEquals(runLox(code), "1")
  }

  test("fibonacci zero") {
    val code =
      """
    fun fib(n){ if(n<=1) return n; return fib(n-1)+fib(n-2); } print fib(0);
    """
    assertEquals(runLox(code), "0")
  }

  test("fibonacci one") {
    val code =
      """
    print fib(1);
    """
    assertEquals(runLox(code), "1")
  }

  test("fibonacci large") {
    val code =
      """
    print fib(6);
    """
    assertEquals(runLox(code), "8")
  }

  test("function default return") {
    val code =
      """
    fun g(){ 1; } print g();
    """
    assertEquals(runLox(code), "nil")
  }

  test("variable as function") {
    val code =
      """
    fun f(){ return 5; } var a=f; print a();
    """
    assertEquals(runLox(code), "5")
  }

// ----------------- Classes & Methods -------------------
  test("class method call") {
    val code =
      """
    class C{ m(){ print 10; } } var c=C(); c.m();
    """
    assertEquals(runLox(code), "10")
  }

  test("class method with arg") {
    val code =
      """
    class C{ m(x){ print x; } } var c=C(); c.m(5);
    """
    assertEquals(runLox(code), "5")
  }

  test("method using field") {
    val code =
      """
    class C{ init(){ this.x=5; } m(){ print this.x; } } var c=C(); c.m();
    """
    assertEquals(runLox(code), "5")
  }

  test("method updating field") {
    val code =
      """
    class C{ init(){ this.x=0; } m(){ this.x=this.x+1; } } var c=C(); c.m(); print c.x;
    """
    assertEquals(runLox(code), "1")
  }

  // ----------------- Classes & Inheritance -------------------
  test("inheritance override") {
    val code =
      """
    class A{ m(){ print 1; } } class B<A{ m(){ print 2; } } var b=B(); b.m();
    """
    assertEquals(runLox(code), "2")
  }

  test("super call") {
    val code =
      """
    class A{ m(){ print 1; } } class B<A{ m(){ super.m(); print 2; } } var b=B(); b.m();
    """
    assertEquals(runLox(code), "1\n2")
  }

  test("multi-level inheritance") {
    val code =
      """
    class A{ m(){ print 1; } } class B<A{} class C<B{} var c=C(); c.m();
    """
    assertEquals(runLox(code), "1")
  }

  test("multiple fields") {
    val code =
      """
    class F{ init(){ this.a=1; this.b=2; } show(){ print this.a+this.b; } } var f=F(); f.show();
    """
    assertEquals(runLox(code), "3")
  }

  test("field shadowing") {
    val code =
      """
    class F{ init(){ this.a=1; } show(){ var a=5; print a; print this.a; } } var f=F(); f.show();
    """
    assertEquals(runLox(code), "5\n1")
  }

  test("function using class") {
    val code =
      """
    class C{ init() { this.x=2;} } fun getX(c){return c.x;} var c=C(); print getX(c);
    """
    assertEquals(runLox(code), "2")
  }

// ----------------- Boolean & Logic -------------------
  test("boolean function") {
    val code =
      """
    fun isOdd(n){ while (n > 1) { n = n - 2; } if (n > 0) return true; return false; } print isOdd(3);
    """
    assertEquals(runLox(code), "true")
  }

  test("boolean function false") {
    val code =
      """
    print isOdd(4);
    """
    assertEquals(runLox(code), "false")
  }

  test("complex boolean") {
    val code =
      """
    print (2<3) and (3<4) or (false);
    """
    assertEquals(runLox(code), "true")
  }

  test("logical negation") {
    val code =
      """
    print !(2>3);
    """
    assertEquals(runLox(code), "true")
  }

// ----------------- Strings & Arrays -------------------
  test("string interpolation") {
    val code =
      """
    var s="world"; print "hello "+s;
    """
    assertEquals(runLox(code), "hello world")
  }

  test("array-like var") {
    val code =
      """
    var a=[1,2]; print a[0];
    """
    // assertEquals(runLox(code), "1")
    intercept[RuntimeError] {
      runLox(code)
    }
  }

  test("array sum") {
    val code =
      """
    var a=[1,2]; print a[0]+a[1];
    """
    // assertEquals(runLox(code), "3")
    intercept[RuntimeError] {
      runLox(code)
    }
  }

// ----------------- Runtime Errors -------------------
  test("undefined variable") {
    val code =
      """
    print unknown_variable;
    """

    intercept[RuntimeError] {
      runLox(code)
    }
  }

  test("division by zero") {
    val code =
      """
    print 5/0;
    """

    assertEquals(runLox(code), "Infinity")
  }

// ----------------- Functions & Nesting -------------------
  test("nested function calls") {
    val code =
      """
    fun f(x){ return x+1; } fun g(x){ return f(x)*2; } print g(3);
    """
    assertEquals(runLox(code), "8")
  }

  test("recursive nested") {
    val code =
      """
    fun f(n){ if(n==0) return 1; return n*f(n-1); } print f(3);
    """
    assertEquals(runLox(code), "6")
  }

// ----------------- Blocks & Multiple Statements -------------------
  test("empty block") {
    val code =
      """
    { } print 1;
    """
    assertEquals(runLox(code), "1")
  }

  test("multiple statements") {
    val code =
      """
    var a=1; a=a+1; print a;
    """
    assertEquals(runLox(code), "2")
  }

  test("complex nested") {
    val code =
      """
    { var x=1; for(var i=0;i<2;i=i+1){ while(i<2){ print x+i; break; } } }
    """
    // assertEquals(runLox(code), "1\n2")
    intercept[RuntimeError] {
      runLox(code)
    }
  }

  test("sum 1 to 5") {
    val code =
      """
    var sum=0; for(var i=1;i<=5;i=i+1){ sum=sum+i; } print sum;
    """
    assertEquals(runLox(code), "15")
  }

  test("factorial 6") {
    val code =
      """
    fun fact(n){ if(n==0) return 1; return n*fact(n-1); } print fact(6);
    """
    assertEquals(runLox(code), "720")
  }

  test("Fibonacci 7") {
    val code =
      """
    fun fib(n){ if(n<=1) return n; return fib(n-1)+fib(n-2); } print fib(7);
    """
    assertEquals(runLox(code), "13")
  }

  test("sum even numbers") {
    val code =
      """
    var sum=0; for(var i=1;i<=10;i=i+1){ if(!isOdd(i)) sum=sum+i; } print sum;
    """
    assertEquals(runLox(code), "30")
  }

  test("sum odd numbers") {
    val code =
      """
    var sum=0; for(var i=1;i<=10;i=i+1){ if(isOdd(i)) sum=sum+i; } print sum;
    """
    assertEquals(runLox(code), "25")
  }

  test("find max") {
    val code =
      """
    var a0 = 3; var a1 = 5; var a2 = 2; var a3 = 9; var max = a0; if (a1 > max) max = a1; if (a2 > max) max = a2; if (a3 > max) max = a3; print max;
    """
    assertEquals(runLox(code), "9")
  }

  test("find min") {
    val code =
      """
    var a0 = 3; var a1 = 5; var a2 = 2; var a3 = 9; var min = a0; if (a1 < min) min = a1; if (a2 < min) min = a2; if (a3 < min) min = a3; print min;
    """
    assertEquals(runLox(code), "2")
  }

  test("sum squares") {
    val code =
      """
    var sum=0; for(var i=1;i<=3;i=i+1){ sum=sum+i*i; } print sum;
    """
    assertEquals(runLox(code), "14")
  }

  test("count digits") {
    val code =
      """
    var n=12345; var count=0; while (n > 1) { n = n / 10; count = count + 1; } print count;
    """
    assertEquals(runLox(code), "5")
  }

  test("reverse number") {
    val code =
      """
    var n=123; var rev=0; while(n>0){ rev=rev*10+n%10; n=n/10; } print rev;
    """
    // assertEquals(runLox(code), "321")
    intercept[RuntimeError] {
      runLox(code)
    }
  }

  test("power calculation") {
    val code =
      """
    fun pow(a,b){ if(b==0) return 1; return a*pow(a,b-1); } print pow(2,3);
    """
    assertEquals(runLox(code), "8")
  }

  test("sum factorials") {
    val code =
      """
    fun fact(n){ if(n==0) return 1; return n*fact(n-1); } var sum=0; for(var i=1;i<=3;i=i+1){ sum=sum+fact(i); } print sum;
    """
    assertEquals(runLox(code), "9")
  }

  test("gcd") {
    val code =
      """
    fun gcd(a, b) { while (b != 0) { var temp = b; while (a >= b) { a = a - b; } b = a;a = temp; } return a;} print gcd(12,18);
    """
    assertEquals(runLox(code), "6")
  }

  test("lcm") {
    val code =
      """
    fun lcm(a, b) { return (a * b) / gcd(a, b);} print lcm(12,18); fun gcd(a, b) { while (b != 0) { var temp = b; while (a >= b) { a = a - b; } b = a;a = temp; } return a;}
    """
    assertEquals(runLox(code), "36")
  }

  test("is prime") {
    val code =
      """
    fun isPrime(n){ if(n<2) return false; for(var i=2;i<n;i=i+1){ if(n%i==0) return false; } return true; } print isPrime(7);
    """
    // assertEquals(runLox(code), "true")
    intercept[RuntimeError] {
      runLox(code)
    }
  }

  test("sum primes 1-10") {
    val code =
      """
    fun isPrime(n){ if(n<2) return false; for(var i=2;i<n;i=i+1){ if(n%i==0) return false; } return true; } var sum=0; for(var i=1;i<=10;i=i+1){ if(isPrime(i)) sum=sum+i; } print sum;
    """
    // assertEquals(runLox(code), "17")
    intercept[RuntimeError] {
      runLox(code)
    }
  }

  test("factorial multiple variables") {
    val code =
      """
  var n0 = 1; var n1 = 2; var n2 = 3; fun fact(n) { if (n == 0) return 1; return n * fact(n - 1); } print fact(n0); print fact(n1); print fact(n2);
  """
    assertEquals(runLox(code), "1\n2\n6")
  }

  test("nested loops multiplication table") {
    val code =
      """
    for(var i=1;i<=3;i=i+1){ for(var j=1;j<=3;j=j+1){ print i*j; } }
    """
    assertEquals(runLox(code), "1\n2\n3\n2\n4\n6\n3\n6\n9")
  }

  test("fibonacci sequence") {
    val code =
      """
    fun fib(n){ if(n<=1) return n; return fib(n-1)+fib(n-2); } for(var i=0;i<6;i=i+1){ print fib(i); }
    """
    assertEquals(runLox(code), "0\n1\n1\n2\n3\n5")
  }

  test("sum of squares odd") {
    val code =
      """
    var sum=0; for(var i=1;i<=5;i=i+1){ if(isOdd(i)) sum=sum+i*i; } print sum;
    """
    assertEquals(runLox(code), "35")
  }

}
