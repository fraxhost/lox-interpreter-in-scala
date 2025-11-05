import java.util.{HashMap, Map}

class LoxInstance(private val klass: LoxClass) {

  private val fields: Map[String, Object] = new HashMap()

  def get(name: Token): Any = {
    if (fields.containsKey(name.lexeme)) {
      return fields.get(name.lexeme)
    }

    val method = klass.findMethod(name.lexeme)
    if (method != null) {
      return method.bind(this)
    }

    throw new RuntimeError(name, s"Undefined property '${name.lexeme}'.")
  }

  def set(name: Token, value: Any): Unit = {
    fields.put(name.lexeme, value.asInstanceOf[Object])
  }

  override def toString: String = s"${klass.name} instance"
}
