package org.tau.dslworkshop

import org.tau.workshop2011.expressions._
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.graphics.{ Color => swtColor }
import org.eclipse.swt.graphics.{ Font => swtFont }
import org.eclipse.swt.SWT

/*
 * Global declarations
 */
package object compiler {

  // A variable -> value mapping of a specific scope
  type TVarMap = ScopingMap[String, Any]

  // A variable -> (internal) observers mapping of a specific scope
  type TFlowMap = ScopingMap[String, Set[() => Unit]]
  
  // A function which dynamically changes the size of a box, used in 'LayoutScope.scala'
  type TChangeSize = (Int, Int, Int, Int) => Unit

  // A tuple of the values returned by EvalNode, used in 'LayoutScope.scala'
  type TEvalNodeReturn = (Int, Int, Boolean, Boolean, TChangeSize)
 
  // A variable -> extension (an external observer which is registered using 'when_changed') mapping of a subprogram
  type TExtensions = Map[String, (Any, Any) => Unit]
  
  // An abbreviation for a mutable hash map
  type mutableMap[K, V] = scala.collection.mutable.HashMap[K, V]

  // A constant for the width of a splitter
  val SASH_WIDTH = 7

  // A flag which is inserted to the 'Flow Map' to distinguish a constant from a variable
  val INITIAL_ATT_FLAG = () => {}
  
  // An image to be displayed if the specified image was not found
  val ERROR_IMAGE = "Graphics\\error.png"

  /*
   * Converters
   */
  def colorASTToSWT(astColor: Color, display: Display) =
    new swtColor(display, astColor.red, astColor.green, astColor.blue)

  def fontASTToSWT(astFont: Font, display: Display) =
    new swtFont(display, astFont.face, astFont.size, (astFont.style: @unchecked) match {
      case TextStyle.bold => SWT.BOLD
      case TextStyle.italic => SWT.ITALIC
      case TextStyle.regular => SWT.NORMAL
    })

  def hAlignASTToSWT(astHAlign: HAlign) = (astHAlign: @unchecked) match {
    case HAlign.left => SWT.LEFT
    case HAlign.center => SWT.CENTER
    case HAlign.right => SWT.RIGHT
  }
  
}