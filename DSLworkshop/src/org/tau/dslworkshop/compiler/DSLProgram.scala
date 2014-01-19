package org.tau.dslworkshop.compiler

import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Text
import org.tau.dslworkshop.compiler.exceptions.ParsingError
import org.tau.workshop2011.parser.AST.AtomicWidget
import org.tau.workshop2011.parser.AST.InitialAttribute
import org.tau.workshop2011.parser.AST.Literal
import org.tau.workshop2011.parser.AST.Variable
import org.tau.workshop2011.parser.LayoutParser

class DSLProgram(code: String) {

  private val display = new Display

  private val widgetsMap = LayoutParser parseAll (LayoutParser.Program, code) match {
    case LayoutParser.Success(result, nextInput) =>
      //      print(result)
      result.defs.toMap
    case LayoutParser.NoSuccess(msg, nextInput) => throw new ParsingError(msg, nextInput.pos.line, nextInput.pos.column)
  }

  class DSLObject protected[DSLProgram] (name: String, title: String, icon: String, isDialog: Boolean, isMaximized: Boolean, defaultWidth: Int, defaultHeight: Int) {

    private val window = new Shell(display, if (isDialog) SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL else SWT.SHELL_TRIM)

    private var evaluatedVarMap = new TEvaluatedVarMap()

    private var unevaluatedVarMap = new TUnevaluatedVarMap()

    private var extensions: TExtensions = Map()

    private val keyMap = new mutableHashMap[Char, Boolean]()

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
      extensions += varName -> action
    }

    def onKeyPress(action: Char => Unit) {
      display.addFilter(SWT.KeyDown, new Listener {
        override def handleEvent(event: Event) {
          if (!event.widget.isInstanceOf[Text] && (!keyMap.contains(event.character) || !keyMap(event.character))) {
            action(event.character)
            keyMap(event.character) = true
          }
        }
      })
    }

    def onKeyRelease(action: Char => Unit) {
      display.addFilter(SWT.KeyUp, new Listener {
        override def handleEvent(event: Event) {
          if (!event.widget.isInstanceOf[Text])
            action(event.character)
          keyMap(event.character) = false
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
      val (width, height, isWidthQM, isHeightQM, changeWindowSize) =
        scope.evalNode(mainWidget, window, new Environment(evaluatedVarMap, unevaluatedVarMap))
      window setLayout new FillLayout
      println(width, height, defaultWidth, defaultHeight)
      window.getChildren()(0).setSize(if (isWidthQM) defaultWidth else width, if (isHeightQM) defaultHeight else height)
      window.pack
      window.setMaximized(isMaximized)
      window.setText(title)
      if (icon != null)
        window.setImage(new Image(display, icon))
      window.open
      while (!window.isDisposed) {
        if (!display.readAndDispatch) {
          display.sleep
        }
      }
      mainWidget.attributes.map(att => att.getName + "=" + scope.getParams(att.getName)).mkString(" ")
    }

  }

  def apply(name: String, title: String = "", icon: String = null, isDialog: Boolean = false, isMaximized: Boolean = false, defaultWidth: Int = 500, defaultHeight: Int = 500) =
    new DSLObject(name, title, icon, isDialog, isMaximized, defaultWidth, defaultHeight)

  override def finalize {
    display.dispose
  }

}