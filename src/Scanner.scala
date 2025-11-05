import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Scanner(val source: String) {

  import Scanner.keywords

  private val tokens: ListBuffer[Token] = ListBuffer()
  private var start: Int = 0
  private var current: Int = 0
  private var line: Int = 1

  def scanTokens(): List[Token] = {
    while (!isAtEnd) {
      start = current
      scanToken()
    }
    tokens += new Token(TokenType.EOF, "", null, line)
    tokens.toList
  }

  private def scanToken(): Unit = {
    val c = advance()
    c match {
      case '(' => addToken(TokenType.LEFT_PAREN)
      case ')' => addToken(TokenType.RIGHT_PAREN)
      case '{' => addToken(TokenType.LEFT_BRACE)
      case '}' => addToken(TokenType.RIGHT_BRACE)
      case ',' => addToken(TokenType.COMMA)
      case '.' => addToken(TokenType.DOT)
      case '-' => addToken(TokenType.MINUS)
      case '+' => addToken(TokenType.PLUS)
      case ';' => addToken(TokenType.SEMICOLON)
      case '*' => addToken(TokenType.STAR)
      case '!' =>
        addToken(if (matchChar('=')) TokenType.BANG_EQUAL else TokenType.BANG)
      case '=' =>
        addToken(if (matchChar('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
      case '<' =>
        addToken(if (matchChar('=')) TokenType.LESS_EQUAL else TokenType.LESS)
      case '>' =>
        addToken(
          if (matchChar('=')) TokenType.GREATER_EQUAL else TokenType.GREATER
        )
      case '/' =>
        if (matchChar('/')) {
          while (peek() != '\n' && !isAtEnd) advance()
        } else {
          addToken(TokenType.SLASH)
        }
      case ' ' | '\r' | '\t' => // ignore whitespace
      case '\n'              => line += 1
      case '"'               => string()
      case _ =>
        if (isDigit(c)) number()
        else if (isAlpha(c)) identifier()
        else Lox.error(line, "Unexpected character.")
    }
  }

  private def isAtEnd: Boolean = current >= source.length

  private def peek(): Char = if (isAtEnd) '\u0000' else source.charAt(current)

  private def peekNext(): Char =
    if (current + 1 >= source.length) '\u0000' else source.charAt(current + 1)

  private def advance(): Char = {
    val c = source.charAt(current)
    current += 1
    c
  }

  private def matchChar(expected: Char): Boolean = {
    if (isAtEnd || source.charAt(current) != expected) false
    else {
      current += 1
      true
    }
  }

  private def string(): Unit = {
    while (peek() != '"' && !isAtEnd) {
      if (peek() == '\n') line += 1
      advance()
    }

    if (isAtEnd) {
      Lox.error(line, "Unterminated string.")
      return
    }

    advance() // closing "

    val value = source.substring(start + 1, current - 1)
    addToken(TokenType.STRING, value)
  }

  private def number(): Unit = {
    while (isDigit(peek())) advance()

    if (peek() == '.' && isDigit(peekNext())) {
      advance()
      while (isDigit(peek())) advance()
    }

    addToken(TokenType.NUMBER, source.substring(start, current).toDouble)
  }

  private def identifier(): Unit = {
    while (isAlphaNumeric(peek())) advance()
    val text = source.substring(start, current)
    val ttype = keywords.getOrElse(text, TokenType.IDENTIFIER)
    addToken(ttype)
  }

  private def isDigit(c: Char): Boolean = c.isDigit

  private def isAlpha(c: Char): Boolean = c.isLetter || c == '_'

  private def isAlphaNumeric(c: Char): Boolean = isAlpha(c) || isDigit(c)

  private def addToken(ttype: TokenType, literal: Any = null): Unit = {
    val text = source.substring(start, current)
    tokens += new Token(ttype, text, literal, line)
  }
}

object Scanner {
  val keywords: Map[String, TokenType] = Map(
    "and" -> TokenType.AND,
    "class" -> TokenType.CLASS,
    "else" -> TokenType.ELSE,
    "false" -> TokenType.FALSE,
    "for" -> TokenType.FOR,
    "fun" -> TokenType.FUN,
    "if" -> TokenType.IF,
    "nil" -> TokenType.NIL,
    "or" -> TokenType.OR,
    "print" -> TokenType.PRINT,
    "return" -> TokenType.RETURN,
    "super" -> TokenType.SUPER,
    "this" -> TokenType.THIS,
    "true" -> TokenType.TRUE,
    "var" -> TokenType.VAR,
    "while" -> TokenType.WHILE
  )
}
