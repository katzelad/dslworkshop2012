package org.tau.dslworkshop.main

import scala.collection.mutable.{ Buffer => mutableBuffer }
import scala.collection.mutable.{ Map => mutableMap }
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.ScrolledComposite
import org.eclipse.swt.events.ControlAdapter
import org.eclipse.swt.events.ControlEvent
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.{ Color => swtColor }
import org.eclipse.swt.graphics.{ Font => swtFont }
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Combo
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.Sash
import org.eclipse.swt.widgets.Slider
import org.eclipse.swt.widgets.Text
import org.tau.workshop2011.expressions.Color
import org.tau.workshop2011.expressions.Font
import org.tau.workshop2011.expressions.HAlign
import org.tau.workshop2011.expressions.TextStyle
import org.tau.workshop2011.parser.AST.ASTNode
import org.tau.workshop2011.parser.AST.AtomicWidget
import org.tau.workshop2011.parser.AST.Container
import org.tau.workshop2011.parser.AST.ExpressionAttribute
import org.tau.workshop2011.parser.AST.InitialAttribute
import org.tau.workshop2011.parser.AST.IterationMacro
import org.tau.workshop2011.parser.AST.PropertyScope
import org.tau.workshop2011.parser.AST.Widget
import org.tau.workshop2011.parser.AST.Expr
import org.eclipse.swt.widgets.Group
import org.eclipse.swt.graphics.ImageData
import org.tau.workshop2011.parser.AST.Literal
import org.tau.workshop2011.parser.AST.Variable

class LayoutScope(widgetsMap: Map[String, Widget]) {

  private var params: TEvaluatedVarMap = null

  private var varsAffectedByCurrentUpdate: Set[String] = null

  def getParams = params

  private def isReservedAtrribute(att: String) = att match {
    case "halign" => true
    case "text" => true
    case "checked" => true
    case "filename" => true
    case "value" => true
    case "maxvalue" => true
    case "minvalue" => true
    case "enabled" => true
    case "fgcolor" => true
    case "bgcolor" => true
    case "font" => true
    case _ => false
  }

  private def createSash(parent: Composite, direction: Int) = new Sash(parent, direction | SWT.SMOOTH | SWT.BORDER)

