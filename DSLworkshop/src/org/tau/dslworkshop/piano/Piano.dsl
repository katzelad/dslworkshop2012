  main_window <-
  (
	(ProgramTitle:?x40)
	---
	((Controls:200x?)|(MiddleArea)|(LangAboutNowPlaying:300x?))
  )[vol=?(60), is_eng=langchoice=0, is_deu=langchoice=1, instrument = ?(0), tempo=?(3), hint=?(0),
    up=?(false), down=?(false), clear=?(false), pedal=?(false), filename=?("MySong"), rhythmchoice=?(0), about=?(false),
	titlebgcolor = 0xFFFFFF, titlefgcolor = 0x2FA1E2, titlefont = ("arial", 12, bold), 
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
  
  ProgramTitle <- 
  (
		  (label)[text="The Maestro", bgcolor = 0xC64F1F, fgcolor = 0xffffff, font = ("Monotype Corsiva", 24, bold), halign=center]
		  ---
		  (:?x8)[bgcolor = 0xC64F1F]
  )
		  
  LangAboutNowPlaying <-
  (
		(Leftmargin)|
		(
			(Upmargin)
			---
			(Lang:250x75)
			---
			(Upmargin)
			---
			(About)
			---
			(Nowplaying)
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
		(RhythmHints:?x140)
		---
		(WrappedPiano:?x?)
	) |
	(Leftmargin)
  )
 
  RhythmHints<-
  (
		  (
					(Upmargin)
					---
					(
						(
								(label:80x40)[text={is_eng=>"Choose Beat:", is_deu=>"Rhythmus Selektieren:", otherwise "Choose Beat:"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|
								(:30x?)[bgcolor=regbgcolor]|
								((combo:63x20 )[text = "None,Jazz,Rock, Country, Funk",enabled = true, bgcolor = regbgcolor, fgcolor = 0x000000, value=rhythmchoice]---(Spacer))|
								(Spacer)
						)
						---
						(
								(label:50x?)[text={is_eng=>"Slower", is_deu=>"Langsam", otherwise "Slower"}, bgcolor = regbgcolor]|
								(scale:80x70)[maxvalue=5, minvalue =1 , value=tempo, bgcolor = regbgcolor]|
								(label:50x?)[text={is_eng=>"Faster", is_deu=>"Schnell", otherwise "Faster"}, bgcolor = regbgcolor]
						)
						---
						(Spacer)
					  )
					)
	|
	(Leftmargin)|
	(Leftmargin)|
	(Leftmargin)|	
	(
			(Upmargin)
			---
			((label:80x20)[text="Too Hard?", bgcolor = regbgcolor, fgcolor = regfgcolor, font = regfont]|(Spacer))
			---
			(	     
				(( (radio:20x20)[checked= hint = i, bgcolor = regbgcolor] | (label:120x?)[text = Hints[i], bgcolor = regbgcolor, fgcolor = regfgcolor, font = regfont] )	
				*|*
				[i=0...2,Hints={nohint, noteshint, keyboardhint}])
				[ noteshint={is_eng=>"Music Notes Hints", is_deu=>"Tastatur Hinweise ", otherwise "Music Notes Hints"},
				  keyboardhint={is_eng=>"Keyboard Hints", is_deu=>"Musiknoten Hinweise", otherwise "Keyboard Hints"},
				  nohint={is_eng=>"No! No Hints", is_deu=>"Nein! Keine Hinweise", otherwise "No! No Hints"}
				 ]|
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
			(Spacer)|
			((button:63x33)[text={is_eng=>"Clear", is_deu=>"Klar", otherwise "Clear"}, checked=clear])
		)
		---
		(Upmargin)
		---
		(textbox)[text=recent, enabled = false, bgcolor = 0xFFEBD6, fgcolor = regfgcolor, font = regfont, halign = center]
		---
		(Upmargin)
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
	((Leftmargin)|(button:100x30)[checked=about, text={is_eng=>"About", is_deu=>"Informationen", otherwise "About"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|(Spacer))
	---
	(Spacer)
  )
 
  AboutContent <-
  (
	(Upmargin)
	---
	(
		(Leftmargin)|
		(
			((label:80x20)[text={is_eng=>"Welcome to ", is_deu=>"Willkommen bei ", otherwise "Welcome to "}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|(label:310x20)[text="The Maestro!", bgcolor=regbgcolor, fgcolor=regfgcolor, font = ("Monotype Corsiva", 11, bold)])
			---
			(label:?x20)[text={is_eng=>"Use the mouse or the keyboard to play.", is_deu=>"Mit der Maus oder der Tastatur zu spielen.", otherwise "Use the mouse or the keyboard to play."}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]
			---
			(label:?x20)[text={is_eng=>"Use the mouse to personalize the various settings.", is_deu=>"Benutzen Sie die Maus, um die verschiedenen Einstellungen personalisieren.", otherwise "Use the mouse to personalize the various settings."}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]
			---
			(
					(image:250x120)[filename="Graphics\\Keyboard.png", bgcolor=regbgcolor]|
					(
							(image:50x100)[filename="Graphics\\Mouse.png", bgcolor=regbgcolor]
							---
							(Spacer)
					)|
					(Spacer)
			)
			---
			(label:?x20)[text={is_eng=>"DSL Workshop, Tel-Aviv University", is_deu=>"DSL Werkstatt, TAU", otherwise "DSL Workshop, TAU"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]
			---
			(label:?x20)[text={is_eng=>"Guided by: Prof. Mooly Sagiv, T.A. Shahar Itzhaki", is_deu=>"Gefuhrt von: Mooly Sagiv, Shahar Itzhaki", otherwise "Guided by: Mooly Sagiv, Shahar Itzhaki"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]
			---	
			(label:?x20)[text={is_eng=>"Created by: Elad Katz, Shir Sofer", is_deu=>"Erstellt von: Elad Katz, Shir Sofer", otherwise "Created by: Elad Katz, Shir Sofer"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]
		)|
		(Spacer)
	)
	---
	(Spacer)
  )[regbgcolor=0xFFFFFF, regfgcolor=0xFF6A26, regfont=("arial", 10, bold), is_eng=?(true), is_deu=?(false)]
  
  WrappedPiano<-
  (
	  ((Leftmargin)|
		(Piano:504x232)|
		(:100x?)[bgcolor=regbgcolor]|
		((Spacer)---(image:98x130)[filename="Graphics\\mouseMaestro.png", bgcolor=regbgcolor])|
		(Spacer))
	  ---
	  (Spacer)
  )
   
  Piano <-
  (
	(Upmargin)
	---
	(
		(image)[filename={hint=0=>"Graphics\\pianoKeys.png", hint=1=>"Graphics\\notesHint.png", hint=2=>"Graphics\\keyboardHint.png", otherwise ""}, action=play]
	)
  )
  
  Nowplaying<-
  (
	(
		((label:150x20)[text={is_eng=>"Now Playing:", is_deu=>"Jetzt Spielen:", otherwise "Now Playing:"}, bgcolor=regbgcolor, fgcolor=regfgcolor, font=regfont]|(Spacer))
		---
		(Upmargin)
		---
		(
				(image:100x100)[filename={instrument=0=>"Graphics\\piano.png", instrument=1=>"Graphics\\violin.png",
						instrument=2=>"Graphics\\saxophone.png",instrument=3=>"Graphics\\guitar.png",instrument=4=>"Graphics\\trumpet.png",instrument=5=>"Graphics\\harp.png",instrument=6=>"Graphics\\drums.png",instrument=7=>"Graphics\\bird.png",otherwise ""},
						bgcolor = regbgcolor]|
				(Spacer)
		)
		---
		(
				(Upmargin)
				*---*
				[i=0...2]
		)
	)
  )
  
  BOTTOM<-(label)[text="bottom placeholder"]
  
  Controls <-
  (
		(LeftmarginControls)|
		(
				(UpmarginControls)
				---
				(label:?x20)[text={is_eng=>"Audio Controls", is_deu=>"Kontrol", otherwise "Controls"}, bgcolor = controlsbgcolor, fgcolor = controlsfgcolor, font = controlsfont]
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
		(label:?x20)[text={is_eng=>"Instrument:", is_deu=>"Instrument:", otherwise "Instrument:"}, bgcolor = controlsbgcolor, fgcolor = controlsfgcolor, font = controlsfont]
		---
		(
			(( (radio:20x20) [checked= instrument = i, bgcolor = controlsbgcolor] | (label)[text = Instruments[i], bgcolor = controlsbgcolor, fgcolor = controlsfgcolor, font = controlsfont] )
	     	*---*
	     	[i=0...7,Instruments={piano, violin, saxophone, guitar, trumpet, harp, drums, bird}])
		)
  )[piano={is_eng=>"Piano", is_deu=>"Klavier", otherwise "Klavier"},
	violin={is_eng=>"Violin", is_deu=>"Violine", otherwise "Violine"},
	saxophone={is_eng=>"Saxophone", is_deu=>"Saxophon", otherwise "Saxophon"},
	guitar={is_eng=>"Guitar", is_deu=>"Gitarre", otherwise "Gitarre"},
	trumpet={is_eng=>"Trumpet", is_deu=>"Trompete", otherwise "Trompete"},
	harp={is_eng=>"Harp", is_deu=>"Harfe", otherwise "Harfe"},
	drums={is_eng=>"Drums", is_deu=>"Schlagzeug", otherwise "Schlagzeug"},
	bird={is_eng=>"Bird", is_deu=>"Vogel", otherwise "Vogel"}]
	
  
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
				(label)[text={is_eng=>"Octave:", is_deu=>"Octave", otherwise "Octave"}, bgcolor = controlsbgcolor, fgcolor = controlsfgcolor, font = controlsfont]
		)|
    	(image:46x106)[filename={octave=1=>"Graphics\\sol8.png",octave=0=>"Graphics\\sol.png",octave=-1=>"Graphics\\fa.png",octave=-2=>"Graphics\\fa8.png", otherwise "Graphics\\sol.png"}, bgcolor = controlsbgcolor]|
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