main_window <-
  (inner_main_window)[vol=?(60), langchoice=?(0)]
  
  inner_main_window <-
  (
	(ProgramTitle:?x33)
	---
	(Upmargin)
	---
	((Leftmargin)|(Controls:200x?)|(Leftmargin)|(RightHandArea))
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
			(label:80x?)[text={is_eng=>"Recently Played:", is_deu=>"Zuletzt gespielt:", otherwise "Recently Played:"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|
			(:20x?)[bgcolor=regbgcolor]|
			(button:63x33)[text={is_eng=>"Clear", is_deu=>"Klar", otherwise "Clear"}]|
			(:40x33)[bgcolor=regbgcolor]|
			(label:100x?)[text={is_eng=>"Choose File Name:", is_deu=>"Gib einen Dateinamen:", otherwise "Choose File Name:"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|
			(textbox:100x?)[text="b", enabled = false, bgcolor = 0xFFFF00, fgcolor = regfgcolor, font = regfont]|
			(:20x33)[bgcolor=regbgcolor]|
			(button:63x33)[text={is_eng=>"Save to Text File", is_deu=>"Speichern als Textdatei", otherwise "Save to Text File"}]|
			(Spacer)
		)
		---
		(Upmargin)
		---
		(textbox)[text=recent, enabled = false, bgcolor = 0xFFAA00, fgcolor = regfgcolor, font = regfont, halign = center]
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
  ((Piano:550x230)|(Nowplaying:180x?)|(Spacer))
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
		(label:?x20)[text={is_eng=>"Controls", is_deu=>"Kontrol", otherwise "Controls"}, bgcolor = titlebgcolor, fgcolor = titlefgcolor, font = titlefont]
		---
		(Upmargin)
		---
		(VOLUME:?x?)
		---
		(Upmargin)
		---
		(INSTRUMENT)
		---
		(Upmargin)
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
  ) [mute=1]
	
  
  INSTRUMENT<-
  (
		(label:?x20)[text={is_eng=>"Instrument:", is_deu=>"Instrument", otherwise "Instrument"}, bgcolor = regbgcolor, fgcolor = regfgcolor, font = regfont]
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
		(label:40x?)[text={is_eng=>"Pedal", is_deu=>"Pedal", otherwise "Pedal"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|
		(Spacer)
  ) [pedal = ?(false)]
			
  
  OCTAVE<-
  (
		(
				(:?x30)[bgcolor=regbgcolor]
				---
				(label)[text={is_eng=>"Octave", is_deu=>"Octave", otherwise "Octave"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]
		)|
    	(image:46x106)[filename={octave=1=>"Graphics\\sol8.png",octave=0=>"Graphics\\sol.png",octave=-1=>"Graphics\\fa.png",octave=-2=>"Graphics\\fa8.png", otherwise "Graphics\\sol.png"}]|
    	(
    		(:?x30)[bgcolor=regbgcolor]
    		---
    		(button:50x20)[text={is_eng=>"Up", is_deu=>"Herauf", otherwise "Up"}, checked = up, enabled=!(octave=1) ]
    		---
    		(:?x6)[bgcolor=regbgcolor]
    		---
    		(button:?x20)[text={is_eng=>"Down", is_deu=>"Hinab", otherwise "Down"}, checked = down, enabled=!(octave=-2)]
    		---
    		(Spacer)
    	)
  )