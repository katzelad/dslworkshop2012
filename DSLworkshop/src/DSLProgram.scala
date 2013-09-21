import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Display
import org.tau.workshop2011.parser.LayoutParser
import org.eclipse.swt.events.ControlAdapter
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.custom.ScrolledComposite
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.events.ControlEvent
import org.eclipse.swt.SWT
import scala.collection.immutable.HashSet
import scala.collection.mutable.Map

class DSLProgram(code: String) {
  val display = new Display
  val widgetsMap = LayoutParser parseAll (LayoutParser.Program, code) match {
    case LayoutParser.Success(result, nextInput) => result.defs.toMap //print(result) 
    case LayoutParser.NoSuccess(msg, nextInput) =>
      println("Could not parse the input.");
      println(msg)
      null
  }
  class DSLObject protected[DSLProgram] (name: String) {
    val window = new Shell(display)
    var bindedFunctionsMap = Map[String, Any]()
    var evaluatedVarMap = Map[String, Any]()
    var unevaluatedVarMap = Map[String, Set[() => Unit]]()
    val widget = widgetsMap get name match {
      case Some(widget) => widget
      case None => print("Error: " + name + " not found.")
      null // TODO Exception
    }
    def set(varName: String, value: Any) {
      evaluatedVarMap(varName) = value
    }

    def bind(name: String, value: Any) { //TODO not good - functionToAdd should be able to have multiple parameters, and we don't know how many and of which type
      bindedFunctionsMap(name) = value
    }

    def when_changed(varName: String, func: () => Unit) {
      if (!unevaluatedVarMap.contains(varName))
        unevaluatedVarMap(varName) = new HashSet[() => Unit]
      unevaluatedVarMap(varName) += func
    }

    def apply(parametersList: Map[String, Any]) {
      Main.evalCode(widget, window, parametersList, unevaluatedVarMap, evaluatedVarMap)
      window.open
      while (!window.isDisposed) {
        if (!display.readAndDispatch) {
          display.sleep
        }
      }
      display.dispose
    }

  }
  def apply(name: String) = {
    new DSLObject(name)
  }
}