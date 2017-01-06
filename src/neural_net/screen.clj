(ns neural-net.screen
  (:import (java.awt Robot Rectangle Toolkit)
           (java.awt.image BufferedImage)
           (java.io File IOException)
           (javax.imageio ImageIO)))

;; see http://www.mailsend-online.com/blog/screen-captures-with-java-and-clojure.html
;; for a pure java implementation

;; from http://gettingclojure.wikidot.com/cookbook:system
(defn screen-grab [file-name]
  (let [img-type (second (re-find (re-matcher #"\.(\w+)$" file-name)))
        capture (.createScreenCapture (Robot.)
                                      (Rectangle. (.getScreenSize (Toolkit/getDefaultToolkit))))
        file (File. file-name)]
    (ImageIO/write capture img-type file)))

(defn score-grab [file-name]
  (let [img-type (second (re-find (re-matcher #"\.(\w+)$" file-name)))
        capture (.createScreenCapture (Robot.)
                                      (Rectangle. 895 81 228 31))
        file (File. file-name)]
    (ImageIO/write capture img-type file)))
