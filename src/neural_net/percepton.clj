(ns neural-net.percepton
  (:require [clojure.string :as string]
            [clojure.edn :as edn]
            [mikera.image.core :refer [new-image show set-pixels get-pixels]]
            [mikera.image.colours :refer [rgb]]))

(defn sigmoid [z]
  (/ 1 (+ 1 (Math/exp (* -1 z)))))

(defn read-mat-file
  "Read a matrix from a text mat file"
  [filename]
  (let [mat-lines (-> (slurp filename)
                      ;; ignore comments
                      (string/replace #"#.*\n" "")
                      (string/split #"\n"))
        process-lines #(mapv edn/read-string
                             (filterv (comp not string/blank?)
                                      (string/split % #"\s")))
        mat-vectors (mapv process-lines mat-lines)]
    mat-vectors))

(defn numeral-matrix->image
  [numeral-matrix]
  (let [bi (new-image 20 20)
        pixels (atom (get-pixels bi))]
    ;; this should be more functional, but follows directly from
    ;; https://github.com/mikera/imagez#more-examples
    ;; one issue is that clojure isn't reconizing the pixels type
    ;; neural-net.percepton> pixels
    ;; #object["[I" 0x3d73cd56 "[I@3d73cd56"]
    (dotimes [i 400] (let [colormap (nth numeral-matrix i)]
                       (aset @pixels i
                             (rgb colormap colormap colormap))))
    (set-pixels bi @pixels)
    bi))

;; note: this takes about 6 seconds to load, so is commented out
;;(def mat-file (read-mat-file "resources/data/ex3data1_txt.mat"))
;;(def Theta1 (read-mat-file "resources/data/Theta1_txt.mat"))
;;(def Theta2 (read-mat-file "resources/data/Theta2_txt.mat"))

(defn show-nth-numeral
  "Assuming that mat-file is already defined above, show the nth letter"
  [n]
  (show (numeral-matrix->image (nth mat-file n)) :zoom 10.0
        :title (str "Numeral " n)))
