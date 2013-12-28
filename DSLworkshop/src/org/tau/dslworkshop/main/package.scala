package org.tau.dslworkshop

import org.tau.workshop2011.expressions._
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.graphics.{ Color => swtColor }
import org.eclipse.swt.graphics.{ Font => swtFont }
import org.eclipse.swt.SWT

package object main {

  type TEvaluatedVarMap = ScopingMap[String, Any]

  type TUnevaluatedVarMap = ScopingMap[String, Set[() => Unit]]
  
  type TChangeSize = (Int, Int, Int, Int) => Unit

  type TEvalNodeReturn = (Int, Int, Boolean, Boolean, TChangeSize)

  val SASH_WIDTH = 5

  val INITIAL_ATT_FLAG = () => {}

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