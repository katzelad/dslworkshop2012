main_window <-
  (inner_main_window)[vol=?(60), langchoice=?(0)]
  
  inner_main_window <-
  (
	(ProgramTitle:?x33)
	---
	((Controls:200x?)|(MiddleArea)|(LangAbout))
  )[is_eng=langchoice=0, is_deu=langchoice=1, instrument = ?(0),
	titlebgcolor = 0xFFFFFF, titlefgcolor = 0xFF2B39, titlefont = ("arial", 12, bold),
	regbgcolor = 0xFFFFFF, regfgcolor = 0xFF6A26, regfont = ("arial", 10, bold),
	controlsbgcolor = 0xD6FCFF, controlsfgcolor = 0xFF6A26, controlsfont = ("arial", 10, bold)]
  
  Leftmargin<-
  (:20x?)[bgcolor = regbgcolor]
  
  LeftmarginControls<-
  (:20x?)[bgcolor = controlsbgcolor]

  Rightmargin<-
  (:20x?)[bgcolor = regbgcolor]

  RightmarginControls<-
  (:20x?)[bgcolor = controlsbgcolor]
				  
  Upmargin<-
  (:?x15)[bgcolor = regbgcolor]
  
  UpmarginControls<-
  (:?x15)[bgcolor = controlsbgcolor]
				  
  Spacer<-
  ()[bgcolor=regbgcolor]

  SpacerControls<-
  ()[bgcolor=controlsbgcolor]
  
  ProgramTitle <- (label)[text="TAU PIANO", bgcolor = 0xFFA528, fgcolor = 0xffffff, font = ("arial", 16, bold)]
		  
  LangAbout <-
  (
		(Leftmargin)|
		(
			(Upmargin)
			---
			((Lang:250x75)
			---
			(About))
		)|
		(Spacer)
  )
	