  private def handleHorizontalContainer(parent: Composite, env: Environment, children: List[Widget]): TEvalNodeReturn = {
    var seenQM = 0
    var sashes = mutableBuffer[Sash]()
    val childInfo = children map (evalNode(_, parent, env))
    val qms = childInfo count { case (_, _, b, _, _) => b } // total number of question marks (qms)
    val numWidth = childInfo map { case (w, _, _, _, _) => w } sum
    var sashMap = mutableMap[Sash, Double]()
    var prevSashMap = Map[Sash, (Option[Sash], Int)]()
    var nextSashMap = Map[Sash, (Option[Sash], Int)]()
    var sashLeftBounds = mutableMap[Sash, Int]()
    var sashRightBounds = mutableMap[Sash, Int]()
    var changeSizes = List[((Int, Int, Int, Int) => Unit, Option[Sash], Option[Int], Option[Sash], Option[Int], Option[Int])]()
    var j = 0
    var isChangingSize = false
    for (i <- 0 to childInfo.length - 1) {
      childInfo(i) match { //i is the right mark
        case (_, _, true, _, qmChangeSize) =>
          if (seenQM > 0) { // ... ? ... ? ...
            val leftSash = createSash(parent, SWT.VERTICAL)
            (childInfo(j): @unchecked) match {
              case (_, _, true, _, changeSize) =>
                changeSizes ::= (changeSize, if (sashes isEmpty) None else Some(sashes last),
                  if (sashes isEmpty)
                    Some(childInfo take (childInfo prefixLength { case (_, _, b, _, _) => !b })
                    map ({ case (w, _, false, _, _) => w; case _ => print("Error: 134"); 0 }) sum)
                  else
                    Some(0), Some(leftSash), Some(0), None)
            }
            prevSashMap += leftSash -> (if (sashes.isEmpty) None else Some(sashes.last),
              if (sashes.isEmpty)
                childInfo takeWhile { case (_, _, b, _, _) => !b } map { case (w, _, false, _, _) => w; case _ => print("Error: 143"); 0 } sum
              else 0) // MARK 8.12, originally without adding the childInfo(j)._1
            sashMap(leftSash) = 1.0 / qms
            sashes += leftSash
            if (i > j + 1) { // ... ? 20 20 20 ? ...
              val rightSash = createSash(parent, SWT.VERTICAL)
              sashes += rightSash
              var accWidth = 0
              for (j <- j + 1 to i - 1)
                childInfo(j) match {
                  case (width, _, false, _, changeSize) =>
                    changeSizes ::= (changeSize, Some(leftSash), Some(accWidth), None, None, Some(width))
                    accWidth += width
                  case _ => print("Error: 148")
                }
              prevSashMap += rightSash -> (Some(leftSash), accWidth)
              sashMap(rightSash) = 0
              val k = j
              val sashDist = accWidth
              leftSash addControlListener new ControlAdapter {
                override def controlMoved(event: ControlEvent) {
                  if (!isChangingSize) {
                    val leftBound = prevSashMap(leftSash) match {
                      case (Some(prevSash), leftMargin) => prevSash.getBounds.x + SASH_WIDTH + leftMargin
                      case (None, leftMargin) => sashLeftBounds(leftSash) + leftMargin
                    }
                    if (leftSash.getBounds.x < leftBound + childInfo(k)._1)
                      leftSash.setLocation(leftBound + childInfo(k)._1, leftSash.getBounds.y)
                    val rightBound = nextSashMap(rightSash) match {
                      case (Some(nextSash), rightMargin) => nextSash.getBounds.x - rightMargin - sashDist - SASH_WIDTH
                      case (None, rightMargin) => sashRightBounds(rightSash) - rightMargin - sashDist - SASH_WIDTH
                    }
                    if (leftSash.getBounds.x + SASH_WIDTH > rightBound - childInfo(i)._1)
                      leftSash.setLocation(rightBound - SASH_WIDTH - childInfo(i)._1, leftSash.getBounds.y)
                    (childInfo(k): @unchecked) match {
                      case (_, _, true, _, changeSize) => changeSize(leftBound /*BEGIN CHANGE 8.12 - prevSashMap(leftSash)._2 END CHANGE 8.12 */ , leftSash.getBounds.y, leftSash.getBounds.x, leftSash.getBounds.y + leftSash.getBounds.height)
                    }
                    var accWidth = 0
                    for (j <- k + 1 to i - 1) childInfo(j) match {
                      case (width, _, false, _, changeSize) =>
                        changeSize(leftSash.getBounds.x + SASH_WIDTH + accWidth, leftSash.getBounds.y, leftSash.getBounds.x + SASH_WIDTH + accWidth + width, leftSash.getBounds.y + leftSash.getBounds.height)
                        accWidth += width
                      case _ => print("Error: 184")
                    }
                    rightSash.setLocation(leftSash.getBounds.x + SASH_WIDTH + accWidth, leftSash.getBounds.y)
                    (childInfo(i): @unchecked) match {
                      case (_, _, true, _, changeSize) => changeSize(rightSash.getBounds.x + SASH_WIDTH, leftSash.getBounds.y, rightBound + accWidth + SASH_WIDTH, leftSash.getBounds.y + leftSash.getBounds.height)
                    }
                    sashMap(leftSash) = (leftSash.getBounds.x - leftBound) * 1.0 / (sashRightBounds(leftSash) - sashLeftBounds(leftSash) - numWidth - sashes.length * SASH_WIDTH)
                    nextSashMap(rightSash) match {
                      case (Some(nextSash), _) => sashMap(nextSash) = (rightBound + accWidth - rightSash.getBounds.x) * 1.0 / (sashRightBounds(leftSash) - sashLeftBounds(leftSash) - numWidth - sashes.length * SASH_WIDTH)
                      case _ =>
                    }
                  }
                }
              }
              rightSash addControlListener new ControlAdapter {
                override def controlMoved(event: ControlEvent) {
                  if (!isChangingSize) {
                    val leftBound = prevSashMap(leftSash) match {
                      case (Some(prevSash), leftMargin) => prevSash.getBounds.x + 2 * SASH_WIDTH + leftMargin + sashDist
                      case (None, leftMargin) => sashLeftBounds(rightSash) + leftMargin + SASH_WIDTH + sashDist
                    }
                    if (rightSash.getBounds.x < leftBound + childInfo(k)._1)
                      rightSash.setLocation(leftBound + childInfo(k)._1, rightSash.getBounds.y)
                    val rightBound = nextSashMap(rightSash) match {
                      case (Some(nextSash), rightMargin) => nextSash.getBounds.x - rightMargin
                      case (None, rightMargin) => sashRightBounds(rightSash) - rightMargin
                    }
                    if (rightSash.getBounds.x + SASH_WIDTH > rightBound - childInfo(i)._1)
                      rightSash.setLocation(rightBound - SASH_WIDTH - childInfo(i)._1, rightSash.getBounds.y)
                    (childInfo(i): @unchecked) match {
                      case (_, _, true, _, changeSize) => changeSize(rightSash.getBounds.x + SASH_WIDTH, rightSash.getBounds.y, rightBound, rightSash.getBounds.y + rightSash.getBounds.height)
                    }
                    var accWidth = 0
                    for (j <- i - 1 to k + 1 by -1) childInfo(j) match {
                      case (width, _, false, _, changeSize) =>
                        changeSize(rightSash.getBounds.x - accWidth - width, rightSash.getBounds.y, rightSash.getBounds.x - accWidth, rightSash.getBounds.y + rightSash.getBounds.height)
                        accWidth += width
                      case _ => print("Error: 215")
                    }
                    leftSash.setLocation(rightSash.getBounds.x - SASH_WIDTH - accWidth, rightSash.getBounds.y)
                    (childInfo(k): @unchecked) match {
                      case (_, _, true, _, changeSize) => changeSize(leftBound - accWidth - SASH_WIDTH, rightSash.getBounds.y, leftSash.getBounds.x, rightSash.getBounds.y + rightSash.getBounds.height)
                    }
                    sashMap(leftSash) = (leftSash.getBounds.x - leftBound + accWidth + SASH_WIDTH) * 1.0 / (sashRightBounds(leftSash) - sashLeftBounds(leftSash) - numWidth - sashes.length * SASH_WIDTH)
                    nextSashMap(rightSash) match {
                      case (Some(nextSash), _) => sashMap(nextSash) = (rightBound - rightSash.getBounds.x - SASH_WIDTH) * 1.0 / (sashRightBounds(rightSash) - sashLeftBounds(rightSash) - numWidth - sashes.length * SASH_WIDTH)
                      case _ =>
                    }
                  }
                }
              }
              if (i == childInfo.length - 1)
                (childInfo(i): @unchecked) match {
                  case (_, _, true, _, changeSize) =>
                    changeSizes ::= (changeSize, Some(leftSash), Some(accWidth + SASH_WIDTH), None, Some(0), None)
                }
            } else {
              if (i == childInfo.length - 1)
                (childInfo(i): @unchecked) match {
                  case (_, _, true, _, changeSize) =>
                    changeSizes ::= (changeSize, Some(leftSash), Some(0), None, Some(0), None)
                }
              leftSash addControlListener new ControlAdapter {
                override def controlMoved(event: ControlEvent) {
                  if (!isChangingSize) {
                    val leftBound = prevSashMap(leftSash) match {
                      case (Some(prevSash), leftMargin) => prevSash.getBounds.x + SASH_WIDTH + leftMargin
                      case (None, leftMargin) => sashLeftBounds(leftSash) + leftMargin
                    }
                    if (leftSash.getBounds.x < leftBound + childInfo(i - 1)._1) // MARK 9.12 - added 'childInfo(i-1)._1'
                      leftSash.setLocation(leftBound + childInfo(i - 1)._1, leftSash.getBounds.y) // MARK 9.12 - added 'childInfo(i-1)._1'
                    val rightBound = nextSashMap(leftSash) match {
                      case (Some(nextSash), rightMargin) => nextSash.getBounds.x - rightMargin
                      case (None, rightMargin) => sashRightBounds(leftSash) - rightMargin
                    }
                    if (leftSash.getBounds.x + SASH_WIDTH > rightBound - childInfo(i)._1)
                      leftSash.setLocation(rightBound - SASH_WIDTH - childInfo(i)._1, leftSash.getBounds.y)
                    (childInfo(i - 1): @unchecked) match {
                      case (_, _, true, _, changeSize) => changeSize(leftBound, leftSash.getBounds.y, leftSash.getBounds.x, leftSash.getBounds.y + leftSash.getBounds.height)
                    }
                    (childInfo(i): @unchecked) match {
                      case (_, _, true, _, changeSize) => changeSize(leftSash.getBounds.x + SASH_WIDTH, leftSash.getBounds.y, rightBound, leftSash.getBounds.y + leftSash.getBounds.height)
                    }
                    //println(currRight)
                    sashMap(leftSash) = (leftSash.getBounds.x - leftBound) * 1.0 / (sashRightBounds(leftSash) - sashLeftBounds(leftSash) - numWidth - sashes.length * SASH_WIDTH)
                    nextSashMap(leftSash) match {
                      case (Some(nextSash), _) => sashMap(nextSash) = (rightBound - leftSash.getBounds.x - SASH_WIDTH) * 1.0 / (sashRightBounds(leftSash) - sashLeftBounds(leftSash) - numWidth - sashes.length * SASH_WIDTH)
                      case _ =>
                    }
                  }
                }
              }
            }
          } else { // 20 20 ? ...
            var accWidth = 0
            for (j <- 0 to i - 1)
              childInfo(j) match {
                case (width, _, false, _, changeSize) =>
                  changeSizes ::= (changeSize, None, Some(accWidth), None, None, Some(width))
                  accWidth += width
                case _ => print("Error: 158")
              }
            if (i == childInfo.length - 1)
              (childInfo(i): @unchecked) match {
                case (_, _, true, _, changeSize) =>
                  changeSizes ::= (changeSize, None, Some(accWidth), None, Some(0), None)
              }
          }
          j = i
          seenQM += 1
        case (width, _, false, _, _) =>
          if (j == 0 && i == childInfo.length - 1) { // 20 20 20
            var accWidth = 0
            for (j <- i to 0 by -1)
              childInfo(j) match {
                case (width, _, false, _, changeSize) =>
                  changeSizes ::= (changeSize, None, None, None, Some(accWidth), Some(width))
                  accWidth += width
                case (_, _, true, _, changeSize) =>
                  changeSizes ::= (changeSize, None, Some(0), None, Some(accWidth), None)
              }
          } else if (i == childInfo.length - 1) { // ... ? 20 20
            var accWidth = 0
            for (j <- i to j + 1 by -1)
              childInfo(j) match {
                case (width, _, false, _, changeSize) =>
                  changeSizes ::= (changeSize, None, None, None, Some(accWidth), Some(width))
                  accWidth += width
                case _ => print("Error: 181")
              }
            (childInfo(j): @unchecked) match {
              case (_, _, true, _, changeSize) =>
                if (seenQM > 1)
                  changeSizes ::= (changeSize, Some(sashes.last), Some(0), None, Some(accWidth), None)
                else
                  changeSizes ::= (changeSize, None, Some(numWidth - accWidth), None, Some(accWidth), None)
            }
          }
      }
    }
    for ((sash, (prevSash, const)) <- prevSashMap) prevSash match {
      case Some(prevSash) => nextSashMap += prevSash -> (Some(sash), const)
      case _ =>
    }
    if (!sashes.isEmpty)
      nextSashMap += sashes.last -> (None, (childInfo reverse) takeWhile { case (_, _, b, _, _) => !b } map { case (w, _, false, _, _) => w; case _ => 0 } sum)
    val totalWidth = numWidth + sashes.length * SASH_WIDTH
    val height = childInfo map { case (_, h, _, _, _) => h } max
    val isHeightQM = childInfo.count({ case (_, _, _, isQM, _) => !isQM }) == 0
    (totalWidth, height, seenQM > 0, isHeightQM, (left: Int, top: Int, right: Int, bottom: Int) => {
      sashes foreach (sash => {
        sashLeftBounds(sash) = left
        sashRightBounds(sash) = right
      })
      isChangingSize = true
      sashes foreach (sash => {
        val (prevSash, constMargin) = prevSashMap(sash)
        val leftMargin = (prevSash map (s => s.getBounds.x + s.getBounds.width) getOrElse left) + constMargin + sashMap(sash) * (right - left - totalWidth)
        sash.setBounds(leftMargin.toInt, top, SASH_WIDTH, bottom - top)
        // println("sash", sash.getBounds)
      })
      changeSizes foreach {
        case (changeSize, Some(leftSash), Some(leftMargin), None, None, Some(width)) =>
          changeSize(leftSash.getBounds.x + leftSash.getBounds.width + leftMargin, top,
            leftSash.getBounds.x + leftSash.getBounds.width + leftMargin + width, bottom)
        case (changeSize, Some(leftSash), Some(leftMargin), Some(rightSash), Some(rightMargin), None) =>
          changeSize(leftSash.getBounds.x + leftSash.getBounds.width + leftMargin, top,
            rightSash.getBounds.x - rightMargin, bottom)
        case (changeSize, None, Some(leftMargin), Some(rightSash), Some(rightMargin), None) =>
          changeSize(left + leftMargin, top, rightSash.getBounds.x - rightMargin, bottom)
        case (changeSize, None, Some(leftMargin), None, None, Some(width)) =>
          changeSize(left + leftMargin, top, left + leftMargin + width, bottom)
        case (changeSize, None, None, None, Some(rightMargin), Some(width)) =>
          changeSize(right - rightMargin - width, top, right - rightMargin, bottom)
        case (changeSize, Some(leftSash), Some(leftMargin), None, Some(rightMargin), None) =>
          changeSize(leftSash.getBounds.x + leftSash.getBounds.width + leftMargin, top, right - rightMargin, bottom)
        case (changeSize, None, Some(leftMargin), None, Some(rightMargin), None) =>
          changeSize(left + leftMargin, top, right - rightMargin, bottom)
        case _ => print("Error: 217")
      }

      isChangingSize = false
    })
  }

