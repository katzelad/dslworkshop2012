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
import org.eclipse.swt.events.KeyAdapter
import org.eclipse.swt.events.KeyEvent
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.Event

class DSLProgram(code: String) {

  private val display = new Display

  private val widgetsMap = LayoutParser parseAll (LayoutParser.Program, code) match {
    case LayoutParser.Success(result, nextInput) =>
      //      print(result)
      result.defs.toMap
    case LayoutParser.NoSuccess(msg, nextInput) => throw new ParsingError(msg, nextInput.pos.line, nextInput.pos.column)
  }

  class DSLObject protected[DSLProgram] (name: String) {

    private val window = new Shell(display)

    private var evaluatedVarMap = new TEvaluatedVarMap()

    private var unevaluatedVarMap = new TUnevaluatedVarMap()

    private var extensions: TExtensions = Map()

    private val widget = widgetsMap get name match {
      case Some(widget) => widget
      case None => throw new Exception("Error: " + name + " not found.")
    }

    def set(varName: String, value: Any) {
      evaluatedVarMap(varName) = value
      unevaluatedVarMap(varName) foreach (_())
    }

    def bind(name: String, value: Any) {
      evaluatedVarMap.put(name, value)
    }

    def when_changed(varName: String, action: (Any, Any) => Unit) {
      //      if (!unevaluatedVarMap.contains(varName))
      //        unevaluatedVarMap.put(varName, new HashSet[() => Unit])
      extensions += varName -> action
    }

    def onKey(action: Char => Unit) {
      display.addFilter(SWT.KeyDown, new Listener {
        override def handleEvent(event: Event) {
          action(event.character)
        }
      })
    }

    def apply(args: Array[String]) = {
      val mainWidget = AtomicWidget(name, args.toList.map(arg => {
        val argName = Variable(arg.take(arg.indexOf("=")))
        val argValueString = arg.drop(arg.indexOf("=") + 1)
        val argValue = Some(
          if (argValueString.startsWith("\"") && argValueString.endsWith("\""))
            Literal(argValueString.replace("\"", ""))
          else
            Literal(argValueString.toInt))
        InitialAttribute(argName, argValue)
      }), None, None)
      val scope = new LayoutScope(widgetsMap, extensions)
      val (width, height, isWidthQM, isHeightQM, changeWindowSize) = scope.evalNode(mainWidget, window, new Environment(evaluatedVarMap, unevaluatedVarMap))
      window setLayout new FillLayout
      window.getChildren()(0).setSize(width, height) // TODO add code to handle limitation on window size when not '?'
      window.pack
      window.open
      while (!window.isDisposed) {
        if (!display.readAndDispatch) {
          display.sleep
        }
      }
      display.dispose
      mainWidget.attributes.map(att => att.getName + "=" + scope.getParams(att.getName)).mkString(" ")
    }

  }

  def apply(name: String) = new DSLObject(name)

}