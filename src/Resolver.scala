import java.util.{HashMap, List, Map, Stack}
import scala.jdk.CollectionConverters._
import scala.util.control.Breaks._

class Resolver(interpreter: Interpreter)
    extends Expr.Visitor[Unit]
    with Stmt.Visitor[Unit] {

  private val scopes: Stack[Map[String, Boolean]] = new Stack()

  private object FunctionType extends Enumeration {
    val NONE, FUNCTION, INITIALIZER, METHOD = Value
  }
  private var currentFunction: FunctionType.Value = FunctionType.NONE

  private object ClassType extends Enumeration {
    val NONE, CLASS, SUBCLASS = Value
  }
  private var currentClass: ClassType.Value = ClassType.NONE

  def resolve(statements: List[Stmt]): Unit = {
    statements.asScala.filter(_ != null).foreach(resolve)
  }

  private def resolve(stmt: Stmt): Unit = stmt.accept(this)
  private def resolve(expr: Expr): Unit = expr.accept(this)

  private def resolveFunction(
      function: Stmt.Function,
      `type`: FunctionType.Value
  ): Unit = {
    val enclosingFunction = currentFunction
    currentFunction = `type`
    beginScope()
    function.params.foreach { param =>
      declare(param)
      define(param)
    }
    resolve(function.body.asJava)
    endScope()
    currentFunction = enclosingFunction
  }

  private def beginScope(): Unit = scopes.push(new HashMap[String, Boolean]())
  private def endScope(): Unit = scopes.pop()

  private def declare(name: Token): Unit = {
    if (scopes.isEmpty) return
    val scope = scopes.peek()
    if (scope.containsKey(name.lexeme)) {
      Lox.error(name, "Already a variable with this name in this scope.")
    }
    scope.put(name.lexeme, false)
  }

  private def define(name: Token): Unit = {
    if (scopes.isEmpty) return
    scopes.peek().put(name.lexeme, true)
  }

  private def resolveLocal(expr: Expr, name: Token): Unit = {
    breakable {
      for (i <- scopes.size() - 1 to 0 by -1) {
        if (scopes.get(i).containsKey(name.lexeme)) {
          interpreter.resolve(expr, scopes.size() - 1 - i)
          break // exit the loop instead of returning
        }
      }
    }
  }

  // ==================== Statement Visitors ====================

  override def visitBlockStmt(stmt: Stmt.Block): Unit = {
    beginScope()
    resolve(stmt.statements.asJava)
    endScope()
  }

  override def visitClassStmt(stmt: Stmt.Class): Unit = {
    val enclosingClass = currentClass
    currentClass = ClassType.CLASS

    declare(stmt.name)
    define(stmt.name)

    if (
      stmt.superclass.isDefined &&
      stmt.name.lexeme == stmt.superclass.map(_.name.lexeme).getOrElse("")
    ) {
      Lox.error(stmt.name, "A class cannot inherit from itself.")
    }

    if (stmt.superclass != null) {
      currentClass = ClassType.SUBCLASS
      stmt.superclass.foreach(sc => resolve(sc))
    }

    if (stmt.superclass != null) {
      beginScope()
      scopes.peek().put("super", true)
    }

    beginScope()
    scopes.peek().put("this", true)

    stmt.methods.foreach { method =>
      val declaration =
        if (method.name.lexeme == "init") FunctionType.INITIALIZER
        else FunctionType.METHOD
      resolveFunction(method, declaration)
    }

    endScope()
    if (stmt.superclass != null) endScope()
    currentClass = enclosingClass
  }

  override def visitExpressionStmt(stmt: Stmt.Expression): Unit = resolve(
    stmt.expression
  )
  override def visitFunctionStmt(stmt: Stmt.Function): Unit = {
    declare(stmt.name)
    define(stmt.name)
    resolveFunction(stmt, FunctionType.FUNCTION)
  }
  override def visitIfStmt(stmt: Stmt.If): Unit = {
    resolve(stmt.condition)
    resolve(stmt.thenBranch)
    stmt.elseBranch.foreach(resolve)
  }
  override def visitPrintStmt(stmt: Stmt.Print): Unit = resolve(stmt.expression)
  override def visitReturnStmt(stmt: Stmt.Return): Unit = {
    if (currentFunction == FunctionType.NONE)
      Lox.error(stmt.keyword, "Can't return from top-level code.")
    if (stmt.value != null) {
      if (currentFunction == FunctionType.INITIALIZER)
        Lox.error(stmt.keyword, "Can't return a value from an initializer.")
      stmt.value.foreach(resolve)
    }
  }
  override def visitVarStmt(stmt: Stmt.Var): Unit = {
    declare(stmt.name)
    stmt.initializer.foreach(resolve)
    define(stmt.name)
  }
  override def visitWhileStmt(stmt: Stmt.While): Unit = {
    resolve(stmt.condition)
    resolve(stmt.body)
  }

  // ==================== Expression Visitors ====================

  override def visitAssignExpr(expr: Expr.Assign): Unit = {
    resolve(expr.value)
    resolveLocal(expr, expr.name)
  }
  override def visitBinaryExpr(expr: Expr.Binary): Unit = {
    resolve(expr.left)
    resolve(expr.right)
  }
  override def visitCallExpr(expr: Expr.Call): Unit = {
    resolve(expr.callee)
    expr.arguments.foreach(resolve)
  }
  override def visitGetExpr(expr: Expr.Get): Unit = resolve(expr.obj)
  override def visitGroupingExpr(expr: Expr.Grouping): Unit = resolve(
    expr.expression
  )
  override def visitLiteralExpr(expr: Expr.Literal): Unit = {}
  override def visitLogicalExpr(expr: Expr.Logical): Unit = {
    resolve(expr.left)
    resolve(expr.right)
  }
  override def visitSetExpr(expr: Expr.Set): Unit = {
    resolve(expr.value)
    resolve(expr.obj)
  }
  override def visitSuperExpr(expr: Expr.Super): Unit = {
    if (currentClass == ClassType.NONE)
      Lox.error(expr.keyword, "Can't use 'super' outside of a class.")
    else if (currentClass != ClassType.SUBCLASS)
      Lox.error(
        expr.keyword,
        "Can't use 'super' in a class with no superclass."
      )
    resolveLocal(expr, expr.keyword)
  }
  override def visitThisExpr(expr: Expr.This): Unit = {
    if (currentClass == ClassType.NONE) {
      Lox.error(expr.keyword, "Can't use 'this' outside of a class.")
      return
    }
    resolveLocal(expr, expr.keyword)
  }
  override def visitUnaryExpr(expr: Expr.Unary): Unit = resolve(expr.right)
  override def visitVariableExpr(expr: Expr.Variable): Unit = {
    if (
      !scopes.isEmpty && scopes
        .peek()
        .get(expr.name.lexeme) == java.lang.Boolean.FALSE
    )
      Lox.error(expr.name, "Can't read local variable in its own initializer.")
    resolveLocal(expr, expr.name)
  }
}
