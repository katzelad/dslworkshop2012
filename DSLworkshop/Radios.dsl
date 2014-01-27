main_window <-
    (((radio:30x30)[checked = v = i] | (label)[text = Animals[i]])
                           *---*
     [i=0...6,Animals={"Alpaca","Bunny","Cat","Dog","Elephant","Fox","Goose"}])[v=?(3)]