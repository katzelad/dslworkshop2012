import org.eclipse.swt._
import scala.List
import org.eclipse.swt.layout._
import org.eclipse.swt.widgets._
import org.eclipse.swt.events._
import org.eclipse.swt.graphics.Image
import org.tau.workshop2011.parser._
import org.tau.workshop2011.parser.AST._
import org.tau.workshop2011.expressions._
import org.eclipse.swt.custom.ScrolledComposite
import scala.collection.mutable.{ Map => mutableMap, Buffer => mutableBuffer }
import org.eclipse.swt.graphics.{ Color => swtColor }
import org.eclipse.swt.graphics.{ Font => swtFont }

object Main {

  val SASH_WIDTH = 5
//TES
  var widgetsMap: Map[String, AST.Widget] = null

  def evalNode(code: ASTNode, parent: Composite): (Int, Int, Boolean, Boolean, (Int, Int, Int, Int) => Unit) = code match {
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
        case "halign" => hAlign = (EvalExpr[HAlign](att getValue).get: @unchecked) match {
          case HAlign.left => SWT.LEFT
          case HAlign.center => SWT.CENTER
          case HAlign.right => SWT.RIGHT
        }
        case "text" => text = EvalExpr(att getValue).get
        case "checked" => checked = EvalExpr(att getValue).get
        case "image" => image = EvalExpr(att getValue).get
        case "value" => value = EvalExpr(att getValue)
        case "maxvalue" => maxValue = EvalExpr(att getValue).get
        case "minvalue" => minValue = EvalExpr(att getValue).get
        case _ =>
      }
      val widget = kind match {
        case "label" | "" =>
          val label = new Label(parent, SWT.WRAP | hAlign)
          label setText text
          label
        case "textbox" =>
          val textbox = new Text(parent, SWT.WRAP | hAlign)
          textbox setText text
          textbox
        case "button" =>
          val button = new Button(parent, SWT.PUSH | SWT.WRAP | hAlign)
          button setText text
          button
        case "checkbox" =>
          val checkbox = new Button(parent, SWT.CHECK) //TODO see if it's 0/1 or true/false
          checkbox.setSelection(checked)
          checkbox
        case "radio" =>
          val radio = new Button(parent, SWT.RADIO)
          radio.setSelection(checked)
          radio
        case "image" =>
          val label = new Label(parent, SWT.NONE)
          label setImage new Image(label.getDisplay(), image)
          changeImageSize = (width, height) => { // TODO fix change image size
            if (width > 0 && height > 0) {
              val prevImage = label.getImage
              label setImage new Image(label.getDisplay(), prevImage.getImageData().scaledTo(width, height))
              prevImage dispose
            }
          }
          label
        case "combo" =>
          val combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY)
          combo setItems text.split(",")
          value match {
            case Some(v) => combo select v
            case None =>
          }
          combo
        case "slider" =>
          val slider = new Slider(parent, SWT.HORIZONTAL)
          slider setMaximum maxValue
          slider setMinimum minValue
          slider setSelection value.getOrElse(0)
          slider
        case s =>
          val scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL)
          scrolledComposite setLayout new FillLayout
          scrolledComposite setExpandHorizontal true
          //scrolledComposite setExpandVertical true
          val composite = new Composite(scrolledComposite, SWT.NONE)
          scrolledComposite setContent composite
          val (width, height, _, _, changeSize) = evalNode(widgetsMap(s), composite)
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
        case "enabled" => widget setEnabled EvalExpr(att getValue).get
        case "fgcolor" =>
          val color = EvalExpr[Color](att getValue).get
          widget setForeground new swtColor(widget.getDisplay(), color.red, color.green, color.blue)
        case "bgcolor" =>
          val color = EvalExpr[Color](att getValue).get
          widget setBackground new swtColor(widget.getDisplay(), color.red, color.green, color.blue)
        case "font" =>
          val font = EvalExpr[Font](att getValue).get
          val style = (font.style: @unchecked) match {
            case TextStyle.bold => SWT.BOLD
            case TextStyle.italic => SWT.ITALIC
            case TextStyle.regular => SWT.NORMAL
          }
          widget setFont new swtFont(widget.getDisplay(), font.face, font.size, style)
        case _ =>
      }
      val widthVal = EvalExpr(width)
      val heightVal = EvalExpr(height)
      (widthVal getOrElse 0, heightVal getOrElse 0, widthVal.isEmpty, heightVal.isEmpty, (left: Int, top: Int, right: Int, bottom: Int) => {
        widget setBounds (left, top, math.min(right - left, widthVal.getOrElse(Int.MaxValue)),
          math.min(bottom - top, heightVal.getOrElse(Int.MaxValue)))
        changeImageSize(widget.getSize.x, widget.getSize.y)
        // println(widget, widget.getBounds)
      })
    // TODO deal with vertical
    case Container(Container.Direction.Horizontal, children, _, _) =>
      var seenQM = 0
      var sashes = mutableBuffer[Sash]()
      val childInfo = children map (evalNode(_, parent))
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

  def evalCode(code: Program, window: Shell) = {
    widgetsMap = code.defs.toMap
    widgetsMap get "main_window" match {
      case Some(w) =>
        window setLayout new FillLayout
        val scrolledComposite = new ScrolledComposite(window, SWT.H_SCROLL)
        scrolledComposite setLayout new FillLayout
        scrolledComposite setExpandHorizontal true
        //scrolledComposite setExpandVertical true
        val composite = new Composite(scrolledComposite, SWT.NONE)
        scrolledComposite setContent composite
        val (width, height, _, _, changeSize) = evalNode(w, composite)
        scrolledComposite setMinWidth width
        window addControlListener new ControlAdapter {
          override def controlResized(event: ControlEvent) {
            composite.setSize(scrolledComposite.getSize)
            changeSize(0, 0, composite.getSize.x, composite.getSize.y)
          }
        }
        window.setSize(1000, 500)
        composite.setSize(1000, 500)
        changeSize(0, 0, composite.getSize.x, composite.getSize.y)
      /*val (width, height, changeSize) = evalNode(w, window)
        window addControlListener new ControlAdapter {
          override def controlResized(event: ControlEvent) {
            changeSize(0, 0, window.getSize.x, window.getSize.y)
          }
        }
        window.setSize(1000, 500)
        changeSize(0, 0, 1000, 500)*/

      case None => println("main_window not found")
    }
  }

  def main(args: Array[String]) = {
    val display = new Display
    val shell = new Shell(display)
    val code = /*"""main_window<-( label :100x? )[ text ="typicaltypicaltypical", enabled = true, bgcolor = 0x0000FF, fgcolor = 0xFF0000, font = ("times new roman", 14, bold), halign = left] |
      ( textbox :?x70 )[ text ="eladeladeladeladeladeladeladelad", enabled = false, bgcolor = 0x0000FF, fgcolor = 0x00FF00, font = ("times new roman", 12, italic), halign = center] |
      ( button :?x100 )[ text ="shirshirshirshirshirshir", enabled = false, bgcolor = 0x00FFFF, fgcolor = 0x008F8F, font = ("times new roman", 16, italic), halign = center] | (
      ( checkbox :?x70 )[enabled = true, bgcolor = 0x0000FF, fgcolor = 0x00FF00, checked = true] |
      ( radio :?x70 )[enabled = true, bgcolor = 0x00FFFF, fgcolor = 0xAAFF00, checked = false] | (
      ( radio :?x70 )[enabled = true, bgcolor = 0x00AAFF, fgcolor = 0xAAFF00, checked = true] |
      ( textbox :?x100 )[bgcolor = 0x00AAFF, fgcolor = 0xAAFF00, text = "kjfdhjk"]) |
      ( slider :?x50 )[enabled = true, bgcolor = 0x00CADF, fgcolor = 0x7ABF00, maxvalue=300, minvalue =1 , value=50]) |
      ( combo :100x100 )[text = "a,b,c",enabled = true, bgcolor = 0x00CADF, fgcolor = 0x7ABF00]
      l<-( label :? x(a+b) )
      x<-(y)[x=?(3)]
      m<-( label :20x20 )[ text =" typical "]"""*/
      
      /*"""main_window<-(l:?x?)[x=3] |
      ( textbox :?x70 )[ text ="eladeladeladeladeladeladeladelad", enabled = false, bgcolor = 0x0000FF, fgcolor = 0x00FF00, font = ("times new roman", 12, italic), halign = center] |
      ( button :?x100 )[ text ="shirshirshirshirshirshir", enabled = false, bgcolor = 0x00FFFF, fgcolor = 0x008F8F, font = ("times new roman", 16, italic), halign = center] | (
      ( checkbox :?x70 )[enabled = true, bgcolor = 0x0000FF, fgcolor = 0x00FF00, checked = true] |
      ( radio :?x70 )[enabled = true, bgcolor = 0x00FFFF, fgcolor = 0xAAFF00, checked = false] | (
      ( radio :?x70 )[enabled = true, bgcolor = 0x00AAFF, fgcolor = 0xAAFF00, checked = true] |
      ( textbox :?x100 )[bgcolor = 0x00AAFF, fgcolor = 0xAAFF00, text = "kjfdhjk"]) |
      ( slider :?x50 )[enabled = true, bgcolor = 0x00CADF, fgcolor = 0x7ABF00, maxvalue=300, minvalue =1 , value=50]) |
      ( combo :100x100 )[text = "a,b,c",enabled = true, bgcolor = 0x00CADF, fgcolor = 0x7ABF00]
      l<-( label :100x? )[ text ="typicaltypicaltypical", enabled = true, bgcolor = 0x0000FF, fgcolor = 0xFF0000, font = ("times new roman", 14, bold), halign = left]
      x<-(y)[x=?(3)]
      m<-( label :20x20 )[ text =" typical "]"""; */
      
      /* including complicated expression
       *
      val shell = new Shell(display)
       val originalDSLcode = """main_window <- (( label :?x? )[ text ="label-typicaltypicaltypical", enabled = true, bgcolor = 0x00FF00, fgcolor = 0xFF0000, font = ("times new roman", 14, bold), halign = left] |
      ( textbox :?x70 )[ text ="textbox-eladeladeladeladeladeladeladelad", enabled = false, bgcolor = 0xAACC00, fgcolor = 0x00FF00, font = ("times new roman", 12, italic), halign = center] |
      ( button :?x100 )[ text ="button-shirshirshirshirshirshir", enabled = false, bgcolor = 0x0000FF, fgcolor = 0x008F8F, font = ("times new roman", 16, italic), halign = center] |
      (
	    ( checkbox :?x70 )[enabled = true, bgcolor = 0x9900FF, fgcolor = 0x00FF00, checked = {false => true, otherwise false}] |
	    ( radio :?x70 )[enabled = true, bgcolor = 0x00FFFF, fgcolor = 0xAAFF00, myVar=?(false), checked = true] |
	    ( radio :?x70 )[enabled = true, bgcolor = 0x00AAFF, fgcolor = 0xAAFF00, checked = false]
      )|
      ( textbox :?x100 )[bgcolor = 0xDD00AA, fgcolor = 0xDDDD00, text = "kjfdhjk"] |
      (
      	( slider :?x50 )[enabled = true, bgcolor = 0xDDDD00, fgcolor = 0xDDDD00, maxvalue=300, minvalue =1 , value={true => 20, otherwise 250}] |
      	( combo :?x100 )[text = "a,b,c",enabled = true, bgcolor = 0x00CADF, fgcolor = 0x7ABF00] 
      ))[willThisWork=?(3)]
      l<-( label :? x(a+b))
      x<-(y)[x=?(3)]
      m<-(label :20x20 )[ text =" typical "]""";
    */
    
      """main_window <- (( label :?x? )[ text ="label-typicaltypicaltypical", enabled = true, bgcolor = 0x00FF00, fgcolor = 0xFF0000, font = ("times new roman", 14, bold), halign = left] |
      ( textbox :?x70 )[ text ="textbox-eladeladeladeladeladeladeladelad", enabled = false, bgcolor = 0xAACC00, fgcolor = 0x00FF00, font = ("times new roman", 12, italic), halign = center] |
      ( button :?x100 )[ text ="button-shirshirshirshirshirshir", enabled = false, bgcolor = 0x0000FF, fgcolor = 0x008F8F, font = ("times new roman", 16, italic), halign = center] |
      (
	    ( checkbox :?x70 )[enabled = true, bgcolor = 0x9900FF, fgcolor = 0x00FF00, checked = false] |
	    ( radio :?x70 )[enabled = true, bgcolor = 0x00FFFF, fgcolor = 0xAAFF00, checked = true] |
	    ( radio :?x70 )[enabled = true, bgcolor = 0x00AAFF, fgcolor = 0xAAFF00, checked = false]
      )|
      ( textbox :?x100 )[bgcolor = 0xDD00AA, fgcolor = 0xDDDD00, text = "kjfdhjk"] |
      (
      	( slider :?x50 )[enabled = true, bgcolor = 0xDDDD00, fgcolor = 0xDDDD00, maxvalue=300, minvalue =1 , value=250] |
      	( combo :?x100 )[text = "a,b,c",enabled = true, bgcolor = 0x00CADF, fgcolor = 0x7ABF00] 
      ))
      l<-( label :? x(a+b))
      x<-(y)[x=?(3)]
      m<-(label :20x20 )[ text =" typical "]""";
      
    val prog = LayoutParser iParse code;
    LayoutParser parseAll (LayoutParser.Program, code) match {
      case LayoutParser.Success(result, nextInput) => evalCode(result, shell) //print(result) 
      case LayoutParser.NoSuccess(msg, nextInput) =>
        println("Could not parse the input.");
        println(msg)
    }
    shell setText "Test"

    shell.open
    while (!shell.isDisposed) {
      if (!display.readAndDispatch) {
        display.sleep
      }
    }
    display.dispose
  }
}