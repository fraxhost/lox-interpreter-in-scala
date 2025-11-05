sealed abstract class Stmt {
  def accept[R](visitor: Stmt.Visitor[R]): R
}

object Stmt {
  trait Visitor[R] {
    def visitBlockStmt(stmt: Stmt.Block): R
    def visitClassStmt(stmt: Stmt.Class): R
    def visitExpressionStmt(stmt: Stmt.Expression): R
    def visitFunctionStmt(stmt: Stmt.Function): R
    def visitIfStmt(stmt: Stmt.If): R
    def visitPrintStmt(stmt: Stmt.Print): R
    def visitReturnStmt(stmt: Stmt.Return): R
    def visitVarStmt(stmt: Stmt.Var): R
    def visitWhileStmt(stmt: Stmt.While): R
  }

  final case class Block(statements: List[Stmt]) extends Stmt {
    override def accept[R](visitor: Visitor[R]): R =
      visitor.visitBlockStmt(this)
  }

  final case class Class(
      name: Token,
      superclass: Option[Expr.Variable],
      methods: List[Function]
  ) extends Stmt {
    override def accept[R](visitor: Visitor[R]): R =
      visitor.visitClassStmt(this)
  }

  final case class Expression(expression: Expr) extends Stmt {
    override def accept[R](visitor: Visitor[R]): R =
      visitor.visitExpressionStmt(this)
  }

  final case class Function(name: Token, params: List[Token], body: List[Stmt])
      extends Stmt {
    override def accept[R](visitor: Visitor[R]): R =
      visitor.visitFunctionStmt(this)
  }

  final case class If(
      condition: Expr,
      thenBranch: Stmt,
      elseBranch: Option[Stmt]
  ) extends Stmt {
    override def accept[R](visitor: Visitor[R]): R = visitor.visitIfStmt(this)
  }

  final case class Print(expression: Expr) extends Stmt {
    override def accept[R](visitor: Visitor[R]): R =
      visitor.visitPrintStmt(this)
  }

  final case class Return(keyword: Token, value: Option[Expr]) extends Stmt {
    override def accept[R](visitor: Visitor[R]): R =
      visitor.visitReturnStmt(this)
  }

  final case class Var(name: Token, initializer: Option[Expr]) extends Stmt {
    override def accept[R](visitor: Visitor[R]): R = visitor.visitVarStmt(this)
  }

  final case class While(condition: Expr, body: Stmt) extends Stmt {
    override def accept[R](visitor: Visitor[R]): R =
      visitor.visitWhileStmt(this)
  }

}