MiddleArea<-
  (
	(Leftmargin) |
	(
		(Upmargin)
		---
		(RecentlyplayedSavetext:?x?)
		---
		(RhythmRecord:?x?)
		---
		(PianoNowplaying:?x320)
	)
  )
 
  RhythmRecord<-
  (
		  (
					(Upmargin)
					---
					(
						(
								(label:80x40)[text={is_eng=>"Choose Rhythm:", is_deu=>"Rhythmus Selektieren:", otherwise "Choose Rhythm:"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|
								(:20x?)[bgcolor=regbgcolor]|
								((combo:63x20 )[text = "None,Jazz,Rock",enabled = true, bgcolor = regbgcolor, fgcolor = 0x000000, value=rhythmchoice]---(Spacer))|
								(Spacer)
						)
						---
						(
								(label:20x?)[text="slow", bgcolor = regbgcolor]|
								(:4x?)[bgcolor = regbgcolor]|
								(scale:115x20)[maxvalue=5, minvalue =1 , value=tempo, bgcolor = regbgcolor]|
								(:4x?)[bgcolor = regbgcolor]|
								(label:20x?)[text="fast", bgcolor = regbgcolor]
						)
						---
						(Spacer)
					  )
					)
	|
		  
	(
			(Upmargin)
			---
			(
				(Leftmargin)|(Leftmargin)|(Leftmargin)|
				(
					(
							(label:100x80)[text={is_eng=>"Choose Audio File Name:", is_deu=>"Gib einen Audio dateinamen:", otherwise "Choose Audio File Name:"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|
							(
									(textbox:140x?)[text=filename, enabled = renamingaudiofile, bgcolor = 0xFFFF00, fgcolor = regfgcolor, font = regfont]
									---
									((button:70x32)[text={is_eng=>"Rename", is_deu=>"Umbenennen", otherwise "Rename"}, enabled=!renamingaudiofile]
									|
									(button:70x32)[text={is_eng=>"Done Renaming", is_deu=>"Getan Umbenennung", otherwise "Done Renaming"}, enabled=renamingaudiofile])
							)
					)
					---
					(		(label:100x40)[text={is_eng=>"Recording Duration:", is_deu=>"Aufzeichnungsdauer:", otherwise "Recording Duration:"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|
							((:?x2)[bgcolor=regbgcolor]---((combo:63x22)[value=2, text ="10,15,30",enabled = true, bgcolor = regbgcolor, fgcolor = 0x000000])---(Spacer))|
							(Spacer)
					)
				)|
				(:20x?)[bgcolor=regbgcolor]|
				((button:60x30)[text={is_eng=>"Record", is_deu=>"Rekord", otherwise "Record"}, bgcolor=0x0000FF, fgcolor=regfgcolor, font=regfont]---(Spacer))|
				(Spacer)
			)
			---
			(Spacer)
		  )	  
  )
  
  RecentlyplayedSavetext<-
  (
		(
			(label:80x?)[text={is_eng=>"Recently Played:", is_deu=>"Zuletzt gespielt:", otherwise "Recently Played:"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|
			(:20x?)[bgcolor=regbgcolor]|
			((button:63x33)[text={is_eng=>"Clear", is_deu=>"Klar", otherwise "Clear"}, checked=clear]---(Spacer))|
			(Leftmargin)|(Leftmargin)|(Leftmargin)|
			(label:100x80)[text={is_eng=>"Choose Text File Name:", is_deu=>"Gib einen Textdateinamen:", otherwise "Choose Text File Name:"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|
			(
					(textbox:140x?)[text=filename, enabled = renamingtextfile = false 	, bgcolor = 0xFFFF00, fgcolor = regfgcolor, font = regfont]
					---
					((button:70x32)[text={is_eng=>"Rename", is_deu=>"Umbenennen", otherwise "Rename"}, enabled=!renamingtextfile]
					|
					(button:70x32)[text={is_eng=>"Done Renaming", is_deu=>"Getan Umbenennung", otherwise "Done Renaming"}, enabled=renamingtextfile])
			)|
			(:20x?)[bgcolor=regbgcolor]|
			((button:63x33)[text={is_eng=>"Save to Text File", is_deu=>"Speichern als Textdatei", otherwise "Save to Text File"}]---(Spacer))|
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
 
  
  PianoNowplaying<-
  (
  ((Piano:550x230)|(Nowplaying:180x?)|(Spacer))
  ---
  (Spacer)
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
  
  BOTTOM<-(label)[text="bottom placeholder"]
  
  Controls <-
  (
		(LeftmarginControls)|
		(
				(UpmarginControls)
				---
				(label:?x20)[text={is_eng=>"Controls", is_deu=>"Kontrol", otherwise "Controls"}, bgcolor = controlsbgcolor, fgcolor = controlsfgcolor, font = controlsfont]
				---
				(UpmarginControls)
				---
				(VOLUME:?x?)
				---
				(UpmarginControls)
				---
				(INSTRUMENT)
				---
				(UpmarginControls)
				---
				(PEDAL)
				---
				(OCTAVE)
				---
				(SpacerControls)
    	)|
    	(RightmarginControls)
  )
  
  VOLUME<-
  (
		(label:?x20)[text={is_eng=>"Volume:", is_deu=>"Volumen", otherwise "Volume"}, bgcolor = controlsbgcolor, fgcolor = controlsfgcolor, font = controlsfont]
		---
		((slider:?x20) [maxvalue=120, minvalue =0 , value=vol]|(:20x?)[bgcolor = controlsbgcolor])
		---
    	(checkbox:20x20)[checked=(vol=0), bgcolor = controlsbgcolor, fgcolor = controlsfgcolor, font = controlsfont] |
		(label:50x?)[text={is_eng=>"Mute", is_deu=>"Dampfen", otherwise "Mute"}, bgcolor = controlsbgcolor, fgcolor = controlsfgcolor, font = controlsfont]|
		(SpacerControls)
  ) [mute=1]
	
  
  INSTRUMENT<-
  (
		(label:?x20)[text={is_eng=>"Instrument:", is_deu=>"Instrument", otherwise "Instrument"}, bgcolor = controlsbgcolor, fgcolor = controlsfgcolor, font = controlsfont]
		---
		(
			(( (radio:20x20) [checked= instrument = i, bgcolor = controlsbgcolor] | (label)[text = Instruments[i], bgcolor = controlsbgcolor, fgcolor = controlsfgcolor, font = controlsfont] )
	     	*---*
	     	[i=0...4,Instruments={piano, violin, drums, guitar, trumpet}])
			|
			(image:100x100)[filename={instrument=0=>"Graphics\\piano.png", instrument=1=>"Graphics\\violin.png",
						instrument=2=>"Graphics\\drums.png",instrument=3=>"Graphics\\guitar.png",instrument=4=>"Graphics\\trumpet.png",otherwise ""},
						bgcolor = controlsbgcolor]
		)
  )[piano={is_eng=>"Piano", is_deu=>"Klavier", otherwise "Piano"},
	guitar={is_eng=>"Guitar", is_deu=>"Gitarre", otherwise "Guitar"},
	violin={is_eng=>"Violin", is_deu=>"Violine", otherwise "Violin"},
	trumpet={is_eng=>"Trumpet", is_deu=>"Trompete", otherwise "Trumpet"},
	drums={is_eng=>"Drums", is_deu=>"Schlagzeug", otherwise "Drums"}]
	
  
  PEDAL<-
  (
		(checkbox:20x20)[checked = pedal, bgcolor = controlsbgcolor, enabled = instrument = 0] |
		(label:40x?)[text={is_eng=>"Pedal", is_deu=>"Pedal", otherwise "Pedal"}, bgcolor = controlsbgcolor, fgcolor=regfgcolor, font=regfont, enabled = instrument = 0]|
		(SpacerControls)
  ) [pedal = ?(false)]
			
  
  OCTAVE<-
  (
		(
				(:?x30)[bgcolor = controlsbgcolor]
				---
				(label)[text={is_eng=>"Octave", is_deu=>"Octave", otherwise "Octave"}, bgcolor = controlsbgcolor, fgcolor = controlsfgcolor, font = controlsfont]
		)|
    	(image:46x106)[filename={octave=1=>"Graphics\\sol8.png",octave=0=>"Graphics\\sol.png",octave=-1=>"Graphics\\fa.png",octave=-2=>"Graphics\\fa8.png", otherwise "Graphics\\sol.png"}]|
    	(
    		(:?x30)[bgcolor = controlsbgcolor]
    		---
    		(button:50x20)[text={is_eng=>"Up", is_deu=>"Herauf", otherwise "Up"}, checked = up, enabled=!(octave=1) ]
    		---
    		(:?x6)[bgcolor = controlsbgcolor]
    		---
    		(button:?x20)[text={is_eng=>"Down", is_deu=>"Hinab", otherwise "Down"}, checked = down, enabled=!(octave=-2)]
    		---
    		(SpacerControls)
    	)
  )