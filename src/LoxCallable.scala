import java.util.List

trait LoxCallable {
  def arity(): Int
  def call(interpreter: Interpreter, arguments: List[Object]): Object
}
