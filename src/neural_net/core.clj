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
;; note: 1.  would eventually be better to wait for particular cues
;;       and then start the game.
;;       e.g. wait until the prompt screen shows and include test for the "OK"
;;            cue.
;;            wait till the screen has a particular image. Would be
;;            good to take screen shots and isolate a particular part of it.
;;
;;       2. Would be GREAT to make this headless
;;       3. EVEN better to enable a speedup of the game
;;          mame options: -[no]throttle
;;                         -speed <factor>
(defn simulate
  []
  (future (mame/launch-rom "mrdo"))
  (Thread/sleep 2000) ;; this should correspond to launch time
  (keyboard/keytype! "S") ;; just to start the game
  (Thread/sleep 500) ;; the time it takes to start the games
  (keyboard/keytype! "5") ;; insert coint
  (keyboard/keytype! "1") ;; start game
  ;;(at-at/every 500 capture-score thread-pool)
  (Thread/sleep 1500)
  ;; (keyboard/press-key! :left)
  ;; (Thread/sleep 200)
  ;; (keyboard/release-key! :left)
  ;; (keyboard/press-key! :up)
  ;; (Thread/sleep 1600)
  ;; (keyboard/release-key! :up)
  ;; (keyboard/press-key! :left)
  ;; (Thread/sleep 2000)
  ;; (keyboard/release-key! :left)
  ;; (keyboard/hit-key! :right)
  ;; (keyboard/hit-key! :ctrl)
  (screen/save-grayscale (screen/game-grab) "mrdo.png")
  (Thread/sleep 200)
  (keyboard/hit-key! :esc) ;; quit the game
  ;;(at-at/stop-and-reset-pool! thread-pool)
  )

