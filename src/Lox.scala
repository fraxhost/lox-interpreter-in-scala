import java.io.{BufferedReader, InputStreamReader}
import java.nio.charset.Charset
import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters._

object Lox {

  private val interpreter = new Interpreter()
  var hadError: Boolean = false
  var hadRuntimeError: Boolean = false
  var testMode = false

  def main(args: Array[String]): Unit = {
    if (args.length > 1) {
      println("Usage: lox [script]")
      System.exit(64)
    } else if (args.length == 1) {
      runFile(args(0))
    } else {
      runPrompt()
    }
  }

  private def runFile(path: String): Unit = {
    val bytes = Files.readAllBytes(Paths.get(path))
    run(new String(bytes, Charset.defaultCharset()))

    if (hadError) System.exit(65)
    if (hadRuntimeError) System.exit(70)
  }

  private def runPrompt(): Unit = {
    val reader = new BufferedReader(new InputStreamReader(System.in))

    while (true) {
      print("> ")
      val line = reader.readLine()
      if (line == null) return

      run(line)
      hadError = false
    }
  }

  def run(source: String): Unit = {
    val scanner = new Scanner(source)
    val tokens = scanner.scanTokens()

    val parser = new Parser(tokens.asJava)
    val statements = parser.parse()

    if (hadError) return

    val resolver = new Resolver(interpreter)
    resolver.resolve(statements)

    if (hadError) return

    interpreter.interpret(statements.asScala.toList)
  }

  def error(line: Int, message: String): Unit = {
    report(line, "", message)
  }

  private def report(line: Int, where: String, message: String): Unit = {
    Console.err.println(s"[line $line] Error$where: $message")

    if (testMode) {
      val errorToken = new Token(TokenType.EOF, "", null, line)
      throw new RuntimeError(errorToken, message)
    }
  }

  def error(token: Token, message: String): Unit = {
    if (token.`type` == TokenType.EOF)
      report(token.line, " at end", message)
    else
      report(token.line, s" at '${token.lexeme}'", message)
  }

  def runtimeError(error: RuntimeError): Unit = {
    Console.err.println(s"${error.getMessage}\n[line ${error.token.line}]")
    hadRuntimeError = true

    if (testMode) {
      throw error
    }
  }
}
