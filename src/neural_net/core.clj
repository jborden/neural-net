(ns neural-net.core
  (:require [neural-net.keyboard :as keyboard]
            [neural-net.mame :as mame]
            [neural-net.screen :as screen]))

;; note: first time running rom you will be prompted with to type "OK"
(defn simulate
  []
  (future (mame/launch-rom "mrdo"))
  (Thread/sleep 2000) ;; this should correspond to launch time
  (keyboard/keytype "S") ;; just to start the game
  (Thread/sleep 400) ;; the time it takes to start the games
  (keyboard/keytype "5") ;; insert coint
  (keyboard/keytype "1") ;; start game
  (Thread/sleep 1000)
  (screen/screen-grab "mrdo.png") ;; capture a screen shot
  (keyboard/hit-esc) ;; quit the game
  )

