import scala.collection.mutable

class Environment(val enclosing: Environment = null) {

  private val values: mutable.Map[String, Any] = mutable.HashMap()

  def get(name: Token): Any = {
    if (values.contains(name.lexeme)) {
      values(name.lexeme)
    } else if (enclosing != null) {
      enclosing.get(name)
    } else {
      throw new RuntimeError(name, s"Undefined variable '${name.lexeme}'.")
    }
  }

  def assign(name: Token, value: Any): Unit = {
    if (values.contains(name.lexeme)) {
      values(name.lexeme) = value
    } else if (enclosing != null) {
      enclosing.assign(name, value)
    } else {
      throw new RuntimeError(name, s"Undefined variable '${name.lexeme}'.")
    }
  }

  def define(name: String, value: Any): Unit = {
    values(name) = value
  }

  def getAt(distance: Int, name: String): Any = {
    ancestor(distance).values(name)
  }

  def assignAt(distance: Int, name: Token, value: Any): Unit = {
    ancestor(distance).values(name.lexeme) = value
  }

  private def ancestor(distance: Int): Environment = {
    var environment = this
    for (_ <- 0 until distance) {
      environment = environment.enclosing
    }
    environment
  }
}
