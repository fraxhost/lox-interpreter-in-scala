| Name                               | Code                                                                                       | Expected Output | Notes                               |
| ---------------------------------- | ------------------------------------------------------------------------------------------ | --------------- | ----------------------------------- |
| simple addition                    | `print 1 + 2;`                                                                             | `3`             | Simple addition                     |
| subtraction                        | `print 5 - 3;`                                                                             | `2`             | Subtraction                         |
| multiplication                     | `print 4 * 6;`                                                                             | `24`            | Multiplication                      |
| division                           | `print 20 / 5;`                                                                            | `4`             | Division                            |
| grouping                           | `print (2 + 3) * 4;`                                                                       | `20`            | Grouping parentheses                |
| negative number                    | `print -10 + 5;`                                                                           | `-5`            | Negative number                     |
| boolean literal                    | `print true;`                                                                               | `true`          | Boolean literal                     |
| boolean negation                   | `print !false;`                                                                             | `true`          | Boolean negation                    |
| equality                           | `print 10 == 10;`                                                                          | `true`          | Equality check                      |
| inequality                         | `print 10 != 5;`                                                                           | `true`          | Inequality check                    |
| variable declaration               | `var a = 10; print a;`                                                                     | `10`            | Variable declaration                |
| variable update                    | `var a = 5; a = 7; print a;`                                                               | `7`             | Variable update                     |
| variable with expression           | `var x = 3 + 4; print x;`                                                                  | `7`             | Variable with expression            |
| multiple variables                 | `var a=1; var b=2; print a+b;`                                                             | `3`             | Multiple variables                  |
| expression using variables         | `var a=10; var b=20; print a+b*2;`                                                        | `50`            | Expression using variables          |
| variable shadowing                 | `var a=5; { var a=10; print a; } print a;`                                                | `10\n5`         | Variable shadowing                  |
| simple if                          | `if (true) print 1;`                                                                       | `1`             | Simple if                           |
| if-else                            | `if (false) print 1; else print 2;`                                                       | `2`             | If-else                             |
| nested if                          | `if (true) { if(false) print 1; else print 2; }`                                          | `2`             | Nested if                           |
| comparison in if                   | `if (3 < 5) print 10;`                                                                     | `10`            | Comparison in if                    |
| logical AND in if                  | `if (2<3 && 4>1) print 100;`                                                              | `100`           | Logical AND                         |
| logical OR in if                   | `if (false || true) print 50;`                                                            | `50`            | Logical OR                          |
| simple while loop                  | `var i=0; while(i<3){ print i; i=i+1; }`                                                 | `0\n1\n2`       | Simple while loop                   |
| while with accumulation            | `var sum=0; var i=1; while(i<=3){ sum=sum+i; i=i+1; } print sum;`                         | `6`             | While with accumulation             |
| simple for loop                    | `for (var i = 0; i < 3; i = i + 1) { print i; }`                                         | `0\n1\n2`       | Simple for loop                     |
| for loop with accumulation         | `var sum = 0; for (var i = 1; i <= 3; i = i + 1) { sum = sum + i; } print sum;`           | `6`             | For loop with accumulation          |
| simple function add                | `fun add(a,b){ return a+b; } print add(2,3);`                                             | `5`             | Simple function                     |
| recursive function factorial       | `fun fact(n){ if(n==0) return 1; return n * fact(n-1); } print fact(5);`                  | `120`           | Recursive factorial                 |
| function shadowing                 | `var a=5; fun f(a){ return a+1; } print f(10);`                                           | `11`            | Function shadowing                  |
| function returning boolean         | `fun isNumberTwo(n){ return n == 2; } print isNumberTwo(2);`                               | `true`          | Function returning boolean          |
| simple class creation              | `class Foo {} var f = Foo(); print f;`                                                    | `Foo instance`  | Simple class creation               |
| class with method                  | `class Foo { fun bar() { print 123; } } var f = Foo(); f.bar();`                           | `123`           | Class with method                   |
| field access in class              | `class Foo { init(x) { this.x = x; } show() { print this.x; } } var f = Foo(10); f.show();`| `10`            | Field access in class               |
| field update in class              | `class Foo { init() { this.x = 5; } } var f = Foo(); f.x = 20; print f.x;`                 | `20`            | Field update in class               |
| this keyword in class              | `class Foo { init() { this.x = 5; } get() { print this.x; } } var f = Foo(); f.get();`     | `5`             | this keyword in class               |
| super call with inheritance        | `class A { a() { print 1; } } class B < A { a() { super.a(); print 2; } } var b = B(); b.a();` | `1\n2`       | Super call with inheritance         |
| multi-level inheritance            | `class A { a() { print 1; } } class B < A { b() { print 2; } } class C < B {} var c = C(); c.a(); c.b();` | `1\n2`  | Multi-level inheritance             |
| multiple operation on class fields | `class Counter { init() { this.value = 0; } increment() { this.value = this.value + 1; } decrement() { this.value = this.value - 1; } show() { print this.value; } } var c = Counter(); c.init(); c.increment(); c.increment(); c.decrement(); c.show();` | `1` | Multiple operations on class fields |
| function and class interaction     | `class Foo { x=5; } fun getX(f) { return f.x; } var f = Foo(); print getX(f);`             | `5`             | Function and class interaction      |
| full inheritance test              | `class A { a(){ print 1; } } class B < A { a(){ super.a(); print 2; } } class C < B {} var c = C(); c.a();` | `1\n2` | Full inheritance test |
