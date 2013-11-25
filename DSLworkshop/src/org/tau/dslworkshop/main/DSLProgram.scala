package org.tau.dslworkshop.main

import scala.collection.immutable.HashSet
import scala.collection.mutable.Map
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell
import org.tau.workshop2011.parser.LayoutParser

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

    def apply(parametersList: Map[String, Any]) {
      evalNode(widget, window, unevaluatedVarMap, parametersList ++ evaluatedVarMap)
      window.setSize(1000, 500)
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