package org.tau.dslworkshop.main

import org.tau.workshop2011.expressions.Color

object Main {

  //note - internal terminology:
  //  parentheses () -- container -- currently does not supports scroll
  // L -- non-atomic widget -- currently supports scroll
  // label etc -  atomic widget

  /*def evalCode(w: Widget, window: Shell, unevaluatedVarMap: mutableMap[String, Set[() => Unit]], evaluatedVarMap: mutableMap[String, Any]) = {
    window setLayout new FillLayout
    val scrolledComposite = new ScrolledComposite(window, SWT.H_SCROLL)
    scrolledComposite setLayout new FillLayout
    scrolledComposite setExpandHorizontal true
    //scrolledComposite setExpandVertical true
    val composite = new Composite(scrolledComposite, SWT.NONE)
    scrolledComposite setContent composite
    val (width, height, _, _, changeSize) = evalNode(w, composite, unevaluatedVarMap, evaluatedVarMap)
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
  }*/

  def main(args: Array[String]) = {

    val code =

      //      """main_window<-( label :100x? )[ text ="typicaltypicaltypical", enabled = true, bgcolor = 0x0000FF, fgcolor = 0xFF0000, font = ("times new roman", 14, bold), halign = left] |
      //      ( textbox :?x70 )[ text ="eladeladeladeladeladeladeladelad", enabled = false, bgcolor = 0x0000FF, fgcolor = 0x00FF00, font = ("times new roman", 12, italic), halign = center] |
      //      ( button :?x100 )[ text ="shirshirshirshirshirshir", enabled = false, bgcolor = 0x00FFFF, fgcolor = 0x008F8F, font = ("times new roman", 16, italic), halign = center] | (
      //      ( checkbox :?x70 )[enabled = true, bgcolor = 0x0000FF, fgcolor = 0x00FF00, checked = true] |
      //      ( radio :?x70 )[enabled = true, bgcolor = 0x00FFFF, fgcolor = 0xAAFF00, checked = false] | (
      //      ( radio :?x70 )[enabled = true, bgcolor = 0x00AAFF, fgcolor = 0xAAFF00, checked = true] |
      //      ( textbox :?x100 )[bgcolor = 0x00AAFF, fgcolor = 0xAAFF00, text = "kjfdhjk"]) |
      //      ( slider :?x50 )[enabled = true, bgcolor = 0x00CADF, fgcolor = 0x7ABF00, maxvalue=300, minvalue =1 , value=50]) |
      //      ( combo :100x100 )[text = "a,b,c",enabled = true, bgcolor = 0x00CADF, fgcolor = 0x7ABF00]
      //      l<-( label :? x(a+b) )
      //      x<-(y)[x=?(3)]
      //      m<-( label :20x20 )[ text =" typical "]"""

      //      """main_window<-(l:?x?)[x=3] |
      //      ( textbox :?x70 )[ text ="eladeladeladeladeladeladeladelad", enabled = false, bgcolor = 0x0000FF, fgcolor = 0x00FF00, font = ("times new roman", 12, italic), halign = center] |
      //      ( button :?x100 )[ text ="shirshirshirshirshirshir", enabled = false, bgcolor = 0x00FFFF, fgcolor = 0x008F8F, font = ("times new roman", 16, italic), halign = center] | (
      //      ( checkbox :?x70 )[enabled = true, bgcolor = 0x0000FF, fgcolor = 0x00FF00, checked = true] |
      //      ( radio :?x70 )[enabled = true, bgcolor = 0x00FFFF, fgcolor = 0xAAFF00, checked = false] | (
      //      ( radio :?x70 )[enabled = true, bgcolor = 0x00AAFF, fgcolor = 0xAAFF00, checked = true] |
      //      ( textbox :?x100 )[bgcolor = 0x00AAFF, fgcolor = 0xAAFF00, text = "kjfdhjk"]) |
      //      ( slider :?x50 )[enabled = true, bgcolor = 0x00CADF, fgcolor = 0x7ABF00, maxvalue=300, minvalue =1 , value=50]) |
      //      ( combo :100x100 )[text = "a,b,c",enabled = true, bgcolor = 0x00CADF, fgcolor = 0x7ABF00]
      //      l<-( label :100x? )[ text ="typicaltypicaltypical", enabled = true, bgcolor = 0x0000FF, fgcolor = 0xFF0000, font = ("times new roman", 14, bold), halign = left]
      //      x<-(y)[x=?(3)]
      //      m<-( label :20x20 )[ text =" typical "]""";

      //      // including complicated expression
      //    	"""main_window <- (( label :?x? )[ text ="label-typicaltypicaltypical", enabled = true, bgcolor = 0x00FF00, fgcolor = 0xFF0000, font = ("times new roman", 14, bold), halign = left] |
      //      ( textbox :?x70 )[ text ="textbox-eladeladeladeladeladeladeladelad", enabled = false, bgcolor = 0xAACC00, fgcolor = 0x00FF00, font = ("times new roman", 12, italic), halign = center] |
      //      ( button :?x100 )[ text ="button-shirshirshirshirshirshir", enabled = false, bgcolor = 0x0000FF, fgcolor = 0x008F8F, font = ("times new roman", 16, italic), halign = center] |
      //      (
      //	    ( checkbox :?x70 )[enabled = true, bgcolor = 0x9900FF, fgcolor = 0x00FF00, checked = {false => true, otherwise false}] |
      //	    ( radio :?x70 )[enabled = true, bgcolor = 0x00FFFF, fgcolor = 0xAAFF00, myVar=?(false), checked = true] |
      //	    ( radio :?x70 )[enabled = true, bgcolor = 0x00AAFF, fgcolor = 0xAAFF00, checked = false]
      //      )|
      //      ( textbox :?x100 )[bgcolor = 0xDD00AA, fgcolor = 0xDDDD00, text = "kjfdhjk"] |
      //      (
      //      	( slider :?x50 )[enabled = true, bgcolor = 0xDDDD00, fgcolor = 0xDDDD00, maxvalue=300, minvalue =1 , value={true => 20, otherwise 250}] |
      //      	( combo :?x100 )[text = "a,b,c",enabled = true, bgcolor = 0x00CADF, fgcolor = 0x7ABF00] 
      //      ))[willThisWork=?(3)]
      //      l<-( label :? x(a+b))
      //      x<-(y)[x=?(3)]
      //      m<-(label :20x20 )[ text =" typical "]""";

      //test initial test
      //      """main_window <- (( label :?x? )[ text ="label-typicaltypicaltypical", enabled = true, bgcolor = 0x00FF00, fgcolor = 0xFF0000, font = ("times new roman", 14, bold), halign = left] |
      //      ( textbox :?x70 )[ text ="textbox-eladeladeladeladeladeladeladelad", enabled = false, bgcolor = 0xAACC00, fgcolor = 0x00FF00, font = ("times new roman", 12, italic), halign = center] |
      //      ( button :?x100 )[ text ="button-shirshirshirshirshirshir", enabled = false, bgcolor = 0x0000FF, fgcolor = 0x008F8F, font = ("times new roman", 16, italic), halign = center] |
      //      (
      //	    ( checkbox :?x70 )[enabled = true, bgcolor = 0x9900FF, fgcolor = 0x00FF00, checked = false] |
      //	    ( radio :?x70 )[enabled = true, bgcolor = 0x00FFFF, fgcolor = 0xAAFF00, checked = true] |
      //	    ( radio :?x70 )[enabled = true, bgcolor = 0x00AAFF, fgcolor = 0xAAFF00, checked = false]
      //      )|
      //      ( textbox :?x100 )[bgcolor = 0xDD00AA, fgcolor = 0xDDDD00, text = "kjfdhjk"] |
      //      (
      //      	( slider :?x50 )[enabled = true, bgcolor = 0xDDDD00, fgcolor = 0xDDDD00, maxvalue=300, minvalue =1 , value=250] |
      //      	( combo :?x100 )[text = "a,b,c",enabled = true, bgcolor = 0x00CADF, fgcolor = 0x7ABF00] 
      //      ))
      //      l<-( label :? x(a+b))
      //      x<-(y)[x=?(3)]
      //      m<-(label :20x20 )[ text =" typical "]"""

      //test fixed gui radio size
      //       """main_window <- (( label :?x? )[ text ="label-typicaltypicaltypical", enabled = true, bgcolor = 0x00FF00, fgcolor = 0xFF0000, font = ("times new roman", 14, bold), halign = left] |
      //      ( textbox :?x70 )[ text ="textbox-eladeladeladeladeladeladeladelad", enabled = false, bgcolor = 0xAACC00, fgcolor = 0x00FF00, font = ("times new roman", 12, italic), halign = center] |
      //      ( button :?x100 )[ text ="button-shirshirshirshirshirshir", enabled = false, bgcolor = 0x0000FF, fgcolor = 0x008F8F, font = ("times new roman", 16, italic), halign = center] |
      //      (
      //	    ( checkbox :?x70 )[enabled = true, bgcolor = 0x9900FF, fgcolor = 0x00FF00, checked = false] |
      //	    ( radio :30x70 )[enabled = true, bgcolor = 0x00FFFF, fgcolor = 0xAAFF00, checked = true] |
      //	    ( radio :?x70 )[enabled = true, bgcolor = 0x00AAFF, fgcolor = 0xAAFF00, checked = false]
      //      )|
      //      ( textbox :?x100 )[bgcolor = 0xDD00AA, fgcolor = 0xDDDD00, text = "kjfdhjk"] |
      //      (
      //      	( slider :?x50 )[enabled = true, bgcolor = 0xDDDD00, fgcolor = 0xDDDD00, maxvalue=300, minvalue =1 , value=250] |
      //      	( combo :50x100 )[text = "a,b,c",enabled = true, bgcolor = 0x00CADF, fgcolor = 0x7ABF00] 
      //      ))
      //      l<-( label :? x(a+b))
      //      x<-(y)[x=?(3)]
      //      m<-(label :20x20 )[ text =" typical "]"""

//            """L <- (
//            (label:?x?)[text="Do you like?"] |
//            (radio:?x?)[checked=v] | (label:30x?)[text="Yes"] | (radio:?x?)[checked=!v] | (label:?x?)[text="No"]
//          )
//          I <- (image:32x32)[filename={v=>"like.png", otherwise "dislike.png"}]
//          main_window <- 
//            (label:?x?)[text="Do you like?"] |
//            (radio:?x?)[checked=false] | (label:30x70)[text="Yes"] | (radio:?x?)[checked=true] | (label:?x?)[text="No"]
//         
//          """

      //testing demo subprograms - simplified, no vertical
//            """L <- (
//            (label)[text="Do you like?"]
//            |
//            (label)[text={v=>"true", otherwise "false"}]
//            |
//            (radio)[checked=v] | (label)[text="Yes"] | (radio)[checked=!v] | (label)[text="No"]
//          )
//          I <- (image)[filename={v=>"D:\\like.jpg", otherwise "D:\\dislike.jpg"}]
//          main_window <- ((L) | (I:80x80))[v=?(1)]
//          """

      //testing IterationMacro
//      """ main_window <-
//    (((radio:30x30)[checked = v = i, bgcolor = Colors[i]] | (label)[text = Animals[i], fgcolor = Colors[i]])
//                           *|*
//     [i=0...3,Animals={"Alpaca","Bunny","Cat","Dog","Elephant","Fox","Goose"},
//      Colors = {0x00FF00, 0x808080, 0xFF0000, 0xFFFF00, 0x008000, 0x008080, 0x000080}])
//    
//      """
      
      
    //testing width/height expressions SUPER BASIC CASE    //fails due to with 0
//          """ main_window <- (
//    		(button:(200)x?) | (button) 
//    		)
//    		"""
      
      //testing width/height expressions
//    """ main_window <- (
//    (button:200x?)|
//    (button:?x500)|
//    (button:?x?)|
//    (button:200x?)
//  )
//    """
      
      
// test "maindemo" simplified

//          """Emailtextbox <- (label:(SenderW)x(EEHeight))[text = EmailSender(i), fgcolor = EEfgcol(i), bgcolor = EEbgcol(i)] | (button)[text = EmailSubject(i), fgcolor = EEfgcol(i), bgcolor = EEbgcol(i), checked = ?(false), active = Activate(i,checked) + 5]
//
//EmailList  <- (((Emailtextbox)
//                   *---*
//                 [i=0...15]):?x(ListHeight))
//
//EmailView <-
//
//  (label:(descW)x(descH))[text = AdressLabel]    | (textbox:?x?) [text = readSender]
//                                            |
//  (label:(descW)x(descH))[text = "Subject:"] | (textbox:?x?) [text = readSubject]
//                                            |
//                                (textbox)[text = readContent]
//
//
//main_window <- (
//
//        (button:100x30)[text = "New", checked = NewC] | (button:100x?)[text = "Reply", checked = ReplyC] | (button:100x?)[text = "Refresh", checked = RefreshC] | ()
//                                      |
//  (EmailList) | (EmailView)[readContent = EmailContent(Active), readSender = EmailSender(Active), readSubject = EmailSubject(Active), AdressLabel = "From: "]
//
//)[SenderW = ?(150), ListHeight = height - 30, EEHeight = ?(30), descW = ?(150), descH = ?(30), NewC = ?(false), ReplyC = ?(false), RefreshC = ?(false), Active = ?(0)]
//
//
//reply_window <- (
//
//    (EmailView:?x(height-30))[descW = ?(150), descH = ?(30), AdressLabel = "To: "]
//                    |
//  (button:100x?)[text = "Submit", checked = SubmitC] | ()
//
//)[readContent = ?, readSender = ?, readSubject = ?, SubmitC = ?(false)]"""
      
      """ main_window <- (
    (button:?x(height/2))
           -----
    (button:?x(height/2))
           ---
    (button:?x(height/2))
  )
    """

      
      
      
    val instance = new DSLProgram(code)("main_window")
    println(args.mkString("{", " ", "}"))
    
    instance.bind("EmailSender", (_: Seq[Any]) => "Neta Katz")
    instance.bind("EEfgcol", (_: Seq[Any]) => new Color("0xFF0000"))
    instance.bind("EEbgcol", (_: Seq[Any]) => new Color("0x00FF00"))
    instance.bind("EmailSubject", (_: Seq[Any]) => "Piggish slippers")
    instance.bind("EmailContent", (_: Seq[Any]) => "I WANT MY PIGGISH SLIPPERS")
    val output = instance(/*args*/ ("v=3" :: Nil).toArray)

    println(output)

  }
}