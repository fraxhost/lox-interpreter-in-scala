import java.util.{ArrayList, Arrays, List}
import scala.jdk.CollectionConverters._

class Parser(private val tokens: List[Token]) {

  private class ParseError extends RuntimeException

  private var current = 0

  def parse(): List[Stmt] = {
    val statements = new ArrayList[Stmt]()
    while (!isAtEnd) {
      statements.add(declaration())
    }
    statements
  }

  private def statement(): Stmt = {
    if (matchToken(TokenType.FOR)) return forStatement()
    if (matchToken(TokenType.IF)) return ifStatement()
    if (matchToken(TokenType.PRINT)) return printStatement()
    if (matchToken(TokenType.RETURN)) return returnStatement()
    if (matchToken(TokenType.WHILE)) return whileStatement()
    if (matchToken(TokenType.LEFT_BRACE))
      return Stmt.Block(block().asScala.toList)

    expressionStatement()
  }

  private def forStatement(): Stmt = {
    consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.")

    val initializer: Stmt =
      if (matchToken(TokenType.SEMICOLON)) null
      else if (matchToken(TokenType.VAR)) varDeclaration()
      else expressionStatement()

    var condition: Expr = null
    if (!check(TokenType.SEMICOLON)) condition = expression()
    consume(TokenType.SEMICOLON, "Expect ';' after loop condition.")

    var increment: Expr = null
    if (!check(TokenType.RIGHT_PAREN)) increment = expression()
    consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.")

    var body = statement()

    if (increment != null) {
      body = Stmt.Block(
        Arrays.asList(body, Stmt.Expression(increment)).asScala.toList
      )
    }

    if (condition == null) condition = Expr.Literal(true)
    body = Stmt.While(condition, body)

    if (initializer != null) {
      body = Stmt.Block(Arrays.asList(initializer, body).asScala.toList)
    }

    body
  }

  private def ifStatement(): Stmt = {
    consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.")
    val condition = expression()
    consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.")

    val thenBranch = statement()
    var elseBranch: Stmt = null
    if (matchToken(TokenType.ELSE)) elseBranch = statement()

    Stmt.If(condition, thenBranch, Option(elseBranch))
  }

  private def printStatement(): Stmt = {
    val value = expression()
    consume(TokenType.SEMICOLON, "Expect ';' after value.")
    Stmt.Print(value)
  }

  private def returnStatement(): Stmt = {
    val keyword = previous()
    var value: Expr = null
    if (!check(TokenType.SEMICOLON)) value = expression()
    consume(TokenType.SEMICOLON, "Expect ';' after return value.")
    Stmt.Return(keyword, Option(value))
  }

  private def varDeclaration(): Stmt = {
    val name = consume(TokenType.IDENTIFIER, "Expect variable name.")

    var initializer: Expr = null
    if (matchToken(TokenType.EQUAL)) initializer = expression()

    consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.")
    Stmt.Var(name, Option(initializer))
  }

  private def whileStatement(): Stmt = {
    consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.")
    val condition = expression()
    consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.")
    val body = statement()
    Stmt.While(condition, body)
  }

  private def expressionStatement(): Stmt = {
    val expr = expression()
    consume(TokenType.SEMICOLON, "Expect ';' after expression.")
    Stmt.Expression(expr)
  }

  private def function(kind: String): Stmt.Function = {
    val name = consume(TokenType.IDENTIFIER, s"Expect $kind name.")
    consume(TokenType.LEFT_PAREN, s"Expect '(' after $kind name.")
    val parameters = new ArrayList[Token]()
    if (!check(TokenType.RIGHT_PAREN)) {
      var continue = true
      while (continue) {
        if (parameters.size >= 255)
          error(peek(), "Can't have more than 255 parameters.")
        parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name."))
        continue = matchToken(TokenType.COMMA)
      }
    }
    consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.")
    consume(TokenType.LEFT_BRACE, s"Expect '{' before $kind body.")
    val body = block()
    Stmt.Function(name, parameters.asScala.toList, body.asScala.toList)
  }

  private def block(): List[Stmt] = {
    val statements = new ArrayList[Stmt]()
    while (!check(TokenType.RIGHT_BRACE) && !isAtEnd) {
      statements.add(declaration())
    }
    consume(TokenType.RIGHT_BRACE, "Expect '}' after block.")
    statements
  }

  private def assignment(): Expr = {
    var expr = or()
    if (matchToken(TokenType.EQUAL)) {
      val equals = previous()
      val value = assignment()
      expr match {
        case v: Expr.Variable => return Expr.Assign(v.name, value)
        case g: Expr.Get      => return Expr.Set(g.obj, g.name, value)
        case _ => throw error(equals, "Invalid assignment target.")
      }
    }
    expr
  }

  private def or(): Expr = {
    var expr = and()
    while (matchToken(TokenType.OR)) {
      val operator = previous()
      val right = and()
      expr = Expr.Logical(expr, operator, right)
    }
    expr
  }

  private def and(): Expr = {
    var expr = equality()
    while (matchToken(TokenType.AND)) {
      val operator = previous()
      val right = equality()
      expr = Expr.Logical(expr, operator, right)
    }
    expr
  }

  private def expression(): Expr = assignment()

  private def declaration(): Stmt = {
    try {
      if (matchToken(TokenType.CLASS)) return classDeclaration()
      if (matchToken(TokenType.FUN)) return function("function")
      if (matchToken(TokenType.VAR)) return varDeclaration()

      statement()
    } catch {
      case _: ParseError =>
        synchronize()
        null
    }
  }

