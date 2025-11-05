import java.util.{List, Map}

class LoxClass(
    val name: String,
    val superclass: LoxClass,
    private val methods: Map[String, LoxFunction]
) extends LoxCallable {

  def findMethod(name: String): LoxFunction = {
    if (methods.containsKey(name)) {
      methods.get(name)
    } else if (superclass != null) {
      superclass.findMethod(name)
    } else {
      null
    }
  }

  override def toString: String = name

  override def call(
      interpreter: Interpreter,
      arguments: List[Object]
  ): Object = {
    val instance = new LoxInstance(this)
    val initializer = findMethod("init")
    if (initializer != null) {
      initializer.bind(instance).call(interpreter, arguments)
    }
    instance
  }

  override def arity(): Int = {
    val initializer = findMethod("init")
    if (initializer == null) 0 else initializer.arity()
  }
}
