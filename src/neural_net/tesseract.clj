(ns neural-net.tesseract
  (:require [clojure.java.io :refer [file]]
            [clojure.java.shell :refer [sh]]))

;; note: attempt to utilize tess4j
;; (.doOCR (Tesseract.) (file "score.png")) kills it
;; also crashes, but shows how to set digits config
;; (doto (Tesseract/getInstance) (.setConfigs '("digits")) (.doOCR (file "score.png")))
;; doesn't work
;; (.doOCR (Tesseract.) (file "score.png") (Rectangle. 0 0)) 
;; doesn't work
;; note: attemp to use tess4j with imagez library
;; (.doOCR (Tesseract.) (image/load-image "score.png"))

;; tess4j is a nightmare.. doesn't want to run. Instead, using command line
;; subprocess to read the score

;; todo: image should be put directly in as stdin
(defn read-score
  "Convert the image given by file-name to a number"
  [file-name]
  (read-string (:out (sh "tesseract" file-name "stdout" "digits"))))