  private def handleVerticalContainer(parent: Composite, env: Environment, children: List[Widget]): TEvalNodeReturn = {
    var seenQM = 0
    var sashes = mutableBuffer[Sash]()
    val childInfo = children map (evalNode(_, parent, env))
    val qms = childInfo count { case (_, _, _, b, _) => b } // total number of question marks (qms)
    val numHeight = childInfo map { case (_, h, _, _, _) => h } sum
    var sashMap = mutableMap[Sash, Double]()
    var prevSashMap = Map[Sash, (Option[Sash], Int)]()
    var nextSashMap = Map[Sash, (Option[Sash], Int)]()
    var sashTopBounds = mutableMap[Sash, Int]()
    var sashBottomBounds = mutableMap[Sash, Int]()
    var changeSizes = List[((Int, Int, Int, Int) => Unit, Option[Sash], Option[Int], Option[Sash], Option[Int], Option[Int])]()
    var j = 0
    var isChangingSize = false
    for (i <- 0 to childInfo.length - 1) {
      childInfo(i) match { //i is the right mark
        case (_, _, _, true, qmChangeSize) =>
          if (seenQM > 0) { // ... ? ... ? ...
            val topSash = createSash(parent, SWT.HORIZONTAL)
            (childInfo(j): @unchecked) match {
              case (_, _, _, true, changeSize) =>
                changeSizes ::= (changeSize, if (sashes isEmpty) None else Some(sashes last),
                  if (sashes isEmpty)
                    Some(childInfo take (childInfo prefixLength { case (_, _, _, b, _) => !b })
                    map ({ case (_, h, _, false, _) => h; case _ => print("Error: 346"); 0 }) sum)
                  else
                    Some(0), Some(topSash), Some(0), None)
            }
            prevSashMap += topSash -> (if (sashes.isEmpty) None else Some(sashes.last),
              if (sashes.isEmpty)
                childInfo takeWhile { case (_, _, _, b, _) => !b } map { case (_, h, _, false, _) => h; case _ => print("Error: 352"); 0 } sum
              else 0) // MARK 8.12, originally without adding the childInfo(j)._1
            sashMap(topSash) = 1.0 / qms
            sashes += topSash
            if (i > j + 1) { // ... ? 20 20 20 ? ...
              val bottomSash = createSash(parent, SWT.HORIZONTAL)
              sashes += bottomSash
              var accHeight = 0
              for (j <- j + 1 to i - 1)
                childInfo(j) match {
                  case (_, height, _, false, changeSize) =>
                    changeSizes ::= (changeSize, Some(topSash), Some(accHeight), None, None, Some(height))
                    accHeight += height
                  case _ => print("Error: 365")
                }
              prevSashMap += bottomSash -> (Some(topSash), accHeight)
              sashMap(bottomSash) = 0
              val k = j
              val sashDist = accHeight
              topSash addControlListener new ControlAdapter {
                override def controlMoved(event: ControlEvent) {
                  if (!isChangingSize) {
                    val topBound = prevSashMap(topSash) match {
                      case (Some(prevSash), topMargin) => prevSash.getBounds.y + SASH_WIDTH + topMargin
                      case (None, topMargin) => sashTopBounds(topSash) + topMargin
                    }
                    if (topSash.getBounds.y < topBound + childInfo(k)._2)
                      topSash.setLocation(topSash.getBounds.x, topBound + childInfo(k)._2)
                    val bottomBound = nextSashMap(bottomSash) match {
                      case (Some(nextSash), bottomMargin) => nextSash.getBounds.y - bottomMargin - sashDist - SASH_WIDTH
                      case (None, bottomMargin) => sashBottomBounds(bottomSash) - bottomMargin - sashDist - SASH_WIDTH
                    }
                    if (topSash.getBounds.y + SASH_WIDTH > bottomBound - childInfo(i)._2)
                      topSash.setLocation(topSash.getBounds.x, bottomBound - SASH_WIDTH - childInfo(i)._2)
                    (childInfo(k): @unchecked) match {
                      case (_, _, _, true, changeSize) => changeSize(topSash.getBounds.x, topBound, topSash.getBounds.x + topSash.getBounds.width, topSash.getBounds.y)
                    }
                    var accHeight = 0
                    for (j <- k + 1 to i - 1) childInfo(j) match {
                      case (_, height, _, false, changeSize) =>
                        changeSize(topSash.getBounds.x, topSash.getBounds.y + SASH_WIDTH + accHeight, topSash.getBounds.x + topSash.getBounds.width, topSash.getBounds.y + SASH_WIDTH + accHeight + height)
                        accHeight += height
                      case _ => print("Error: 394")
                    }
                    bottomSash.setLocation(topSash.getBounds.x, topSash.getBounds.y + SASH_WIDTH + accHeight)
                    (childInfo(i): @unchecked) match {
                      case (_, _, _, true, changeSize) => changeSize(topSash.getBounds.x, bottomSash.getBounds.y + SASH_WIDTH, topSash.getBounds.x + topSash.getBounds.width, bottomBound + accHeight + SASH_WIDTH)
                    }
                    sashMap(topSash) = (topSash.getBounds.y - topBound) * 1.0 / (sashBottomBounds(topSash) - sashTopBounds(topSash) - numHeight - sashes.length * SASH_WIDTH)
                    nextSashMap(bottomSash) match {
                      case (Some(nextSash), _) => sashMap(nextSash) = (bottomBound + accHeight - bottomSash.getBounds.y) * 1.0 / (sashBottomBounds(topSash) - sashTopBounds(topSash) - numHeight - sashes.length * SASH_WIDTH)
                      case _ =>
                    }
                  }
                }
              }
              bottomSash addControlListener new ControlAdapter {
                override def controlMoved(event: ControlEvent) {
                  if (!isChangingSize) {
                    val topBound = prevSashMap(topSash) match {
                      case (Some(prevSash), topMargin) => prevSash.getBounds.y + 2 * SASH_WIDTH + topMargin + sashDist
                      case (None, topMargin) => sashTopBounds(bottomSash) + topMargin + SASH_WIDTH + sashDist
                    }
                    if (bottomSash.getBounds.y < topBound + childInfo(k)._2)
                      bottomSash.setLocation(bottomSash.getBounds.x, topBound + childInfo(k)._2)
                    val bottomBound = nextSashMap(bottomSash) match {
                      case (Some(nextSash), bottomMargin) => nextSash.getBounds.y - bottomMargin
                      case (None, bottomMargin) => sashBottomBounds(bottomSash) - bottomMargin
                    }
                    if (bottomSash.getBounds.y + SASH_WIDTH > bottomBound - childInfo(i)._2)
                      bottomSash.setLocation(bottomSash.getBounds.x, bottomBound - SASH_WIDTH - childInfo(i)._2)
                    (childInfo(i): @unchecked) match {
                      case (_, _, _, true, changeSize) => changeSize(bottomSash.getBounds.x, bottomSash.getBounds.y + SASH_WIDTH, bottomSash.getBounds.x + bottomSash.getBounds.width, bottomBound)
                    }
                    var accHeight = 0
                    for (j <- i - 1 to k + 1 by -1) childInfo(j) match {
                      case (_, height, _, false, changeSize) =>
                        changeSize(bottomSash.getBounds.x, bottomSash.getBounds.y - accHeight - height, bottomSash.getBounds.x + bottomSash.getBounds.width, bottomSash.getBounds.y - accHeight)
                        accHeight += height
                      case _ => print("Error: 431")
                    }
                    topSash.setLocation(bottomSash.getBounds.x, bottomSash.getBounds.y - SASH_WIDTH - accHeight)
                    (childInfo(k): @unchecked) match {
                      case (_, _, _, true, changeSize) => changeSize(bottomSash.getBounds.x, topBound - accHeight - SASH_WIDTH, bottomSash.getBounds.x + bottomSash.getBounds.width, topSash.getBounds.y)
                    }
                    sashMap(topSash) = (topSash.getBounds.y - topBound + accHeight + SASH_WIDTH) * 1.0 / (sashBottomBounds(topSash) - sashTopBounds(topSash) - numHeight - sashes.length * SASH_WIDTH)
                    nextSashMap(bottomSash) match {
                      case (Some(nextSash), _) => sashMap(nextSash) = (bottomBound - bottomSash.getBounds.y - SASH_WIDTH) * 1.0 / (sashBottomBounds(bottomSash) - sashTopBounds(bottomSash) - numHeight - sashes.length * SASH_WIDTH)
                      case _ =>
                    }
                  }
                }
              }
              if (i == childInfo.length - 1)
                (childInfo(i): @unchecked) match {
                  case (_, _, _, true, changeSize) =>
                    changeSizes ::= (changeSize, Some(topSash), Some(accHeight + SASH_WIDTH), None, Some(0), None)
                }
            } else {
              if (i == childInfo.length - 1)
                (childInfo(i): @unchecked) match {
                  case (_, _, _, true, changeSize) =>
                    changeSizes ::= (changeSize, Some(topSash), Some(0), None, Some(0), None)
                }
              topSash addControlListener new ControlAdapter {
                override def controlMoved(event: ControlEvent) {
                  if (!isChangingSize) {
                    val topBound = prevSashMap(topSash) match {
                      case (Some(prevSash), topMargin) => prevSash.getBounds.y + SASH_WIDTH + topMargin
                      case (None, topMargin) => sashTopBounds(topSash) + topMargin
                    }
                    if (topSash.getBounds.y < topBound + childInfo(i - 1)._2) // MARK 9.12 - added 'childInfo(i-1)._2'
                      topSash.setLocation(topSash.getBounds.x, topBound + childInfo(i - 1)._2) // MARK 9.12 - added 'childInfo(i-1)._1'
                    val bottomBound = nextSashMap(topSash) match {
                      case (Some(nextSash), bottomMargin) => nextSash.getBounds.y - bottomMargin
                      case (None, bottomMargin) => sashBottomBounds(topSash) - bottomMargin
                    }
                    if (topSash.getBounds.y + SASH_WIDTH > bottomBound - childInfo(i)._2)
                      topSash.setLocation(topSash.getBounds.x, bottomBound - SASH_WIDTH - childInfo(i)._2)
                    (childInfo(i - 1): @unchecked) match {
                      case (_, _, _, true, changeSize) => changeSize(topSash.getBounds.x, topBound, topSash.getBounds.x + topSash.getBounds.width, topSash.getBounds.y)
                    }
                    (childInfo(i): @unchecked) match {
                      case (_, _, _, true, changeSize) => changeSize(topSash.getBounds.x, topSash.getBounds.y + SASH_WIDTH, topSash.getBounds.x + topSash.getBounds.width, bottomBound)
                    }
                    //println(currRight)
                    sashMap(topSash) = (topSash.getBounds.y - topBound) * 1.0 / (sashBottomBounds(topSash) - sashTopBounds(topSash) - numHeight - sashes.length * SASH_WIDTH)
                    nextSashMap(topSash) match {
                      case (Some(nextSash), _) => sashMap(nextSash) = (bottomBound - topSash.getBounds.y - SASH_WIDTH) * 1.0 / (sashBottomBounds(topSash) - sashTopBounds(topSash) - numHeight - sashes.length * SASH_WIDTH)
                      case _ =>
                    }
                  }
                }
              }
            }
          } else { // 20 20 ? ...
            var accHeight = 0
            for (j <- 0 to i - 1)
              childInfo(j) match {
                case (_, height, _, false, changeSize) =>
                  changeSizes ::= (changeSize, None, Some(accHeight), None, None, Some(height))
                  accHeight += height
                case _ => print("Error: 494")
              }
            if (i == childInfo.length - 1)
              (childInfo(i): @unchecked) match {
                case (_, _, _, true, changeSize) =>
                  changeSizes ::= (changeSize, None, Some(accHeight), None, Some(0), None)
              }
          }
          j = i
          seenQM += 1
        case (_, height, _, false, _) =>
          if (j == 0 && i == childInfo.length - 1) { // 20 20 20
            var accHeight = 0
            for (j <- i to 0 by -1)
              childInfo(j) match {
                case (_, height, _, false, changeSize) =>
                  changeSizes ::= (changeSize, None, None, None, Some(accHeight), Some(height))
                  accHeight += height
                case (_, _, _, true, changeSize) =>
                  changeSizes ::= (changeSize, None, Some(0), None, Some(accHeight), None)
              }
          } else if (i == childInfo.length - 1) { // ... ? 20 20
            var accHeight = 0
            for (j <- i to j + 1 by -1)
              childInfo(j) match {
                case (_, height, _, false, changeSize) =>
                  changeSizes ::= (changeSize, None, None, None, Some(accHeight), Some(height))
                  accHeight += height
                case _ => print("Error: 522")
              }
            (childInfo(j): @unchecked) match {
              case (_, _, _, true, changeSize) =>
                if (seenQM > 1)
                  changeSizes ::= (changeSize, Some(sashes.last), Some(0), None, Some(accHeight), None)
                else
                  changeSizes ::= (changeSize, None, Some(numHeight - accHeight), None, Some(accHeight), None)
            }
          }
      }
    }
    for ((sash, (prevSash, const)) <- prevSashMap) prevSash match {
      case Some(prevSash) => nextSashMap += prevSash -> (Some(sash), const)
      case _ =>
    }
    if (!sashes.isEmpty)
      nextSashMap += sashes.last -> (None, (childInfo reverse) takeWhile { case (_, _, _, b, _) => !b } map { case (_, h, _, false, _) => h; case _ => 0 } sum)
    val totalHeight = numHeight + sashes.length * SASH_WIDTH
    val width = childInfo map { case (w, _, _, _, _) => w } max
    val isWidthQM = childInfo.count({ case (_, _, isQM, _, _) => !isQM }) == 0
    (width, totalHeight, isWidthQM, seenQM > 0, (left: Int, top: Int, right: Int, bottom: Int) => {
      sashes foreach (sash => {
        sashTopBounds(sash) = top
        sashBottomBounds(sash) = bottom
      })
      isChangingSize = true
      sashes foreach (sash => {
        val (prevSash, constMargin) = prevSashMap(sash)
        val topMargin = (prevSash map (s => s.getBounds.y + s.getBounds.height) getOrElse top) + constMargin + sashMap(sash) * (bottom - top - totalHeight)
        sash.setBounds(left, topMargin.toInt, right - left, SASH_WIDTH) //left top width height
        // println("sash", sash.getBounds)
      })
      changeSizes foreach {
        case (changeSize, Some(topSash), Some(topMargin), None, None, Some(height)) =>
          changeSize(left, topSash.getBounds.y + topSash.getBounds.height + topMargin,
            right, topSash.getBounds.y + topSash.getBounds.height + topMargin + height)
        case (changeSize, Some(topSash), Some(topMargin), Some(bottomSash), Some(bottomMargin), None) =>
          changeSize(left, topSash.getBounds.y + topSash.getBounds.height + topMargin,
            right, bottomSash.getBounds.y - bottomMargin)
        case (changeSize, None, Some(topMargin), Some(bottomSash), Some(bottomMargin), None) =>
          changeSize(left, top + topMargin, right, bottomSash.getBounds.y - bottomMargin)
        case (changeSize, None, Some(topMargin), None, None, Some(height)) =>
          changeSize(left, top + topMargin, right, top + topMargin + height)
        case (changeSize, None, None, None, Some(bottomMargin), Some(height)) =>
          changeSize(left, bottom - bottomMargin - height, right, bottom - bottomMargin)
        case (changeSize, Some(topSash), Some(topMargin), None, Some(bottomMargin), None) =>
          changeSize(left, topSash.getBounds.y + topSash.getBounds.height + topMargin, right, bottom - bottomMargin)
        case (changeSize, None, Some(topMargin), None, Some(bottomMargin), None) =>
          changeSize(left, top + topMargin, right, bottom - bottomMargin)
        case _ => print("Error: 572")
      }

      isChangingSize = false
    })
  }

