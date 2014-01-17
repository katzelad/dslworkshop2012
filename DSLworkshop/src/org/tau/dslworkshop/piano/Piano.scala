package org.tau.dslworkshop.piano
import javax.sound.midi._
import scala.io.Source
import java.io.File
import org.tau.dslworkshop.compiler.DSLProgram

object Piano {

  //note - internal terminology:
  //  parentheses () -- container -- currently does not supports scroll
  // L -- non-atomic widget -- currently supports scroll
  // label etc -  atomic widget

  def main(args: Array[String]) = {

    val code = Source.fromFile("src\\org\\tau\\dslworkshop\\piano\\Piano.dsl").mkString

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

    val instance = new DSLProgram(code)(
      name = "main_window",
      icon = "Graphics\\Icon.png",
      isMaximized = true,
      title = "The Maestro")
    println(args.mkString("{", " ", "}"))

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
      mainChannel.programChange(instrument match {
        case 0 => 0
        case 1 => 40
        case 2 => 65
        case 3 => 24
        case 4 => 56
        case 5 => 6
        case _ => 0
      })
      //      mainChannel.programChange(synth.getDefaultSoundbank.getInstruments()(instIndex).getPatch.getProgram)
    }

    instance.when_changed("vol", (_, newer) => {
      vol = newer.asInstanceOf[Int]
      if (seqer.getSequence != null) {
        val tracks = seqer.getSequence.getTracks
        for (i <- 0 until tracks.length)
          tracks(i).add(new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, i, 7, vol), 0))
      }
    })
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

    val output = instance(args = Array("langchoice=0", "recent=\"\"", "octave=0"))

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