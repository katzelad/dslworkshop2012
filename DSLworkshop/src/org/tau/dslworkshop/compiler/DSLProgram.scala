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

/* Main procedural API class 
 * Receives the DSL code as a parameter, use 'apply' to run.
 */
class DSLProgram(code: String) {

  private val display = new Display

  private val widgetsMap = LayoutParser parseAll (LayoutParser.Program, code) match {
    case LayoutParser.Success(result, nextInput) => result.defs.toMap
    case LayoutParser.NoSuccess(msg, nextInput) => throw new ParsingError(msg, nextInput.pos.line, nextInput.pos.column)
  }

  /* This class represents an instance of a subprogram of DSLProgram.
   * It is created using the 'apply' method of DSLProgram.
   * After instantiation, different API method should be applied, followed by 'apply' to execute the subprogram.
   */
  class DSLObject protected[DSLProgram] (
    name: String,
    title: String,
    icon: String,
    isDialog: Boolean,
    isMaximized: Boolean,
    defaultWidth: Int,
    defaultHeight: Int) {

    private val window = new Shell(display, if (isDialog) SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL else SWT.SHELL_TRIM)

    private var varMap = new TVarMap()

    private var flowMap = new TFlowMap()

    private var extensions: TExtensions = Map()

    private val keyMap = new mutableMap[Char, Boolean]()

    private val widget = widgetsMap get name match {
      case Some(widget) => widget
      case None => throw new Exception("Error: " + name + " not found.")
    }
    
    /*
     * Changes the value of a variable of 'main_window', meaning, given as a parameter to the subprogram.
     * Receives the name of the variable and its new value.
     * Can only be used while the program is running.
     */
    def set(varName: String, value: Any) {
      varMap(varName) = value
      flowMap(varName) foreach (_())
    }

    /*
     * Binds a function in the DSL code to its implementation.
     * Receives the name of the function and its implementation.
     * The function has to receive a parameters list of type 'Any *'.
     */
    def bind(name: String, value: Any) {
      varMap.put(name, value)
    }

    /*
     * Adds an observer to a variable.
     * Receives the variable name and notification function,
     * which accepts the values of the variable before and after the modification.
     * The observer is notified every time a variable is changed.
     */
    def when_changed(varName: String, action: (Any, Any) => Unit) {
      extensions += varName -> action
    }

    /*
     * Adds a key listener to the program (a feature required by our application).
     * The listener accepts the character of the key pressed, and is called upon every key press.
     */
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

    /*
     * Adds a key listener to the program (a feature required by our application).
     * The listener accepts the character of the key released, and is called upon every key releases.
     */
    def onKeyRelease(action: Char => Unit) {
      display.addFilter(SWT.KeyUp, new Listener {
        override def handleEvent(event: Event) {
          if (!event.widget.isInstanceOf[Text])
            action(event.character)
          keyMap(event.character) = false
        }
      })
    }

    /*
     * Executes the subprogram.
     * Receives the arguments list (which must include all variables provided to 'set').
     * The arguments must be strings (enclosed in quotation marks) or numbers.
     * Returns the output of the program - A string of the arguments' names and values.
     */
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
        scope.evalNode(mainWidget, window, new Environment(varMap, flowMap))
      window setLayout new FillLayout
      window.getChildren()(0).setSize(if (isWidthQM) defaultWidth else width, if (isHeightQM) defaultHeight else height)
      window.pack
      window.setSize(window.getSize.x - 17, window.getSize.y - 17)
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

  /*
   * Creates a subprogram.
   * Receives the name of the subexpression (to look for in the code) and some customization options for the window.
   * Returns the created subprogram for processing and execution.
   */
  def apply(name: String, title: String = "", icon: String = null, isDialog: Boolean = false, isMaximized: Boolean = false, defaultWidth: Int = 500, defaultHeight: Int = 500) =
    new DSLObject(name, title, icon, isDialog, isMaximized, defaultWidth, defaultHeight)

  override def finalize {
    display.dispose
  }

}