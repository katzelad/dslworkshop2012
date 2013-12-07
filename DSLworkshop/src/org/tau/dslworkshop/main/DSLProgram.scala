package org.tau.dslworkshop.main

import scala.collection.immutable.HashSet
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell
import org.tau.workshop2011.parser.LayoutParser
import org.tau.workshop2011.parser.AST.AtomicWidget
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.events.ControlAdapter
import org.eclipse.swt.events.ControlEvent
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.SWT
import org.tau.workshop2011.parser.AST.InitialAttribute
import org.tau.workshop2011.parser.AST.Variable
import org.tau.workshop2011.parser.AST.Literal

class DSLProgram(code: String) {

  val display = new Display

  val widgetsMap = LayoutParser parseAll (LayoutParser.Program, code) match {
    case LayoutParser.Success(result, nextInput) =>
      print(result); result.defs.toMap
    case LayoutParser.NoSuccess(msg, nextInput) => throw new Exception("Could not parse the input.\n" + msg)
  }

  class DSLObject protected[DSLProgram] (name: String) {

    val window = new Shell(display)

    var evaluatedVarMap = new TEvaluatedVarMap()

    var unevaluatedVarMap = new TUnevaluatedVarMap()

    val widget = widgetsMap get name match {
      case Some(widget) => widget
      case None => throw new Exception("Error: " + name + " not found.")
    }

    def set(varName: String, value: Any) {
      evaluatedVarMap(varName) = value
      unevaluatedVarMap(varName) foreach (_())
    }

    def bind(name: String, value: Any) {
      evaluatedVarMap(name) = value
    }

    def when_changed(varName: String, func: ( /*Any, Any*/ ) => Unit) {
      if (!unevaluatedVarMap.contains(varName))
        unevaluatedVarMap(varName) = new HashSet[() => Unit]
      unevaluatedVarMap(varName) += func
    }

    def apply(args: Array[String]) {
      //      parametersList.foreach({ case (name, value) => evaluatedVarMap(name) = value })
      val mainWidget = AtomicWidget(name, args.toList.map(arg => {
        val argName = Variable(arg.take(arg.indexOf("=")))
        val argValueString = arg.drop(arg.indexOf("=") + 1)
        val argValue = Some(Literal(
          if (argValueString.startsWith("\"") && argValueString.endsWith("\""))
            argValueString.replace("\"", "")
          else
            argValueString.asInstanceOf[Int]))
        InitialAttribute(argName, argValue)
      }), None, None)
      val (_, _, _, _, changeWindowSize) = new LayoutScope(widgetsMap).evalNode(mainWidget, window, new Environment(evaluatedVarMap, unevaluatedVarMap))
      //      changeWindowSize(0, 0, window.getSize.x, window.getSize.y)
      window setLayout new FillLayout
      window.open
      while (!window.isDisposed) {
        if (!display.readAndDispatch) {
          display.sleep
        }
      }
      display.dispose
    }

  }

  def apply(name: String) = new DSLObject(name)

}