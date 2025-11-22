import scala.jdk.CollectionConverters.*
import scala.collection.mutable.{HashMap, ListBuffer, Map => MutableMap}
import java.util.IdentityHashMap
import Stmt.Visitor

class Interpreter extends Expr.Visitor[Any] with Stmt.Visitor[Unit] {

  val globals: Environment = new Environment()
  private var environment: Environment = globals
  val locals: scala.collection.mutable.Map[Expr, Int] =
    new IdentityHashMap[Expr, Int]().asScala

  // clock() native function
  globals.define(
    "clock",
    new LoxCallable {
      override def arity(): Int = 0
      override def call(
          interpreter: Interpreter,
          arguments: java.util.List[Object]
      ): Object = {
        System
          .currentTimeMillis()
          .asInstanceOf[Object] // Box the Long to Object
      }
      override def toString: String = "<native fn>"
    }
  )

  def interpret(statements: List[Stmt]): Unit =
    try {
      statements.foreach(execute)
    } catch {
      case error: RuntimeError => Lox.runtimeError(error)
    }

  // ---- Expression Visitors ----

  override def visitLiteralExpr(expr: Expr.Literal): Any = expr.value

  override def visitLogicalExpr(expr: Expr.Logical): Any = {
    val left = evaluate(expr.left)

    expr.operator.`type` match {
      case TokenType.OR =>
        if (isTruthy(left)) return left
      case _ =>
        if (!isTruthy(left)) return left
    }

    evaluate(expr.right)
  }

  override def visitSetExpr(expr: Expr.Set): Any = {
    val obj = evaluate(expr.obj)

    obj match {
      case instance: LoxInstance =>
        val value = evaluate(expr.value)
        instance.set(expr.name, value)
        value
      case _ =>
        throw RuntimeError(expr.name, "Only instances have fields.")
    }
  }

  override def visitSuperExpr(expr: Expr.Super): Any = {
    val distance = locals(expr)
    val superclass = environment.getAt(distance, "super").asInstanceOf[LoxClass]
    val instance =
      environment.getAt(distance - 1, "this").asInstanceOf[LoxInstance]

    val method = superclass.findMethod(expr.method.lexeme)
    if (method == null) throw RuntimeError(expr.method, "Undefined property.")

    return method.bind(instance)
  }

  override def visitThisExpr(expr: Expr.This): Any =
    lookUpVariable(expr.keyword, expr)

  override def visitGroupingExpr(expr: Expr.Grouping): Any =
    evaluate(expr.expression)

  override def visitUnaryExpr(expr: Expr.Unary): Any = {
    val right = evaluate(expr.right)

    expr.operator.`type` match {
      case TokenType.BANG => !isTruthy(right)
      case TokenType.MINUS =>
        checkNumberOperand(expr.operator, right)
        -right.asInstanceOf[Double]
      case _ => null
    }
  }

  override def visitVariableExpr(expr: Expr.Variable): Any =
    lookUpVariable(expr.name, expr)

  override def visitAssignExpr(expr: Expr.Assign): Any = {
    val value = evaluate(expr.value)
    locals.get(expr) match {
      case Some(distance) =>
        environment.assignAt(distance, expr.name, value)
      case None =>
        globals.assign(expr.name, value)
    }

    environment.assign(expr.name, value);
    value
  }

  override def visitBinaryExpr(expr: Expr.Binary): Any = {
    val left = evaluate(expr.left)
    val right = evaluate(expr.right)

    expr.operator.`type` match {
      case TokenType.GREATER =>
        checkNumberOperands(expr.operator, left, right)
        left.asInstanceOf[Double] > right.asInstanceOf[Double]

      case TokenType.GREATER_EQUAL =>
        checkNumberOperands(expr.operator, left, right)
        left.asInstanceOf[Double] >= right.asInstanceOf[Double]

      case TokenType.LESS =>
        checkNumberOperands(expr.operator, left, right)
        left.asInstanceOf[Double] < right.asInstanceOf[Double]

      case TokenType.LESS_EQUAL =>
        checkNumberOperands(expr.operator, left, right)
        left.asInstanceOf[Double] <= right.asInstanceOf[Double]

      case TokenType.BANG_EQUAL =>
        !isEqual(left, right)

      case TokenType.EQUAL_EQUAL =>
        isEqual(left, right)

      case TokenType.MINUS =>
        checkNumberOperands(expr.operator, left, right)
        left.asInstanceOf[Double] - right.asInstanceOf[Double]

      case TokenType.PLUS =>
        (left, right) match {
          case (l: Double, r: Double) => l + r
          case (l: String, r: String) => l + r
          case _ =>
            throw RuntimeError(
              expr.operator,
              "Operands must be two numbers or two strings."
            )
        }

      case TokenType.SLASH =>
        checkNumberOperands(expr.operator, left, right)
        left.asInstanceOf[Double] / right.asInstanceOf[Double]

      case TokenType.STAR =>
        checkNumberOperands(expr.operator, left, right)
        left.asInstanceOf[Double] * right.asInstanceOf[Double]

      case _ => null
    }
  }

