package org.tau.dslworkshop.main

import scala.collection.immutable.HashSet
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell
import org.tau.workshop2011.parser.LayoutParser
import org.eclipse.swt.layout.FillLayout

class DSLProgram(code: String) {
  
  val display = new Display
  
  val widgetsMap = LayoutParser parseAll (LayoutParser.Program, code) match {
    case LayoutParser.Success(result, nextInput) => result.defs.toMap // print(result) 
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

    def when_changed(varName: String, func: (/*Any, Any*/) => Unit) {
      if (!unevaluatedVarMap.contains(varName))
        unevaluatedVarMap(varName) = new HashSet[() => Unit]
      unevaluatedVarMap(varName) += func
    }

    def apply(parametersList: TEvaluatedVarMap) {
      parametersList.foreach({ case (name, value) => evaluatedVarMap(name) = value })
      new LayoutScope(widgetsMap).evalNode(widget, window, new Environment(evaluatedVarMap, unevaluatedVarMap))
      window setLayout new FillLayout
      // window.setSize(1000, 500)
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