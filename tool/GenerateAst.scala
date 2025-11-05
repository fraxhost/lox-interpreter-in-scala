package tool

import java.io.{IOException, PrintWriter}
import scala.util.Using

object GenerateAst {

  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      Console.err.println("Usage: generate_ast <output directory>")
      System.exit(64)
    }

    val outputDir = args(0)

    defineAst(outputDir, "Expr", Array(
      "Assign   : Token name, Expr value",
      "Binary   : Expr left, Token operator, Expr right",
      "Call     : Expr callee, Token paren, List[Expr] arguments",
      "Get      : Expr obj, Token name",
      "Grouping : Expr expression",
      "Literal  : Any value",
      "Logical  : Expr left, Token operator, Expr right",
      "Set      : Expr obj, Token name, Expr value",
      "Super    : Token keyword, Token method",
      "This     : Token keyword",
      "Unary    : Token operator, Expr right",
      "Variable : Token name"
    ))

    defineAst(outputDir, "Stmt", Array(
      "Block      : List[Stmt] statements",
      "Class      : Token name, Expr.Variable superclass, List[Stmt.Function] methods",
      "Expression : Expr expression",
      "Function   : Token name, List[Token] params, List[Stmt] body",
      "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
      "Print      : Expr expression",
      "Return     : Token keyword, Expr value",
      "Var        : Token name, Expr initializer",
      "While      : Expr condition, Stmt body"
    ))
  }

  @throws[IOException]
  private def defineAst(outputDir: String, baseName: String, types: Array[String]): Unit = {
    val path = s"$outputDir/$baseName.scala"

    Using.resource(new PrintWriter(path, "UTF-8")) { writer =>
      writer.println("import scala.util.*")
      writer.println("import java.util.List") // still compatible w/ existing code if needed
      writer.println()
      writer.println(s"sealed trait $baseName {")

      writer.println(s"  def accept[R](visitor: ${baseName}Visitor[R]): R")
      writer.println("}")

      defineVisitor(writer, baseName, types)

      for (t <- types) {
        val parts = t.split(":")
        val className = parts(0).trim
        val fields = parts(1).trim
        defineType(writer, baseName, className, fields)
      }
    }
  }

  private def defineType(writer: PrintWriter, baseName: String, className: String, fieldList: String): Unit = {
    val fields = fieldList.split(", ")

    val params = fields.map(f => {
      val parts = f.split(" ")
      s"val ${parts(1)}: ${parts(0]}"
    }).mkString(", ")

    writer.println()
    writer.println(s"final case class $className($params) extends $baseName {")
    writer.println(s"  override def accept[R](visitor: ${baseName}Visitor[R]): R =")
    writer.println(s"    visitor.visit$className$baseName(this)")
    writer.println("}")
  }

  private def defineVisitor(writer: PrintWriter, baseName: String, types: Array[String]): Unit = {
    writer.println()
    writer.println(s"trait ${baseName}Visitor[R] {")

    for (t <- types) {
      val typeName = t.split(":")(0).trim
      writer.println(s"  def visit$typeName$baseName(node: $typeName): R")
    }

    writer.println("}")
  }
}
