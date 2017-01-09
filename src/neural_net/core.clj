(ns neural-net.core
  (:require [neural-net.keyboard :as keyboard]
            [neural-net.mame :as mame]
            [neural-net.screen :as screen]
            [neural-net.tesseract :as tesseract]
            [overtone.at-at :as at-at]))

(def score (atom 0))

(def thread-pool (at-at/mk-pool))

(defn capture-score
  "Capture the score has the game is playing"
  []
  (screen/score-grab "score.png")
  (println (tesseract/read-score "score.png"))
  (reset! score (tesseract/read-score "score.png")))

;; note: first time running rom you will be prompted with to type "OK"
(defn simulate
  []
  (future (mame/launch-rom "mrdo"))
  (Thread/sleep 2000) ;; this should correspond to launch time
  (keyboard/keytype "S") ;; just to start the game
  (Thread/sleep 400) ;; the time it takes to start the games
  (keyboard/keytype "5") ;; insert coint
  (keyboard/keytype "1") ;; start game
  ;;(Thread/sleep 1000)
  (at-at/every 500 capture-score thread-pool)
  ;;(keyboard/hit-esc) ;; quit the game
  )

