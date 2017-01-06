(ns neural-net.core
  (:require [neural-net.mame :as mame]
            [neural-net.keyboard :as keyboard]))

(defn simulate
  []
  (future (mame/launch-rom "mrdo"))
  (Thread/sleep 1500)
  (keyboard/keytype "5")
  (Thread/sleep 400)
  (keyboard/keytype "1"))

