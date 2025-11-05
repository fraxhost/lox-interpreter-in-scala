final case class Token(
    `type`: TokenType,
    lexeme: String,
    literal: Any,
    line: Int
) {
  override def toString: String = s"${`type`} $lexeme $literal"
}
