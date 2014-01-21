
import scala.io.Source
import org.tau.dslworkshop.compiler.DSLProgram
import java.io.FileNotFoundException

/* Main standalone interpreter, executed from shell with the name of a file containing the DSL code as a parameter,
 * as well as extra parameters required by the program.
 * Prints the output of the program.
*/
object Main {

  def main(args: Array[String]): Unit = {

    if (args.length == 0) {
      println("Usage:\nMain <DSL code file name> <Program arguments>")
      return
    }
    val code = try {
      Source.fromFile(args(0)).mkString
    } catch {
      case ex: FileNotFoundException => {
        println("File " + args(0) + " Not Found.")
        return
      }
    }
    val params = args.drop(1)
    val instance = new DSLProgram(code)("main_window")

    val output = instance(params)

    println(output)
  }

}
