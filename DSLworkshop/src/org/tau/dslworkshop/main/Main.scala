package org.tau.dslworkshop.main

import org.tau.workshop2011.expressions.Color
import javax.sound.midi._
import scala.io.Source
import java.io.File

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
      Source.fromFile("src\\Piano.dsl").mkString

    //todo conditioned enable to filenames (text/audio) textboxes (dummy buttons etc)
    //todo make initheight/width work
    //todo piano notes play by coordinates
    //TODO mute disables volume feature (using another var)
    //todo about button
    //todo record button
    //todo get rid of titlebgcolor/font/fgcolor etc if unused
    //TODO make sure window resizes nicely/make sure dummy widgets with fixed size for spacing works
    //TODO catch exceptions
    //TODO debug parameters list

    val instance = new DSLProgram(code)("main_window")
    println(args.mkString("{", " ", "}"))

    //    instance.bind("EmailSender", (_: Seq[Any]) => "Neta Katz")
    //    instance.bind("EEfgcol", (_: Seq[Any]) => new Color("0xFF0000"))
    //    instance.bind("EEbgcol", (_: Seq[Any]) => new Color("0x00FF00"))
    //    instance.bind("EmailSubject", (_: Seq[Any]) => "Piggish slippers")
    //    instance.bind("EmailContent", (_: Seq[Any]) => "I WANT MY PIGGISH SLIPPERS")

    var vol = 50
    var octave = 0
    var recent = ""
    var pedal = false
    var filename = ""
    val (doo, doodiez, re, rediez, mi, fa, fadiez, sol, soldiez, la, ladiez, si) = (60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71)

    def noteToString(note: Int) = note % 12 + 60 match {
      case `doo` => "C"
      case `doodiez` => "C#"
      case `re` => "D"
      case `rediez` => "D#"
      case `mi` => "E"
      case `fa` => "F"
      case `fadiez` => "F#"
      case `sol` => "G"
      case `soldiez` => "G#"
      case `la` => "A"
      case `ladiez` => "A#"
      case `si` => "B"
    }

    def keyToNote(key: Char) = key match {
      case 'q' => doo
      case 'w' => re
      case 'e' => mi
      case 'r' => fa
      case 't' => sol
      case 'y' => la
      case 'u' => si
      case 'i' => doo + 12
      case 'z' => doo - 12
      case 'x' => re - 12
      case 'c' => mi - 12
      case 'v' => fa - 12
      case 'b' => sol - 12
      case 'n' => la - 12
      case 'm' => si - 12
      case ',' => doo
      case '2' => doodiez
      case '3' => rediez
      case '5' => fadiez
      case '6' => soldiez
      case '7' => ladiez
      case 's' => doodiez - 12
      case 'd' => rediez - 12
      case 'g' => fadiez - 12
      case 'h' => soldiez - 12
      case 'j' => ladiez - 12
      case _ => -1
    }

    val synth = MidiSystem.getSynthesizer()
    synth.open
    val mainChannel = synth.getChannels()(0)

    def play(note: Int) {
      if (!pedal)
        mainChannel.allNotesOff
      mainChannel.noteOn(note + octave * 12, vol)
      recent = recent + noteToString(note) + ' '
      instance.set("recent", recent)
    }

    val seqer = MidiSystem.getSequencer
    seqer.open
    seqer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY)
    seqer.setTempoFactor(math.pow(2, 0.5).toFloat)

    def playRhythm(rhythm: Int, prev: Int) {
      if (prev != 0)
        seqer.stop
      if (rhythm != 0) {
        val rhythmName = rhythm match {
          case 1 => "Jazz"
          case 2 => "Rock"
          case 3 => "Country"
          case 4 => "Funk"
        }
        seqer.setSequence(MidiSystem.getSequence(new File("Audio\\" + rhythmName + ".mid")))
        seqer.start
      }
    }

    def changeInstrument(instrument: Int) {
      val instIndex = instrument match { //todo update match
        case 0 => 0
        case 1 => 40
        case 2 => 0
        case 3 => 0
        case 4 => 56
        case _ => 0
      } //TODO change to match
      mainChannel.programChange(synth.getDefaultSoundbank.getInstruments()(instIndex).getPatch.getProgram)
    }

    instance.when_changed("vol", (_, newer) => vol = newer.asInstanceOf[Int])
    instance.when_changed("up", (_, _) => {
      octave = octave + 1
      instance.set("octave", octave)
    })
    instance.when_changed("down", (_, _) => {
      octave = octave - 1
      instance.set("octave", octave)
    })
    instance.when_changed("clear", (_, _) => {
      recent = ""
      instance.set("recent", "")
    })
    instance.when_changed("filename", (_, newer) => filename = newer.asInstanceOf[String])
    instance.when_changed("pedal", (_, newer) => pedal = newer.asInstanceOf[Boolean])
    instance.when_changed("rhythmchoice", (old, newer) => playRhythm(newer.asInstanceOf[Int], old.asInstanceOf[Int]))
    instance.when_changed("tempo", (old, newer) => seqer.setTempoFactor(math.pow(2, (newer.asInstanceOf[Int] - 1) / 4.0).toFloat))
    instance.when_changed("instrument", (old, newer) => changeInstrument(newer.asInstanceOf[Int]))

    instance.bind("play", (x: Int, y: Int) =>
      play((x, y) match {
        case _ if x > 20 && x < 44 && y < 153 => doodiez
        case _ if x > 62 && x < 86 && y < 153 => rediez
        case _ if x > 126 && x < 150 && y < 153 => fadiez
        case _ if x > 168 && x < 192 && y < 153 => soldiez
        case _ if x > 208 && x < 232 && y < 153 => ladiez
        case _ if x > 20 + 252 && x < 44 + 252 && y < 153 => doodiez + 12
        case _ if x > 62 + 252 && x < 86 + 252 && y < 153 => rediez + 12
        case _ if x > 126 + 252 && x < 150 + 252 && y < 153 => fadiez + 12
        case _ if x > 168 + 252 && x < 192 + 252 && y < 153 => soldiez + 12
        case _ if x > 208 + 252 && x < 232 + 252 && y < 153 => ladiez + 12
        case _ if x < 36 => doo
        case _ if x < 72 => re
        case _ if x < 108 => mi
        case _ if x < 144 => fa
        case _ if x < 180 => sol
        case _ if x < 216 => la
        case _ if x < 252 => si
        case _ if x < 288 => doo + 12
        case _ if x < 324 => re + 12
        case _ if x < 360 => mi + 12
        case _ if x < 396 => fa + 12
        case _ if x < 432 => sol + 12
        case _ if x < 468 => la + 12
        case _ if x < 504 => si + 12
      }))

    instance.onKeyPress(key => {
      val note = keyToNote(key)
      if (note != -1)
        play(note)
    })
    instance.onKeyRelease(key => {
      if (!pedal && keyToNote(key) != -1)
        mainChannel.allNotesOff
    })

    val output = instance( /*args*/ ("up=0" :: "down=0" :: "octave=0" :: "recent=\"\"" :: "clear=0" :: "pedal=0" :: "filename=\"MySong\"" :: "rhythmchoice=0" :: "tempo=3" :: "renamingtextfile=0" :: "renamingaudiofile=0" :: Nil).toArray)

    synth.close
    seqer.close
    println(output)

    //    def wait(time: Double) = Thread.sleep((250 * time).toInt)
    //    
    //    synth.loadInstrument(synth.getDefaultSoundbank().getInstruments()(16))
    //    val instruments = synth.getAvailableInstruments()
    //    print(synth.isSoundbankSupported(synth.getDefaultSoundbank()))
    //    //      for (i <- s.getDefaultSoundbank().getInstruments())
    //    //        println(i)
    //    //      pianoChannel.programChange(s.getDefaultSoundbank.getInstruments()(16).getPatch.getProgram)
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