(ns neural-net.mame
  (:require [clojure.java.shell :refer [sh]]))

(def mame-dir "/Users/james/mame/")

(def mame-exe
  (str mame-dir "mame64"))

(def rom-path
  (str mame-dir "roms"))

(def rom "mrdo")

(defn launch-rom
  "Given a str rom, launch rom"
  [rom]
  (sh mame-exe "-rompath" rom-path rom))
