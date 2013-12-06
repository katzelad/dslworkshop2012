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

class LayoutScope(widgetsMap: Map[String, Widget]) {

  var varsAffectedByCurrentUpdate: Set[String] = null

  def isReservedAtrribute(att: String) = att match {
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

  def handleHorizontalContainer(code: ASTNode, parent: Composite, env: Environment, children: List[Widget]): TEvalNodeReturn =

    {
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
              val leftSash = new Sash(parent, SWT.VERTICAL | SWT.SMOOTH)
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
                else 0)
              sashMap(leftSash) = 1.0 / qms
              sashes += leftSash
              if (i > j + 1) { // ... ? 20 20 20 ? ...
                val rightSash = new Sash(parent, SWT.VERTICAL | SWT.SMOOTH)
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
                      if (leftSash.getBounds.x < leftBound)
                        leftSash.setLocation(leftBound, leftSash.getBounds.y)
                      val rightBound = nextSashMap(rightSash) match {
                        case (Some(nextSash), rightMargin) => nextSash.getBounds.x - rightMargin - sashDist - SASH_WIDTH
                        case (None, rightMargin) => sashRightBounds(rightSash) - rightMargin - sashDist - SASH_WIDTH
                      }
                      if (leftSash.getBounds.x + SASH_WIDTH > rightBound)
                        leftSash.setLocation(rightBound - SASH_WIDTH, leftSash.getBounds.y)
                      (childInfo(k): @unchecked) match {
                        case (_, _, true, _, changeSize) => changeSize(leftBound, leftSash.getBounds.y, leftSash.getBounds.x, leftSash.getBounds.y + leftSash.getBounds.height)
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
                      if (rightSash.getBounds.x < leftBound)
                        rightSash.setLocation(leftBound, rightSash.getBounds.y)
                      val rightBound = nextSashMap(rightSash) match {
                        case (Some(nextSash), rightMargin) => nextSash.getBounds.x - rightMargin
                        case (None, rightMargin) => sashRightBounds(rightSash) - rightMargin
                      }
                      if (rightSash.getBounds.x + SASH_WIDTH > rightBound)
                        rightSash.setLocation(rightBound - SASH_WIDTH, rightSash.getBounds.y)
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
                      if (leftSash.getBounds.x < leftBound)
                        leftSash.setLocation(leftBound, leftSash.getBounds.y)
                      val rightBound = nextSashMap(leftSash) match {
                        case (Some(nextSash), rightMargin) => nextSash.getBounds.x - rightMargin
                        case (None, rightMargin) => sashRightBounds(leftSash) - rightMargin
                      }
                      if (leftSash.getBounds.x + SASH_WIDTH > rightBound)
                        leftSash.setLocation(rightBound - SASH_WIDTH, leftSash.getBounds.y)
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

  def evalNode(code: ASTNode, parent: Composite, env: Environment): TEvalNodeReturn = code match {
    //***case 1/3 atomic widget***
    case AtomicWidget(kind, attributes, width, height) =>
      var hAlign = 0
      var text = ""
      var checked = false
      // TODO change image location
      var image = "E:/error.jpg"
      var minValue, maxValue = 0
      var value: Option[Int] = None
      var changeImageSize = (width: Int, height: Int) => {}
      var (minWidth, minHeight, isWidthQM, isHeightQM) = (0, 0, true, true)
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
          box
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
          val scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL)
          scrolledComposite setLayout new FillLayout
          scrolledComposite setExpandHorizontal true
          //scrolledComposite setExpandVertical true
          val composite = new Composite(scrolledComposite, SWT.NONE)
          scrolledComposite setContent composite
          val (width, height, _, _, changeSize) = evalNode(PropertyScope(widgetsMap(s), attributes), composite, env)
          minWidth = width
          minHeight = height
          scrolledComposite setMinWidth width
          scrolledComposite addControlListener new ControlAdapter {
            override def controlResized(event: ControlEvent) {
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
        case "code" =>
          widget.addListener(SWT.Selection, new Listener {
            override def handleEvent(e: Event) = {
              env.eval(att.getValue.get)
            }
          })
        case _ =>
      }
      val widthVal = width.map(env.evalInt)
      val heightVal = height.map(env.evalInt)
      (widthVal getOrElse 0, heightVal getOrElse 0, widthVal.isEmpty, heightVal.isEmpty, (left: Int, top: Int, right: Int, bottom: Int) => {
        widget setBounds (left, top, math.min(right - left, widthVal.getOrElse(Int.MaxValue)),
          math.min(bottom - top, heightVal.getOrElse(Int.MaxValue)))
        changeImageSize(widget.getSize.x, widget.getSize.y)
      })
    // TODO deal with vertical

    //***case 2/3 - container***
    case Container(Container.Direction.Horizontal, children, _, _) => // TODO consider width and height
      handleHorizontalContainer(code, parent, env, children)

    //***case 3/3 property scope
    case PropertyScope(container, attributes) => {
      //first add the variables to the varmap:
      val newEnv = new Environment(new ScopingMap(env.evaluatedVarMap), new ScopingMap(env.unevaluatedVarMap))
      attributes.map({
        case ExpressionAttribute(att, expr) => // var = value
          newEnv.unevaluatedVarMap(att.id) = Set()
          newEnv.getVariables(expr).map(variable =>
            env.unevaluatedVarMap(variable) += (() => {
              if (!varsAffectedByCurrentUpdate(att.id)) {
                newEnv.evaluatedVarMap(att.id) = env.eval(expr)
                varsAffectedByCurrentUpdate += att.id
                newEnv.unevaluatedVarMap(att.id).foreach(_())
              }
            }))
          newEnv.evaluatedVarMap(att.id) = env.eval(expr)

        case InitialAttribute(att, Some(expr)) => // var = ?(value)
          newEnv.unevaluatedVarMap(att.id) = Set(INITIAL_ATT_FLAG)
          newEnv.evaluatedVarMap(att.id) = env.eval(expr)

        case InitialAttribute(att, None) => // var = ?
          newEnv.unevaluatedVarMap(att.id) += INITIAL_ATT_FLAG

      })
      //then, handle the rest of the container:
      evalNode(container, parent, newEnv)
    }

    case IterationMacro(widget, direction, props) => evalNode(IterationMacro.expand(widget, direction, props), parent, env)

  }

}