  override def visitCallExpr(expr: Expr.Call): Any = {
    val callee = evaluate(expr.callee)
    val arguments = expr.arguments.map(evaluate)

    callee match {
      case fn: LoxCallable =>
        if (arguments.length != fn.arity())
          throw RuntimeError(
            expr.paren,
            s"Expected ${fn.arity()} arguments but got ${arguments.length}."
          )
        // fn.call(this, arguments)
        fn.call(this, arguments.map(_.asInstanceOf[Object]).asJava)
      case _ =>
        throw RuntimeError(expr.paren, "Can only call functions and classes.")
    }
  }

  override def visitGetExpr(expr: Expr.Get): Any = {
    evaluate(expr.obj) match {
      case instance: LoxInstance => instance.get(expr.name)
      case _ =>
        throw RuntimeError(expr.name, "Only instances have properties.")
    }
  }

  // ---- Statement Visitors ----

  override def visitBlockStmt(stmt: Stmt.Block): Unit =
    executeBlock(stmt.statements, new Environment(environment))

  override def visitClassStmt(stmt: Stmt.Class): Unit = {
    val superclass: LoxClass | Null =
      stmt.superclass match
        case Some(s) =>
          val sc = evaluate(s)
          sc match
            case cls: LoxClass => cls
            case _ =>
              throw RuntimeError(s.name, "Superclass must be a class.")
        case None => null

    environment.define(stmt.name.lexeme, null)

    if (superclass != null) {
      environment = new Environment(environment)
      environment.define("super", superclass)
    }

    val methods = stmt.methods.map { method =>
      val function =
        new LoxFunction(method, environment, method.name.lexeme == "init")
      method.name.lexeme -> function
    }.toMap

    val klass = new LoxClass(stmt.name.lexeme, superclass, methods.asJava)

    if (superclass != null)
      environment = environment.enclosing

    environment.assign(stmt.name, klass)
  }

  override def visitExpressionStmt(stmt: Stmt.Expression): Unit =
    evaluate(stmt.expression)

  override def visitFunctionStmt(stmt: Stmt.Function): Unit = {
    val function = new LoxFunction(stmt, environment, false)
    environment.define(stmt.name.lexeme, function)
  }

  override def visitIfStmt(stmt: Stmt.If): Unit =
    if (isTruthy(evaluate(stmt.condition))) execute(stmt.thenBranch)
    // else if (stmt.elseBranch != null) execute(stmt.elseBranch)
    else stmt.elseBranch.foreach(execute)

  override def visitPrintStmt(stmt: Stmt.Print): Unit =
    println(stringify(evaluate(stmt.expression)))

  override def visitReturnStmt(stmt: Stmt.Return): Unit = {
    // val value = if (stmt.value != null) evaluate(stmt.value) else null
    val value: Any | Null = stmt.value.map(evaluate).getOrElse(null)
    throw Return(value)
  }

  override def visitVarStmt(stmt: Stmt.Var): Unit = {
    val value: Any | Null = stmt.initializer.map(evaluate).getOrElse(null)
    environment.define(stmt.name.lexeme, value)
  }

  override def visitWhileStmt(stmt: Stmt.While): Unit =
    while (isTruthy(evaluate(stmt.condition))) execute(stmt.body)

  // ---- Helpers ----

  private def evaluate(expr: Expr): Any =
    expr.accept(this)

  private def execute(stmt: Stmt): Unit = {
    if (stmt == null) return
    stmt.accept(this)
  }

  def resolve(expr: Expr, depth: Int): Unit =
    locals.put(expr, depth)

  def executeBlock(statements: List[Stmt], newEnv: Environment): Unit = {
    val previous = environment
    try {
      environment = newEnv
      statements.foreach(execute)
    } finally {
      environment = previous
    }
  }

  private def lookUpVariable(name: Token, expr: Expr): Any =
    locals
      .get(expr)
      .map(distance => environment.getAt(distance, name.lexeme))
      .getOrElse(globals.get(name))

  private def checkNumberOperand(operator: Token, operand: Any): Unit =
    if (!operand.isInstanceOf[Double])
      throw RuntimeError(operator, "Operand must be a number.")

  private def checkNumberOperands(
      operator: Token,
      left: Any,
      right: Any
  ): Unit =
    if (!left.isInstanceOf[Double] || !right.isInstanceOf[Double])
      throw RuntimeError(operator, "Operands must be numbers.")

  private def isTruthy(value: Any): Boolean = value match {
    case null       => false
    case b: Boolean => b
    case _          => true
  }

  private def isEqual(a: Any, b: Any): Boolean =
    if (a == null && b == null) true else if (a == null) false else a.equals(b)

  private def stringify(obj: Any): String = obj match {
    case null => "nil"
    case d: Double =>
      val s = d.toString
      if (s.endsWith(".0")) s.dropRight(2) else s
    case other => other.toString
  }
}
