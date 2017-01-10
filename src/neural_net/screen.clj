(ns neural-net.screen
  (:import [java.awt Robot Rectangle])
  (:require [mikera.image.core :refer [save]]
            [mikera.image.filters :refer [grayscale]]))

;; see http://www.mailsend-online.com/blog/screen-captures-with-java-and-clojure.html
;; for a pure java implementation

;; from http://gettingclojure.wikidot.com/cookbook:system
(defn screen-grab []
  (let [;;img-type (second (re-find (re-matcher #"\.(\w+)$" file-name)))
        capture (.createScreenCapture (Robot.)
                                      (Rectangle. (.getScreenSize (Toolkit/getDefaultToolkit))))]
    capture))

(defn score-grab []
  (let [;;img-type (second (re-find (re-matcher #"\.(\w+)$" file-name)))
        capture (.createScreenCapture (Robot.)
                                      (Rectangle. 895 81 228 31))]
    capture))

(defn game-grab []
  (let [;;img-type (second (re-find (re-matcher #"\.(\w+)$" file-name)))
        capture (.createScreenCapture (Robot.)
                                      (Rectangle. 1192 244 178 238))]
    capture))

(defn save-grayscale
  [image file-name]
  (save ((grayscale) image)
        file-name))
