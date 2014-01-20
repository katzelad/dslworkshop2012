
import scala.io.Source
import org.tau.dslworkshop.compiler.DSLProgram
import org.tau.workshop2011.expressions.Color

object Main {

  def main(args: Array[String]) = {

    val code = org.tau.dslworkshop.Notepad() // Source.fromFile(args(0)).mkString

    val params = args.drop(1)
    val instance = new DSLProgram(code)("main_window")

    instance.bind("EmailSender", (_: Seq[Any]) => "Neta Katz")
    instance.bind("EEfgcol", (_: Seq[Any]) => new Color("0xFF0000"))
    instance.bind("EEbgcol", (_: Seq[Any]) => new Color("0x00FF00"))
    instance.bind("EmailSubject", (_: Seq[Any]) => "Piggish slippers")
    instance.bind("EmailContent", (_: Seq[Any]) => "I WANT MY PIGGISH SLIPPERS")

    println(params.mkString("{", " ", "}"))

    val output = instance(params)

    println(output)

  }

}