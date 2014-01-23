package org.tau.dslworkshop.piano

import java.io.File
import scala.io.Source
import org.tau.dslworkshop.compiler.DSLProgram
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequencer
import javax.sound.midi.ShortMessage
import javax.sound.midi.Sequence

object Piano {

  //note - internal terminology:
  //  parentheses () -- container -- currently does not supports scroll
  // L -- non-atomic widget -- currently supports scroll
  // label etc -  atomic widget

  def main(args: Array[String]) = {

    val code = Source.fromFile("src\\org\\tau\\dslworkshop\\piano\\Piano.dsl").mkString

    //todo get rid of titlebgcolor/font/fgcolor etc if unused
    //TODO catch exceptions

    val program = new DSLProgram(code)
    val instance = program(
      name = "main_window",
      icon = "Graphics\\MaestroIcon.png",
      isMaximized = true,
      title = "The Maestro",
      defaultWidth = 700)
    println(args.mkString("{", " ", "}"))

    var langchoice = if (args.length == 0) 0 else if (args(0) == "DE") 1 else 0
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

    //synth initialization
    val synth = MidiSystem.getSynthesizer()
    synth.open
    val mainChannel = synth.getChannels()(0)

    val synthrecv = MidiSystem.getReceiver

    val rhythmsqr = MidiSystem.getSequencer(false)
    rhythmsqr.open
    rhythmsqr.setLoopCount(Sequencer.LOOP_CONTINUOUSLY)
    rhythmsqr.setTempoFactor(math.pow(2, 0.5).toFloat)
    rhythmsqr.getTransmitter.setReceiver(synthrecv)

    //    val recordsqr = MidiSystem.getSequencer(false)
    //    recordsqr.open()
    //    val recordrecv = recordsqr.getReceiver()
    //    //synth.getTransmitter.setReceiver(recordrecv)
    //    rhythmsqr.getTransmitter.setReceiver(recordrecv) //
    //    val recordseq = new Sequence(Sequence.PPQ, 10);
    //    val track = recordseq.createTrack()
    //    recordsqr.setSequence(recordseq)
    //    recordsqr.recordEnable(track, -1)
    //    recordsqr.startRecording()

    //    seqer.startRecording

    def play(note: Int, autoStop: Boolean = false) {
      mainChannel.synchronized {
        if (!pedal)
          mainChannel.allNotesOff
        mainChannel.noteOn(note + octave * 12, vol)
        recent = recent + noteToString(note) + ' '
        instance.set("recent", recent)
        if (autoStop) new Thread {
          Thread.sleep(200)
          mainChannel.noteOff(note, vol)
        }.start
      }
    }

    def playRhythm(rhythm: Int, prev: Int) {
      if (prev != 0)
        rhythmsqr.stop
      if (rhythm != 0) {
        val rhythmName = rhythm match {
          case 1 => "Jazz"
          case 2 => "Rock"
          case 3 => "Country"
          case 4 => "Funk"
        }
        rhythmsqr.setSequence(MidiSystem.getSequence(new File("Audio\\" + rhythmName + ".mid")))
        rhythmsqr.start
      }
    }

    def changeInstrument(instrument: Int) = mainChannel.synchronized {
      mainChannel.programChange(instrument match {
        case 0 => 0
        case 1 => 40
        case 2 => 65
        case 3 => 24
        case 4 => 56
        case 5 => 6
        case 6 => 47
        case 7 => 123
        case _ => 0
      })
      mainChannel.allNotesOff
    }

    instance.when_changed("vol", (_, newer) => {
      vol = newer.asInstanceOf[Int]
      for (i <- 0 until 16)
        synthrecv.send(new ShortMessage(ShortMessage.CONTROL_CHANGE, i, 7, vol), -1)
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
    instance.when_changed("about", (_, _) => {
      program(
        name = "AboutContent",
        title = "About",
        icon = "Graphics\\MaestroIcon.png",
        isDialog = true)(Array("is_eng=" + (1 - langchoice), "is_deu=" + langchoice))
    })
    instance.when_changed("filename", (_, newer) => filename = newer.asInstanceOf[String])
    instance.when_changed("pedal", (_, newer) => pedal = newer.asInstanceOf[Boolean])
    instance.when_changed("rhythmchoice", (old, newer) => playRhythm(newer.asInstanceOf[Int], old.asInstanceOf[Int]))
    instance.when_changed("tempo", (_, newer) => rhythmsqr.setTempoFactor(math.pow(2, (newer.asInstanceOf[Int] - 1) / 4.0).toFloat))
    instance.when_changed("instrument", (_, newer) => changeInstrument(newer.asInstanceOf[Int]))
    instance.when_changed("langchoice", (_, newer) => langchoice = newer.asInstanceOf[Int])

    instance.bind("play", (x: Int, y: Int) => {
      play((x, y) match {
        case _ if x > 20 && x < 44 && y < 153 => doodiez - 12
        case _ if x > 62 && x < 86 && y < 153 => rediez - 12
        case _ if x > 126 && x < 150 && y < 153 => fadiez - 12
        case _ if x > 168 && x < 192 && y < 153 => soldiez - 12
        case _ if x > 208 && x < 232 && y < 153 => ladiez - 12
        case _ if x > 20 + 252 && x < 44 + 252 && y < 153 => doodiez
        case _ if x > 62 + 252 && x < 86 + 252 && y < 153 => rediez
        case _ if x > 126 + 252 && x < 150 + 252 && y < 153 => fadiez
        case _ if x > 168 + 252 && x < 192 + 252 && y < 153 => soldiez
        case _ if x > 208 + 252 && x < 232 + 252 && y < 153 => ladiez
        case _ if x < 36 => doo - 12
        case _ if x < 72 => re - 12
        case _ if x < 108 => mi - 12
        case _ if x < 144 => fa - 12
        case _ if x < 180 => sol - 12
        case _ if x < 216 => la - 12
        case _ if x < 252 => si - 12
        case _ if x < 288 => doo
        case _ if x < 324 => re
        case _ if x < 360 => mi
        case _ if x < 396 => fa
        case _ if x < 432 => sol
        case _ if x < 468 => la
        case _ if x < 504 => si
      }, true)
    })

    instance.onKeyPress(key => {
      val note = keyToNote(key)
      if (note != -1)
        play(note)
    })
    instance.onKeyRelease(key => mainChannel.synchronized {
      if (!pedal && keyToNote(key) != -1)
        mainChannel.allNotesOff
    })

    val output = instance(args = Array(s"langchoice=$langchoice", "recent=\"\"", "octave=0"))

    //    recordsqr.stopRecording()
    //    MidiSystem.write(recordseq, 1, new File("Audio\\recording.mid"))

    synth.close
    rhythmsqr.close
    synthrecv.close
    println(output)

    //TODO delete eventually
    def furelise {
      play(mi + 12)
      wait(1)
      play(re + 13)
      wait(1)
      play(mi + 12)
      wait(1)
      play(re + 13)
      wait(1)
      play(mi + 12)
      wait(1)
      play(si)
      wait(1)
      play(re + 12)
      wait(1)
      play(doo + 12)
      wait(1)
      play(la)
      play(la - 24)
      wait(1)
      play(mi - 12)
      wait(1)
      play(la - 12)
      wait(1)
      play(doo)
      wait(1)
      play(mi)
      wait(1)
      play(la)
      wait(1)
      play(si)
      play(mi - 24)
      wait(1)
      play(mi - 12)
      wait(1)
      play(sol - 11)
      wait(1)
      play(mi)
      wait(1)
      play(sol + 1)
      wait(1)
      play(si)
      wait(1)
      play(doo + 12)
      play(la - 24)
      wait(1)
      play(mi - 12)
      wait(1)
      play(la - 12)
      wait(1)
      play(mi)
      wait(1)

      play(mi + 12)
      wait(1)
      play(re + 13)
      wait(1)
      play(mi + 12)
      wait(1)
      play(re + 13)
      wait(1)
      play(mi + 12)
      wait(1)
      play(si)
      wait(1)
      play(re + 12)
      wait(1)
      play(doo + 12)
      wait(1)
      play(la)
      play(la - 24)
      wait(1)
      play(mi - 12)
      wait(1)
      play(la - 12)
      wait(1)
      play(doo)
      wait(1)
      play(mi)
      wait(1)
      play(la)
      wait(1)
      play(si)
      play(mi - 24)
      wait(1)
      play(mi - 12)
      wait(1)
      play(sol - 11)
      wait(1)
      play(mi)
      wait(1)
      play(doo + 12)
      wait(1)
      play(si)
      wait(1)
      play(la)
      play(la - 24)
      wait(1)
      play(mi - 12)
      wait(1)
      play(la - 12)
      wait(1)
      play(si)
      wait(1)
    }
  }
}
