package org.tau.dslworkshop.main

import org.tau.workshop2011.expressions.Color
import javax.sound.midi._

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

      //      testing demo subprograms - simplified, no vertical
      //            """L <- (
      //            (label)[text="Do you like?"]
      //            ----
      //            (label)[text={v=>"true", otherwise "false"}]
      //            ----
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

      //test radios original
      //      """ main_window <-
      //    (((radio:50x20)[checked = v = i, halign = center] | (label)[text = Animals[i], halign = center])
      //                           *---*
      //     [i=0...6,Animals={"Alpaca","Bunny","Cat","Dog","Elephant","Fox","Goose"}])[v=?(3)]
      //    """

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
      //      """Emailtextbox <- (label:(SenderW)x(EEHeight))[text = EmailSender(i), fgcolor = EEfgcol(i), bgcolor = EEbgcol(i)] | (button)[text = EmailSubject(i), fgcolor = EEfgcol(i), bgcolor = EEbgcol(i), checked = ?(false), active = Activate(i,checked) + 5]
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
      //                                      -----
      //  (EmailList) | (EmailView)[readContent = EmailContent(Active), readSender = EmailSender(Active), readSubject = EmailSubject(Active), AdressLabel = "From: "]
      //
      //)[SenderW = ?(150), ListHeight = height - 30, EEHeight = ?(30), descW = ?(150), descH = ?(30), NewC = ?(false), ReplyC = ?(false), RefreshC = ?(false), Active = ?(0)]
      //
      //
      //reply_window <- (
      //
      //    (EmailView:?x(height-30))[descW = ?(150), descH = ?(30), AdressLabel = "To: "]
      //                    -----
      //  (button:100x?)[text = "Submit", checked = SubmitC] | ()
      //
      //)[readContent = ?, readSender = ?, readSubject = ?, SubmitC = ?(false)]"""

      //      """ main_window <- (
      //    (button:?x(height/2))
      //           -----
      //    (button:?x(height/2))
      //           ---
      //    (button:?x(height/2))
      //  )
      //    """

      //PIANO
      """
      main_window <-
      (inner_main_window)[langchoice=?(0), initWidth=?(800), initHeight=?(450)]
      
      inner_main_window <-
      (
    	(ProgramTitle:(initWidth)x33)
    	---
    	(Upmargin)
    	---
    	((Leftmargin)|(Controls:200x(initHeight))|(Leftmargin)|(RightHandArea))
      )[is_eng=langchoice=0, is_deu=langchoice=1, instrument = ?(0),
    	titlebgcolor = 0xFFFFFF, titlefgcolor = 0xFF2B39, titlefont = ("arial", 12, bold),
    	regbgcolor = 0xFFFFFF, regfgcolor = 0xFF6A26, regfont = ("arial", 10, bold)]
      
      Leftmargin<-
      (:20x?)[bgcolor = regbgcolor]
      
      Upmargin<-
      (:?x15)[bgcolor = regbgcolor]
      
      Spacer<-
      ()[bgcolor=regbgcolor]
      
      ProgramTitle <- (label)[text="TAU PIANO", bgcolor = 0xFFA528, fgcolor = 0xffffff, font = ("arial", 16, bold)]

      RightHandArea<-
      (
    	(RecentlyplayedLangAbout:?x160)
    	---
    	(PianoRythmNowplayngRecord:?x320)
    	---
    	(BOTTOM:?x10)
    	---
    	(Spacer)
      )
     
      
      RecentlyplayedLangAbout<-
      (
    	(Recentlyplayed)|((Lang:250x75)
    									---
    								(About))
      )
      
      Recentlyplayed<-
      (
    		(
    			(label:100x?)[text={is_eng=>"Recently Played:", is_deu=>"Zuletzt gespielt:", otherwise "Recently Played:"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|
    			(:50x?)[bgcolor=regbgcolor]|
    			(button:50x30)[text={is_eng=>"Clear", is_deu=>"Klar", otherwise "Clear"}]|
    			(:20x30)[bgcolor=regbgcolor]|
    			(button:60x30)[text="Save to Text File"]|
    			(Spacer)
    		)
    		---
    		(Upmargin)
    		---
    		(textbox)[text ="Placeholder for recently played notes", enabled = true, bgcolor = 0xFFAA00, fgcolor = regfgcolor, font = regfont, halign = center]
    		---
    		(Spacer)
      )
      
      Lang<-
      (
    	(Leftmargin)
    	|
    	(
    		((label:80x20)[text={is_eng=>"Language:", is_deu=>"Sprache:", otherwise "Language:"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|(Spacer))
    		---
    		(combo:80x20 )[value=langchoice, text ="En,De",enabled = true, bgcolor = regbgcolor, fgcolor = 0x000000]
    		---
    		(Spacer)
    	)
    	|
    	(Leftmargin)
    	|
    	((image:60x40)[filename={is_eng=>"Graphics\\UKFlag.png", is_deu=>"Graphics\\GermanyFlag.png", otherwise "Graphics\\UKFlag.png"},bgcolor = regbgcolor]---(Spacer))
    	|
    	(Spacer)
      )
      
      About<-
      (
    	(Upmargin)
    	---
    	((Leftmargin)|(button:60x30)[text={is_eng=>"About", is_deu=>"Informationen", otherwise "About"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|(Spacer))
    	---
    	(Spacer)
      )
     
      
      PianoRythmNowplayngRecord<-
      (
      ((Piano:550x230)|(Nowplaying:180x?))
      ---
      ((Rythm)|(Record))
      )
       
      Piano <-
      (
    	(Upmargin)
    	---
    	(image)[filename="Graphics\\pianoKeys.png", action=play]
      )
      
      Nowplaying<-
      (
    	(Leftmargin)
    	|
    	(
    		(Upmargin)
    		---
    		((label:150x20)[text={is_eng=>"Now Playing:", is_deu=>"Jetzt Spielen:", otherwise "Now Playing:"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|(Spacer))
    		---
    		((image:30x30)[filename={instrument=0=>"Graphics\\piano.png", instrument=1=>"Graphics\\violin.png",
    						instrument=2=>"Graphics\\drums.png",instrument=3=>"Graphics\\guitar.png",instrument=4=>"Graphics\\trumpet.png",otherwise ""},
    						bgcolor = regbgcolor]
    		 |(Spacer)
    		)
    		---
    		(Spacer)
    	)
      )
      
      Rythm <-
      (
    		((label:80x40)[text={is_eng=>"Choose Rythm:", is_deu=>"Rhythmus Selektieren:", otherwise "Choose Rythm:"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|(Spacer))
    		---
    		(combo:80x20 )[text = "rythm1,rythm2",enabled = true, bgcolor = regbgcolor, fgcolor = 0x000000]
    		---
    		(Spacer)
    	)
      
      Record <-
      (
    	(Upmargin)
    	---
    	((Leftmargin)|(button:60x30)[text={is_eng=>"Record", is_deu=>"Rekord", otherwise "Record"}, bgcolor=0x0000FF, fgcolor=regfgcolor, font=regfont]|(Spacer))
    	---
    	(Spacer)
      )
      
      BOTTOM<-(label)[text="bottom placeholder"]
      
      Controls <-
      (
    		(label:?x20)[text={is_eng=>"Controls", is_deu=>"Kontrol", otherwise "Controls"}, bgcolor = regbgcolor, fgcolor = regfgcolor, font = regfont]
    		---
    		(VOLUME:?x?)
    		---
    		(INSTRUMENT)
    		---
	    	(PEDAL)
			---
			(OCTAVE)
	      	---
	    	(Spacer)
      )
      
      VOLUME<-
      (
			(label:?x20)[text={is_eng=>"Volume:", is_deu=>"Volumen", otherwise "Volume"}, bgcolor = regbgcolor, fgcolor = regfgcolor, font = regfont]
			---
			((slider:?x20) [maxvalue=120, minvalue =0 , value=vol]|(:20x?)[bgcolor = regbgcolor])
			---
	    	(checkbox:20x20)[checked=(vol=0), bgcolor = regbgcolor, fgcolor = regfgcolor, font = regfont] |
    		(label:50x?)[text={is_eng=>"Mute", is_deu=>"Dampfen", otherwise "Mute"}, bgcolor = regbgcolor, fgcolor = regfgcolor, font = regfont]|
    		(Spacer)
      ) [vol=?(60), mute=1]
		
      
      INSTRUMENT<-
      (
    		(label:?x20)[text={is_eng=>"Instrument:", is_deu=>"Instrument", otherwise "Instrument"}, bgcolor = titlebgcolor, fgcolor = titlefgcolor, font = titlefont]
    		---
    		(
    			(( (radio:20x20) [checked= instrument = i, bgcolor = regbgcolor] | (label)[text = Instruments[i], bgcolor = regbgcolor, fgcolor=regfgcolor,font = regfont] )
		     	*---*
		     	[i=0...4,Instruments={piano, violin, drums, guitar, trumpet}])
    			|
    			(image:100x100)[filename={instrument=0=>"Graphics\\piano.png", instrument=1=>"Graphics\\violin.png",
    						instrument=2=>"Graphics\\drums.png",instrument=3=>"Graphics\\guitar.png",instrument=4=>"Graphics\\trumpet.png",otherwise ""},
    						bgcolor = regbgcolor]
    		)
      )[piano={is_eng=>"Piano", is_deu=>"Klavier", otherwise "Piano"},
    	guitar={is_eng=>"Guitar", is_deu=>"Gitarre", otherwise "Guitar"},
    	violin={is_eng=>"Violin", is_deu=>"Violine", otherwise "Violin"},
    	trumpet={is_eng=>"Trumpet", is_deu=>"Trompete", otherwise "Trumpet"},
    	drums={is_eng=>"Drums", is_deu=>"Schlagzeug", otherwise "Drums"}]
		
      
      PEDAL<-
      (
    		(checkbox:20x20)[checked = pedal, bgcolor=regbgcolor] |
    		(label:30x?)[text={is_eng=>"Pedal", is_deu=>"Pedal", otherwise "Pedal"}, bgcolor=regbgcolor]|
    		(Spacer)
      ) [pedal = ?(false)]
				
      
      OCTAVE<-
      (
			(label)[text={is_eng=>"Octave", is_deu=>"Octave", otherwise "Octave"}, bgcolor=regbgcolor] |
	    	(
	    		(button:50x20)[text={is_eng=>"Up", is_deu=>"Herauf", otherwise "Up"}, checked = up ]
	    		---
	    		(button:?x20)[text={is_eng=>"Down", is_deu=>"Hinab", otherwise "Down"}, checked = down]
	    	)
      )
      
        """

    //todo make initheight/width work
    //todo piano notes play by coordinates
      //TODO mute disables volume feature (using another var)
    //TODO octave up/down disabled after a few clicks feature
    //todo about button
    //todo record button
    //todo get rid of titlebgcolor/font/fgcolor etc if unused
    //TODO all the rest
    //TODO make sure window resizes nicely/make sure dummy widgets with fixed size for spacing works
    //TODO perhaps add languages

    val instance = new DSLProgram(code)("main_window")
    println(args.mkString("{", " ", "}"))

    //    instance.bind("EmailSender", (_: Seq[Any]) => "Neta Katz")
    //    instance.bind("EEfgcol", (_: Seq[Any]) => new Color("0xFF0000"))
    //    instance.bind("EEbgcol", (_: Seq[Any]) => new Color("0x00FF00"))
    //    instance.bind("EmailSubject", (_: Seq[Any]) => "Piggish slippers")
    //    instance.bind("EmailContent", (_: Seq[Any]) => "I WANT MY PIGGISH SLIPPERS")

    var vol = 50
    val (doo, re, mi, fa, sol, la, si) = (60, 62, 64, 65, 67, 69, 71)
    val synth = MidiSystem.getSynthesizer()
    synth.open
    val pianoChannel = synth.getChannels()(0)
    def play(note: Int, pedal: Boolean = false) {
      if (!pedal)
        pianoChannel.allNotesOff()
      pianoChannel.noteOn(note, vol)
    }

    instance.when_changed("up", () => println("up")) // TODO write actual function
    instance.when_changed("down", () => println("down")) // TODO write actual function
    instance.bind("play", (x: Int, y: Int) => play(if (x < 250) doo else re))

    val output = instance( /*args*/ ("up=0" :: "down=0" :: Nil).toArray)

    synth.close
    println(output)

    //    def wait(time: Double) = Thread.sleep((250 * time).toInt)
    //    
    //    synth.loadInstrument(synth.getDefaultSoundbank().getInstruments()(16))
    //    val instruments = synth.getAvailableInstruments()
    //    print(synth.isSoundbankSupported(synth.getDefaultSoundbank()))
    //    //      for (i <- s.getDefaultSoundbank().getInstruments())
    //    //        println(i)
    //    //      pianoChannel.programChange(s.getDefaultSoundbank().getInstruments()(16).getPatch().getProgram())
    //    def play(note: Int, drop: Boolean = false) = { if (drop) pianoChannel.allNotesOff(); pianoChannel.noteOn(note, vol) }
    //    //    pianoChannel.
    //
    //    def furelise {
    //      play(mi + 12)
    //      wait(1)
    //      play(re + 13)
    //      wait(1)
    //      play(mi + 12)
    //      wait(1)
    //      play(re + 13)
    //      wait(1)
    //      play(mi + 12)
    //      wait(1)
    //      play(si)
    //      wait(1)
    //      play(re + 12)
    //      wait(1)
    //      play(doo + 12)
    //      wait(1)
    //      play(la)
    //      play(la - 24)
    //      wait(1)
    //      play(mi - 12)
    //      wait(1)
    //      play(la - 12)
    //      wait(1)
    //      play(doo)
    //      wait(1)
    //      play(mi)
    //      wait(1)
    //      play(la)
    //      wait(1)
    //      play(si)
    //      play(mi - 24)
    //      wait(1)
    //      play(mi - 12)
    //      wait(1)
    //      play(sol - 11)
    //      wait(1)
    //      play(mi)
    //      wait(1)
    //      play(sol + 1)
    //      wait(1)
    //      play(si)
    //      wait(1)
    //      play(doo + 12)
    //      play(la - 24)
    //      wait(1)
    //      play(mi - 12)
    //      wait(1)
    //      play(la - 12)
    //      wait(1)
    //      play(mi)
    //      wait(1)
    //
    //      play(mi + 12)
    //      wait(1)
    //      play(re + 13)
    //      wait(1)
    //      play(mi + 12)
    //      wait(1)
    //      play(re + 13)
    //      wait(1)
    //      play(mi + 12)
    //      wait(1)
    //      play(si)
    //      wait(1)
    //      play(re + 12)
    //      wait(1)
    //      play(doo + 12)
    //      wait(1)
    //      play(la)
    //      play(la - 24)
    //      wait(1)
    //      play(mi - 12)
    //      wait(1)
    //      play(la - 12)
    //      wait(1)
    //      play(doo)
    //      wait(1)
    //      play(mi)
    //      wait(1)
    //      play(la)
    //      wait(1)
    //      play(si)
    //      play(mi - 24)
    //      wait(1)
    //      play(mi - 12)
    //      wait(1)
    //      play(sol - 11)
    //      wait(1)
    //      play(mi)
    //      wait(1)
    //      play(doo + 12)
    //      wait(1)
    //      play(si)
    //      wait(1)
    //      play(la)
    //      play(la - 24)
    //      wait(1)
    //      play(mi - 12)
    //      wait(1)
    //      play(la - 12)
    //      wait(1)
    //      play(si)
    //      wait(1)
    //    }
    //
    //    //    furelise
    //
    //    synth.close
  }
}