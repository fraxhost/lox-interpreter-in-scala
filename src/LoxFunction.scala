import java.util.List

class LoxFunction(
    private val declaration: Stmt.Function,
    private val closure: Environment,
    private val isInitializer: Boolean
) extends LoxCallable {

  def bind(instance: LoxInstance): LoxFunction = {
    val environment = new Environment(closure)
    environment.define("this", instance)
    new LoxFunction(declaration, environment, isInitializer)
  }

  override def toString: String = s"<fn ${declaration.name.lexeme}>"

  override def call(
      interpreter: Interpreter,
      arguments: List[Object]
  ): Object = {
    val environment = new Environment(closure)
    for (i <- 0 until declaration.params.size) {
      environment.define(declaration.params(i).lexeme, arguments.get(i))
    }

    try {
      interpreter.executeBlock(declaration.body, environment)
    } catch {
      case returnValue: Return =>
        if (isInitializer) {
          return closure.getAt(0, "this").asInstanceOf[Object]
        }
        return returnValue.value.asInstanceOf[Object]
    }

    if (isInitializer) {
      closure.getAt(0, "this").asInstanceOf[Object]
    } else {
      null
    }
  }

  override def arity(): Int = declaration.params.size
}
