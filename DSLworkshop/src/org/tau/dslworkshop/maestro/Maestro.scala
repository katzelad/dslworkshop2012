package org.tau.dslworkshop.maestro

import java.io.File

import scala.io.Source

import org.tau.dslworkshop.compiler.DSLProgram

import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequencer
import javax.sound.midi.ShortMessage

/*
 * The application demonstrating the DSL's capabilities.
 */
object Maestro {

  def main(args: Array[String]) = {

    val code = Source.fromFile("src\\org\\tau\\dslworkshop\\maestro\\Maestro.dsl").mkString // Read the file which contains the DSL code

    print("The program is being parsed. This might take a few seconds")

    @volatile var done = false

    new Thread {
      override def run {
        while (!done) {
          print(".")
          Thread.sleep(1000)
        }
      }
    }.start

    val program = try { new DSLProgram(code) } finally { done = true } // Parse the program

    done = true

    val mainInstance = program( // Evaluate the "main_window" subprogram
      name = "main_window",
      icon = "Graphics\\MaestroIcon.png",
      isMaximized = true,
      title = "The Maestro",
      defaultWidth = 700)

    var language = if (args.length == 0) 0 else if (args(0) == "DE") 1 else 0
    var volume = 50
    var octave = 0
    var recent = ""
    var pedal = false
    val (doo, doodiez, re, rediez, mi, fa, fadiez, sol, soldiez, la, ladiez, si) = (60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71)

    def noteToString(pitch: Int) = pitch % 12 + 60 match {
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

    val synthesizer = MidiSystem.getSynthesizer() // Used to play the user's notes
    synthesizer.open
    val mainChannel = synthesizer.getChannels()(0)

    val receiver = MidiSystem.getReceiver // Receives the sequencer's MIDI data

    val sequencer = MidiSystem.getSequencer(false) // Plays the beat
    sequencer.open
    sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY)
    sequencer.setTempoFactor(math.pow(2, 0.5).toFloat)
    sequencer.getTransmitter.setReceiver(receiver)

    /*
     * Plays a single note.
     * Receives the note's pitch and whether the note should stop automatically after a set period of time.
     */
    def play(pitch: Int, autoStop: Boolean = false) {
      mainChannel.synchronized {
        if (!pedal)
          mainChannel.allNotesOff
        mainChannel.noteOn(pitch + octave * 12, volume)
        recent = recent + noteToString(pitch) + ' '
        mainInstance.set("recent", recent)
        if (autoStop) new Thread {
          override def run {
            Thread.sleep(200)
            mainChannel.synchronized {
              mainChannel.noteOff(pitch, volume)
            }
          }
        }.start
      }
    }

    /*
     *  Plays the background beat.
     *  Receives the indexes of the new and the previous beat.
     */
    def playBeat(beat: Int, prev: Int) {
      if (prev != 0)
        sequencer.stop
      if (beat != 0) {
        val beatName = beat match {
          case 1 => "Jazz"
          case 2 => "Rock"
          case 3 => "Country"
          case 4 => "Funk"
        }
        sequencer.setSequence(MidiSystem.getSequence(new File("Audio\\" + beatName + ".mid")))
        sequencer.start
      }
    }

    /*
     * Changes the musical instrument played.
     * Receives the index of the instrument.
     */
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
      pedal = false
      mainInstance.set("pedal", pedal)
    }

    /*
     * Listeners of variables in the program, which make use of the procedural API.
     */
    mainInstance.when_changed("vol", (_, newVol) => {
      volume = newVol.asInstanceOf[Int]
      for (i <- 0 until 16)
        receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE, i, 7, volume), -1)
    })
    mainInstance.when_changed("up", (_, _) => {
      octave = octave + 1
      mainInstance.set("octave", octave)
    })
    mainInstance.when_changed("down", (_, _) => {
      octave = octave - 1
      mainInstance.set("octave", octave)
    })
    mainInstance.when_changed("clear", (_, _) => {
      recent = ""
      mainInstance.set("recent", "")
    })
    mainInstance.when_changed("about", (_, _) => {
      val aboutInstance = program(
        name = "AboutContent",
        title = "About",
        icon = "Graphics\\MaestroIcon.png",
        isDialog = true)
      aboutInstance.bind("fullPath", graphicsPath _)
      aboutInstance(Array("is_eng=" + (1 - language), "is_deu=" + language))
    })
    mainInstance.when_changed("pedal", (_, newPedal) => pedal = newPedal.asInstanceOf[Boolean])
    mainInstance.when_changed("rhythmchoice", (oldBeat, newBeat) => playBeat(newBeat.asInstanceOf[Int], oldBeat.asInstanceOf[Int]))
    mainInstance.when_changed("tempo", (_, newTempo) => sequencer.setTempoFactor(math.pow(2, (newTempo.asInstanceOf[Int] - 1) / 4.0).toFloat))
    mainInstance.when_changed("instrument", (_, newInst) => changeInstrument(newInst.asInstanceOf[Int]))
    mainInstance.when_changed("langchoice", (_, newLang) => language = newLang.asInstanceOf[Int])

    /*
     * The "play" function is invoked when the user clicks on the image (using the special "action" attribute), and is registered here.
     * It accepts the coordinates of the click and plays the note of the corresponding pitch.
     */
    mainInstance.bind("play", (x: Int, y: Int) => {
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
      }, !pedal)
    })
    def graphicsPath(s: String*) = "Graphics\\" + s(0)
    mainInstance.bind("fullPath", graphicsPath _)

    // Some key listeners
    mainInstance.onKeyPress(key => {
      val pitch = keyToNote(key)
      if (pitch != -1)
        play(pitch, !pedal)
    })
    mainInstance.onKeyRelease(key => mainChannel.synchronized {
      if (!pedal && keyToNote(key) != -1)
        mainChannel.allNotesOff
    })

    val output = mainInstance(args = Array(s"langchoice=$language", "recent=\"\"", "octave=0", "pedal=0")) // Run

    // Close MIDI devices
    synthesizer.close
    sequencer.close
    receiver.close

  }

}
