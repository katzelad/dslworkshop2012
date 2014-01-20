package org.tau.dslworkshop

import org.tau.workshop2011.parser.AST.AtomicWidget
import org.tau.workshop2011.parser.AST.Program
import org.tau.workshop2011.parser.AST.Widget
import org.tau.workshop2011.parser.LayoutParser

object Demo extends Application {
  val kode = MainDemo();
  val prog = LayoutParser.iParse(kode);
  def evalWidget(param: Widget) = param match {
    case AtomicWidget(kind, attributes, width, height) => ""
  }
  def eval(result: Program) = {
    println(result)
  }
  LayoutParser.parseAll(LayoutParser.Program, kode) match {
    case LayoutParser.Success(result, nextInput) => eval(result)
    case LayoutParser.NoSuccess(msg, nextInput) =>
      println("Could not parse the input.");
      println(msg)
  }

  //println(prog);
}

object MainDemo {
  def apply() =
    """Emailtextbox <- (label:(SenderW)x(EEHeight))[text = EmailSender(i), fgcolor = EEfgcol(i), bgcolor = EEbgcol(i)] | (button)[text = EmailSubject(i), fgcolor = EEfgcol(i), bgcolor = EEbgcol(i), checked = ?(false), active = Activate(i,checked) + 5]

EmailList  <- (((Emailtextbox)
                   *---*
                 [i=0...15]):?x(ListHeight))

EmailView <-

  (label:(descW)x(descH))[text = AdressLabel]    | (textbox:?x?) [text = readSender]
                                            ---
  (label:(descW)x(descH))[text = "Subject:"] | (textbox:?x?) [text = readSubject]
                                            ---
                                (textbox)[text = readContent]


main_window <- (

        (button:100x30)[text = "New", checked = NewC] | (button:100x?)[text = "Reply", checked = ReplyC] | (button:100x?)[text = "Refresh", checked = RefreshC] | ()
                                      ---
  (EmailList) | (EmailView)[readContent = EmailContent(Active), readSender = EmailSender(Active), readSubject = EmailSubject(Active), AdressLabel = "From: "]

)[SenderW = ?(150), ListHeight = height - 30, EEHeight = ?(30), descW = ?(150), descH = ?(30), NewC = ?(false), ReplyC = ?(false), RefreshC = ?(false), Active = ?(0)]


reply_window <- (

    (EmailView:?x(height-30))[descW = ?(150), descH = ?(30), AdressLabel = "To: "]
                    ---
  (button:100x?)[text = "Submit", checked = SubmitC] | ()

)[readContent = ?, readSender = ?, readSubject = ?, SubmitC = ?(false)]"""
}

object Radios {
  def apply() = """ main_window <-
    (((radio:30x30)[checked = v = i] | (label)[text = Animals[i]])
                           *---*
     [i=0...6,Animals={"Alpaca","Bunny","Cat","Dog","Elephant","Fox","Goose"}])[v=?(3)]
    """
}

object VSplitWithScroll {
  def apply() = """ main_window <- (
    (button:?x(height/2))
           -----
    (button:?x(height/2))
           ---
    (button:?x(height/2))
  )
    """
}

object Subprograms { // changed the names of the image files
  def apply() = """L <- (
      (label)[text="Do you like?"]
      ----------------------------
      (radio)[checked=v] | (label)[text="Yes"] | (radio)[checked=!v] | (label)[text="No"]
    )
    I <- (image:32x32)[filename={v=>"examples\\like.jpg", otherwise "examples\\dislike.jpg"}]
    main_window <- ((L) | (I))[v=?(0)]
    """
}


object Notepad {
  def apply() = """  Notepad <-
      (
      	((button:32x32)|(button:32x?)|
      	(button:32x?)|(:10x?)|
      	(button:32x?)|(button:32x?)|())
      	---
      	(textbox)[text="Enter your text here", font=("arial", 16, bold)]
      	---
      	(label)[text="Status: ready"]|(button:40x20)
      ) 
    
     main_window <-(Notepad: 300x200)
    """
}

object Test1 {
  def apply() = """   
     main_window <-(button:50x50)
    """
}
