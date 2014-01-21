
import scala.io.Source
import org.tau.dslworkshop.compiler.DSLProgram
import org.tau.workshop2011.expressions.Color

// Main program, executed from shell with the name of a file containing the DSL code as a parameter
object Main {

  def main(args: Array[String]) = {

    val code = org.tau.dslworkshop.Notepad() // Source.fromFile(args(0)).mkString

    val params = args.drop(1)
    val instance = new DSLProgram(code)("main_window")
  
    println(params.mkString("{", " ", "}"))

    val output = instance(params)

    println(output)

  }

}