  private def handleDynamicHorizontalContainer(parent: Composite, env: Environment, children: List[Widget]): TEvalNodeReturn = {
    val scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL)
    scrolledComposite setLayout new FillLayout
    scrolledComposite setExpandHorizontal true
    scrolledComposite setExpandVertical true
    val composite = new Composite(scrolledComposite, SWT.NONE)
    scrolledComposite setContent composite
    val childInfo = children.map(child => (child, evalNode(child, composite, env)))
    val width = children.map({ case AtomicWidget(_, _, Some(Literal(w: Int)), _) => w; case _ => 0 }).sum
    val height = children.map({ case AtomicWidget(_, _, _, Some(Literal(h: Int))) => h; case _ => 0 }).max
    scrolledComposite addControlListener new ControlAdapter {
      override def controlResized(event: ControlEvent) {
        composite.setSize(scrolledComposite.getSize)
        var accWidth = 0
        for ((child, (_, _, _, _, changeSize)) <- childInfo) {
          val childWidth = env.evalInt(child.getWidth.get)
          changeSize(accWidth, 0, accWidth + childWidth, child.getHeight.map(w => env.evalInt(w)).getOrElse(composite.getSize.y))
          accWidth += childWidth
        }
        scrolledComposite setMinWidth accWidth
        scrolledComposite setMinHeight children.map({ case AtomicWidget(_, _, _, Some(w)) => env.evalInt(w); case _ => 0 }).max
      }
    }
    (width, height, false, children.forall({ case AtomicWidget(_, _, _, None) => true; case _ => false }), (left, top, right, bottom) => {
      scrolledComposite.setMinWidth(right - left)
      scrolledComposite.setBounds(left, top, right - left, bottom - top)
    })
  }

  private def handleDynamicVerticalContainer(parent: Composite, env: Environment, children: List[Widget]): TEvalNodeReturn = {
    val scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL)
    scrolledComposite setLayout new FillLayout
    scrolledComposite setExpandHorizontal true
    scrolledComposite setExpandVertical true
    val composite = new Composite(scrolledComposite, SWT.NONE)
    scrolledComposite setContent composite
    val childInfo = children.map(child => (child, evalNode(child, composite, env)))
    val height = children.map({ case AtomicWidget(_, _, _, Some(Literal(h: Int))) => h; case _ => 0 }).sum
    val width = children.map({ case AtomicWidget(_, _, Some(Literal(w: Int)), _) => w; case _ => 0 }).max
    scrolledComposite addControlListener new ControlAdapter {
      override def controlResized(event: ControlEvent) {
        composite.setSize(scrolledComposite.getSize)
        var accHeight = 0
        for ((child, (_, _, _, _, changeSize)) <- childInfo) {
          val childHeight = env.evalInt(child.getHeight.get)
          changeSize(0, accHeight, child.getWidth.map(w => env.evalInt(w)).getOrElse(composite.getSize.x), accHeight + childHeight)
          accHeight += childHeight
        }
        scrolledComposite setMinHeight accHeight
        scrolledComposite setMinWidth children.map({ case AtomicWidget(_, _, Some(w), _) => env.evalInt(w); case _ => 0 }).max
      }
    }
    (width, height, children.forall({ case AtomicWidget(_, _, None, _) => true; case _ => false }), false, (left, top, right, bottom) => {
      scrolledComposite.setMinHeight(bottom - top)
      scrolledComposite.setBounds(left, top, right - left, bottom - top)
    })
  }

  def evalNode(code: ASTNode, parent: Composite, env: Environment): TEvalNodeReturn = code match {
    //***case 1/3 atomic widget***
    case AtomicWidget(kind, attributes, widthExpr, heightExpr) =>
      val changeAtt = mutableMap[String, () => Unit]()
      var hAlign = 0
      var text = ""
      var checked = false
      // TODO change image location
      var image = "E:/error.jpg"
      var minValue, maxValue = 0
      var value: Option[Int] = None
      var changeImageSize = (width: Int, height: Int) => {}
      //Delete this line var (minWidth, minHeight, isWidthQM, isHeightQM) = (0, 0, true, true)
      var widthVar = (widthExpr match { case Some(w: Literal[_]) => widthExpr; case _ => None }).map(env.evalInt)
      // NOTE: a known issue: the parser does not distinguish between (label) and (label:?x?)
      // which causes handling that does not support native label and image size (3.2.2 in the project specs)
      var heightVar = heightExpr.map(env.evalInt)
      for (att <- attributes) att.getName match {
        case "halign" => hAlign = hAlignASTToSWT(env.evalHAlign(att.getValue.get))
        // valign not included due to lack of SWT support
        case "text" => text = env.evalString(att.getValue.get)
        case "checked" => checked = env.evalBoolean(att.getValue.get)
        case "filename" => image = env.evalString(att.getValue.get)
        case "value" => value = Some(env.evalInt(att.getValue.get))
        case "maxvalue" => maxValue = env.evalInt(att.getValue.get)
        case "minvalue" => minValue = env.evalInt(att.getValue.get)
        case _ =>
      }
      class WidgetSelectionAdapter[T](attName: String, attValue: () => T, changeVarLTR: (Expr, T) => String) extends SelectionAdapter {
        override def widgetSelected(e: SelectionEvent) {
          if (attributes.exists(_.getName == attName)) {
            val name = changeVarLTR(attributes.find(_.getName == attName).get.getValue.get, attValue())
            if (name == null)
              return
            varsAffectedByCurrentUpdate = Set(name)
            env.unevaluatedVarMap(name).foreach(_())
            varsAffectedByCurrentUpdate = null
          }
        }
      }
      //handling RTL
      def changeAttRTL(attName: String, changeAtt: Expr => Unit) = attributes.foreach(att =>
        if (att.getName == attName)
          env.getVariables(att.getValue.get).foreach(name =>
          env.unevaluatedVarMap(name) += (() => changeAtt(att.getValue.get))))
      val widget = kind match {
        case "label" | "" =>
          val label = new Label(parent, SWT.WRAP | hAlign)
          label setText text
          changeAttRTL("halign", expr => label.setAlignment(hAlignASTToSWT(env.evalHAlign(expr))))
          changeAttRTL("text", expr => label.setText(env.evalString(expr)))
          /*(if (widthVal.isEmpty)
            widthVal = Some(label.computeSize(SWT.DEFAULT, SWT.DEFAULT).x)
          if (heightVal.isEmpty)
            heightVal = Some(label.computeSize(SWT.DEFAULT, SWT.DEFAULT).y)*/
          label
        case "textbox" => // dynamic change of checkbox alignment not included due to lack of SWT support
          val textbox = new Text(parent, SWT.WRAP | hAlign)
          textbox setText text
          textbox.addSelectionListener(new WidgetSelectionAdapter[String]("text", () => textbox.getText(), env.changeVarLTR))
          changeAttRTL("text", expr => textbox.setText(env.evalString(expr)))
          textbox
        case "button" =>
          val button = new Button(parent, SWT.PUSH | SWT.WRAP | hAlign)
          button setText text
          button.addSelectionListener(new WidgetSelectionAdapter[Boolean]("checked", () => button.getSelection(), env.changeVarLTR))
          changeAttRTL("halign", expr => button.setAlignment(hAlignASTToSWT(env.evalHAlign(expr))))
          changeAttRTL("text", expr => button.setText(env.evalString(expr)))
          button
        case "checkbox" =>
          val checkbox = new Button(parent, SWT.CHECK) //TODO see if it's 0/1 or true/false
          checkbox.setSelection(checked)
          checkbox.addSelectionListener(new WidgetSelectionAdapter[Boolean]("checked", () => checkbox.getSelection(), env.changeVarLTR))
          changeAttRTL("checked", expr => checkbox.setSelection(env.evalBoolean(expr)))
          checkbox
        case "radio" =>
          val box = new Group(parent, SWT.NONE)
          // dummy radio button allows a default "false" state for all the other radio buttons (a workaround to an swt limitation)
          val dummy = new Button(box, SWT.RADIO)
          dummy.setSelection(true)
          dummy.setBounds(-20000, -20000, 100, 100) // left top width height
          val radio = new Button(box, SWT.RADIO)
          radio.setSelection(checked)
          radio.addSelectionListener(new WidgetSelectionAdapter[Boolean]("checked", () => radio.getSelection(), env.changeVarLTR))
          box.addControlListener(new ControlAdapter {
            override def controlResized(event: ControlEvent) {
              radio.setSize(box.getSize)
            }
          })
          changeAttRTL("checked", expr => radio.setSelection(env.evalBoolean(expr)))
          radio
        case "image" =>
          val label = new Label(parent, SWT.NONE)
          label setImage new Image(label.getDisplay(), image)
          changeImageSize = (width, height) => { // TODO fix change image size
            if (width > 0 && height > 0) {
              label.getImage.dispose
              label.setImage(new Image(label.getDisplay, new ImageData(image).scaledTo(label.getSize.x, label.getSize.y)))
            }
          }
          changeAttRTL("filename", expr => {
            image = env.evalString(expr)
            label.getImage.dispose
            label.setImage(new Image(label.getDisplay, new ImageData(image).scaledTo(label.getSize.x, label.getSize.y)))
          })
          /*if (widthVal.isEmpty)
            widthVal = Some(new ImageData(image).width)
          if (heightVal.isEmpty)
            heightVal = Some(new ImageData(image).height)*/
          label
        case "combo" =>
          val combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY)
          combo setItems text.split(",")
          value match {
            case Some(v) => combo select v
            case None =>
          }
          combo.addSelectionListener(new WidgetSelectionAdapter[Int]("value", () => combo.getSelectionIndex(), env.changeVarLTR))
          changeAttRTL("text", expr => combo.setItems(env.evalString(expr).split(",")))
          changeAttRTL("value", expr => combo.select(env.evalInt(expr)))
          combo
        case "slider" =>
          val slider = new Slider(parent, SWT.HORIZONTAL)
          slider setMaximum maxValue
          slider setMinimum minValue
          slider setSelection value.getOrElse(0)
          slider.addSelectionListener(new WidgetSelectionAdapter[Int]("value", () => slider.getSelection(), env.changeVarLTR))
          changeAttRTL("maxvalue", expr => slider.setMaximum(env.evalInt(expr)))
          changeAttRTL("minvalue", expr => slider.setMinimum(env.evalInt(expr)))
          changeAttRTL("value", expr => slider.setSelection(env.evalInt(expr)))
          slider
        case s =>
          val scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL)
          scrolledComposite setLayout new FillLayout
          scrolledComposite setExpandHorizontal true
          scrolledComposite setExpandVertical true
          val composite = new Composite(scrolledComposite, SWT.NONE)
          scrolledComposite setContent composite
          val newEnv =
            if (params == null)
              env
            else
              new Environment(new ScopingMap(env.evaluatedVarMap), new ScopingMap(env.unevaluatedVarMap))
          newEnv.evaluatedVarMap.put("width", 0)
          newEnv.evaluatedVarMap.put("height", 0)
          newEnv.unevaluatedVarMap.put("width", Set())
          newEnv.unevaluatedVarMap.put("height", Set())
          val (width, height, isWidthQM, isHeightQM, changeSize) = evalNode(PropertyScope(widgetsMap(s), attributes), composite, newEnv)
          if (widthVar.isEmpty && !isWidthQM)
            widthVar = Some(width)
          if (heightVar.isEmpty && !isHeightQM)
            heightVar = Some(height)
          scrolledComposite setMinWidth width
          scrolledComposite setMinHeight height
          scrolledComposite setSize (width, height)
          scrolledComposite addControlListener new ControlAdapter {
            override def controlResized(event: ControlEvent) {
              newEnv.evaluatedVarMap("width") = scrolledComposite.getSize.x
              newEnv.evaluatedVarMap("height") = scrolledComposite.getSize.y
              composite.setSize(scrolledComposite.getSize)
              changeSize(0, 0, composite.getSize.x, composite.getSize.y)
            }
          }
          scrolledComposite
      }
      for (att <- attributes) att.getName match {
        case "enabled" =>
          widget setEnabled env.evalBoolean(att.getValue.get)
          changeAttRTL("enabled", expr => widget.setEnabled(env.evalBoolean(expr)))
        case "fgcolor" =>
          widget setForeground colorASTToSWT(env.evalColor(att.getValue.get), widget.getDisplay())
          changeAttRTL("fgcolor", expr => widget.setForeground(colorASTToSWT(env.evalColor(expr), widget.getDisplay())))
        case "bgcolor" =>
          widget setBackground colorASTToSWT(env.evalColor(att.getValue.get), widget.getDisplay())
          changeAttRTL("bgcolor", expr => widget.setBackground(colorASTToSWT(env.evalColor(expr), widget.getDisplay())))
        case "font" =>
          widget setFont fontASTToSWT(env.evalFont(att.getValue.get), widget.getDisplay())
          changeAttRTL("font", expr => widget.setFont(fontASTToSWT(env.evalFont(expr), widget.getDisplay())))
        // attribute "code" helps handling "bind" 
        //        case "code" =>
        //          widget.addListener(SWT.Selection, new Listener {
        //            override def handleEvent(e: Event) = {
        //              env.eval(att.getValue.get)
        //            }
        //          })
        case _ =>
      }
      val widgetForResize = widget match { case w: Button if (w.getStyle & SWT.RADIO) == SWT.RADIO => widget.getParent; case _ => widget }
      (widthVar getOrElse 0,
        heightVar getOrElse 0,
        widthExpr match {
          case Some(Variable(name, _, false)) => true //TODO value? handling (unrelatedly, also width/height expressions ltr update todo)
          case None => widthVar.isEmpty
          case _ => false
        },
        heightExpr match {
          case Some(Variable(name, _, false)) => true
          case None => heightVar.isEmpty
          case _ => false
        },
        (left: Int, top: Int, right: Int, bottom: Int) => {
          widgetForResize setBounds (left, top, math.min(right - left, widthExpr.map(env.evalInt).getOrElse(Int.MaxValue)),
            math.min(bottom - top, heightExpr.map(env.evalInt).getOrElse(Int.MaxValue)))
          changeImageSize(widgetForResize.getSize.x, widgetForResize.getSize.y)
        })
    // TODO deal with vertical

    //***case 2/3 - container***
    case Container(Container.Direction.Horizontal, children, _, _) => // TODO consider width and height
      if (children.forall({ case AtomicWidget(_, _, Some(_), _) => true; case _ => false }))
        handleDynamicHorizontalContainer(parent, env, children)
      else
        handleHorizontalContainer(parent, env, children)

    case Container(Container.Direction.Vertical, children, _, _) => // TODO consider width and height
      if (children.forall({ case AtomicWidget(_, _, _, Some(_)) => true; case _ => false }))
        handleDynamicVerticalContainer(parent, env, children)
      else
        handleVerticalContainer(parent, env, children)

    //***case 3/3 property scope
    case PropertyScope(container, attributes) => {
      //first add the variables to the varmap:
      val newEnv =
        if (params == null)
          env
        else
          new Environment(new ScopingMap(env.evaluatedVarMap), new ScopingMap(env.unevaluatedVarMap))
      attributes.map({
        case ExpressionAttribute(att, expr) => // var = value
          if (!newEnv.unevaluatedVarMap.contains(att.id))
            newEnv.unevaluatedVarMap.put(att.id, Set())
          newEnv.getVariables(expr).map(variable =>
            env.unevaluatedVarMap(variable) += (() => {
              if (!varsAffectedByCurrentUpdate(att.id)) {
                newEnv.evaluatedVarMap(att.id) = env.eval(expr)
                varsAffectedByCurrentUpdate += att.id
                newEnv.unevaluatedVarMap(att.id).foreach(_())
              }
            }))
          newEnv.evaluatedVarMap.put(att.id, env.eval(expr))

        case InitialAttribute(att, Some(expr)) => // var = ?(value)
          if (!newEnv.unevaluatedVarMap.contains(att.id))
            newEnv.unevaluatedVarMap.put(att.id, Set(INITIAL_ATT_FLAG))
          else
            newEnv.unevaluatedVarMap(att.id) += INITIAL_ATT_FLAG
          newEnv.evaluatedVarMap.put(att.id, env.eval(expr))

        case InitialAttribute(att, None) => // var = ?

      })
      if (params == null)
        params = newEnv.evaluatedVarMap
      //then, handle the rest of the container:
      evalNode(container, parent, newEnv)
    }

    case IterationMacro(widget, direction, props) => evalNode(IterationMacro.expand(widget, direction, props), parent, env)

  }

}