  private def classDeclaration(): Stmt = {
    val name = consume(TokenType.IDENTIFIER, "Expect class name.")
    var superclass: Expr.Variable = null
    if (matchToken(TokenType.LESS)) {
      consume(TokenType.IDENTIFIER, "Expect superclass name.")
      superclass = Expr.Variable(previous())
    }

    consume(TokenType.LEFT_BRACE, "Expect '{' before class body.")
    val methods = new ArrayList[Stmt.Function]()
    while (!check(TokenType.RIGHT_BRACE) && !isAtEnd) {
      methods.add(function("method"))
    }
    consume(TokenType.RIGHT_BRACE, "Expect '}' after class body.")

    Stmt.Class(name, Option(superclass), methods.asScala.toList)
  }

  private def equality(): Expr = {
    var expr = comparison()
    while (matchToken(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
      val operator = previous()
      val right = comparison()
      expr = Expr.Binary(expr, operator, right)
    }
    expr
  }

  private def comparison(): Expr = {
    var expr = term()
    while (
      matchToken(
        TokenType.GREATER,
        TokenType.GREATER_EQUAL,
        TokenType.LESS,
        TokenType.LESS_EQUAL
      )
    ) {
      val operator = previous()
      val right = term()
      expr = Expr.Binary(expr, operator, right)
    }
    expr
  }

  private def term(): Expr = {
    var expr = factor()
    while (matchToken(TokenType.MINUS, TokenType.PLUS)) {
      val operator = previous()
      val right = factor()
      expr = Expr.Binary(expr, operator, right)
    }
    expr
  }

  private def factor(): Expr = {
    var expr = unary() // next lower precedence (unary operators)
    while (matchToken(TokenType.SLASH, TokenType.STAR)) {
      val operator = previous()
      val right = unary()
      expr = Expr.Binary(expr, operator, right)
    }
    expr
  }

  private def unary(): Expr = {
    if (matchToken(TokenType.BANG, TokenType.MINUS)) {
      val operator = previous()
      val right = unary()
      return Expr.Unary(operator, right)
    }

    call()
  }

  private def finishCall(callee: Expr): Expr = {
    val arguments = scala.collection.mutable.ListBuffer[Expr]()

    if (!check(TokenType.RIGHT_PAREN)) {
      var continue = true
      while (continue) {
        if (arguments.size >= 255)
          error(peek(), "Can't have more than 255 arguments.")
        arguments += expression()
        continue = matchToken(TokenType.COMMA)
      }
    }

    val paren = consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.")

    Expr.Call(callee, paren, arguments.toList)
  }

  private def call(): Expr = {
    var expr = primary()

    var continue = true
    while (continue) {
      if (matchToken(TokenType.LEFT_PAREN)) {
        expr = finishCall(expr)
      } else if (matchToken(TokenType.DOT)) {
        val name =
          consume(TokenType.IDENTIFIER, "Expect property name after '.'.")
        expr = Expr.Get(expr, name)
      } else {
        continue = false
      }
    }

    expr
  }

  private def primary(): Expr = {
    if (matchToken(TokenType.FALSE)) return Expr.Literal(false)
    if (matchToken(TokenType.TRUE)) return Expr.Literal(true)
    if (matchToken(TokenType.NIL)) return Expr.Literal(null)

    if (matchToken(TokenType.NUMBER, TokenType.STRING)) {
      return Expr.Literal(previous().literal)
    }

    if (matchToken(TokenType.SUPER)) {
      val keyword = previous()
      consume(TokenType.DOT, "Expect '.' after 'super'.")
      val method =
        consume(TokenType.IDENTIFIER, "Expect superclass method name.")
      return Expr.Super(keyword, method)
    }

    if (matchToken(TokenType.THIS)) {
      return Expr.This(previous())
    }

    if (matchToken(TokenType.IDENTIFIER)) {
      return Expr.Variable(previous())
    }

    if (matchToken(TokenType.LEFT_PAREN)) {
      val expr = expression()
      consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
      return Expr.Grouping(expr)
    }

    throw error(peek(), "Expect expression.")
  }

  private def matchToken(types: TokenType*): Boolean = {
    types.exists { `type` =>
      if (check(`type`)) {
        advance()
        true
      } else false
    }
  }

  private def consume(tokenType: TokenType, message: String): Token = {
    if (check(tokenType)) return advance()
    throw error(peek(), message)
  }

  private def check(`type`: TokenType): Boolean = {
    if (isAtEnd) false
    else peek().`type` == `type`
  }

  private def advance(): Token = {
    if (!isAtEnd) current += 1;
    previous()
  }

  private def isAtEnd: Boolean = peek().`type` == TokenType.EOF

  private def peek(): Token = tokens.get(current)

  private def previous(): Token = tokens.get(current - 1)

  private def error(token: Token, message: String): ParseError = {
    Lox.error(token, message)
    new ParseError
  }

  private def synchronize(): Unit = {
    advance()
    while (!isAtEnd) {
      if (previous().`type` == TokenType.SEMICOLON) return
      peek().`type` match {
        case TokenType.CLASS | TokenType.FUN | TokenType.VAR | TokenType.FOR |
            TokenType.IF | TokenType.WHILE | TokenType.PRINT |
            TokenType.RETURN =>
          return
        case _ => advance()
      }
    }
  }
}
