# neural-net

A Clojure Neural Net for playing games

All development was done on a MacBook Pro with macOS Sierra 10.12.2

# External Prerequisites

## MAME

MAME is required to be installed in order to run the rom

Modify src/neural_net/mame.clj to reflect your local installation
of MAME.

## ROM file

You will need a ROM of the game you plan to emulate. This is not provided
due to copyright concerns.

## Tesseract OCR Engine

Source code is available at https://github.com/tesseract-ocr/tesseract but it is
recommended to install it through macports or homebrew
(see https://github.com/tesseract-ocr/tesseract/wiki)

$ sudo port install tesseract

You will also need the langauge pack

$ sudo port install tesseract-eng

Confirm that tesseract is install and working

$ tesseract p1_score.png out digits ; cat out.txt
Tesseract Open Source OCR Engine v3.04.01 with Leptonica
Warning in pixReadMemPng: work-around: writing to a temp file
1000

**Note:** You can ignore the pixReadMemPNG warning.
see https://github.com/tesseract-ocr/tesseract/issues/606

## License

Copyright © 2017 James Borden

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
