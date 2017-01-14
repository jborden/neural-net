(ns neural-net.percepton
  (:require [clojure.string :as string]
            [clojure.edn :as edn]
            [mikera.image.core :refer [new-image show set-pixels get-pixels]]
            [mikera.image.colours :refer [rgb]]
            [clojure.core.matrix :as m]
            [clojure.core.matrix.operators :as op]))
;; notes on core.matrix
;; (slice mat n) is equivalent to (nth vec-of-vecs n)
;; (vec mat) converts the matrix to a clojure vector
;; (m/shape matrix) - return the shape of the data
;; (m/array (mapv vector the-vector)) - convert vector to column matrix
;; alternative
;; (m/reshape the-vector [(m/row-count the-vector) 1])
;;
;; note: octave starts index at 1 (it's sane)
;;       core.matrix starts index at 0
;; octave core.matrix equivalents
;; size(X,1) = (m/dimension-count X 0)
;; zeros(5000,1) = (m/zero-vector 5000)
;; ones(5000,1) =  (m/add (m/zero-matrix 5000 1) 1)
;; [ones(m, 1) X] = (m/join-along 1 (m/add (m/zero-matrix (m/dimension-count X 0) 1) 1) X)
;; X' = (m/transpose X)
;; Theta1 * X' = (m/mmul Theta1 (m/transpose X))
;; z2(1,5000) = (m/select z2 0 4999)
;; z2(25,5000) = (m/select z2 24 4999)
;; sigmoid(z2) = (m/emap sigmoid z2)
;; [max_values, max_value_indices] = max(Z)
;; max_values = (mapv m/emax (m/columns Z))
;; max_value_indices = (mapv #(.indexOf (vec %) (m/emax %)) (m/columns Z))
;; [ones(20,1) (exp(1) * sin(1:1:20))' (exp(0.5) * cos(1:1:20))'] =
;; (m/join-along 1 (m/add (m/zero-matrix 20 1) 1)
;; note: You can not tranpose a vector to an array in core.matrix, unfortunately
;;                 (m/array (mapv vector (m/array (map #(* (Math/exp 1) (Math/sin %)) (range 1 21)))))
;;                 (m/array (mapv vector (m/array (map #(* (Math/exp 0.5) (Math/cos %)) (range 1 21))))))
;; alternatively
;; (m/join-along 1 (m/add (m/zero-matrix 20 1) 1)
;;               (m/reshape (m/array (map #(* (Math/exp 1) (Math/sin %)) (range 1 21))) [20 1])
;;               (m/reshape (m/array (map #(* (Math/exp 0.5) (Math/cos %)) (range 1 21))) [20 1]))

;; note: X = [ones(20,1) (exp(1) * sin(1:1:20))' (exp(0.5) * cos(1:1:20))']
;; sin(X(:,1) + X(:,2)) > 0; =
;; (m/gt (m/reshape (m/emap #(Math/sin %) (op/+ (m/slice X 1 0) (m/slice X 1 1))) [(m/row-count (m/slice X 1 0)) 1]) 0)
;; where
;; X(:,1) = (m/slice X 1 0)
;; X(:,2) = (m/slice X 1 1)

;; below would be a MUCH nicer syntax
;; (> (sin (+ (X : 1) (X : 2)) 0))
;; : = row-count in 1st position
;;   = column-count in 2nd position
;;
;; (m/array (map #(Math/sin %) (range 1 21))

;; what? vectors have a row-count but not a column count? seems like bad aesthetics
;; (m/column-count the-vector) =>
;; RuntimeException Vector does not have dimension: 1  mikera.vectorz.matrix-api/eval20433/fn--20442 (matrix_api.clj:506)
;;
;; (m/row-count the-vector) => 20

(defn sigmoid [z]
  (/ 1 (+ 1 (Math/exp (* -1 z)))))

(m/set-current-implementation :vectorz)

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

;; note: X takes about 6 seconds to load
;; (defonce X (m/array
;;             (read-mat-file "resources/data/ex3data1_txt.mat")))
;; (defonce Theta1 (m/array (read-mat-file "resources/data/Theta1_txt.mat")))
;; (defonce Theta2 (m/array (read-mat-file "resources/data/Theta2_txt.mat")))

(defn show-nth-numeral
  "Show the nth numeral in numeral-mat"
  [number-mat n]
  (show (numeral-matrix->image (vec (m/slice number-mat n))) :zoom 10.0
        :title (str "Numeral " n)))

(defn predict
  [Theta1 Theta2 X]
  (let [n (m/dimension-count X 0)
        p (m/zero-vector n)
        X (m/join-along 1 (m/add (m/zero-matrix n 1) 1) X)
        z2 (m/mmul Theta1 (m/transpose X))
        a2 (m/emap sigmoid z2)
        a2 (m/join-along 1 (m/add (m/zero-matrix n 1) 1) (m/transpose a2))
        z3 (m/mmul Theta2 (m/transpose a2))
        a3 (m/emap sigmoid z3)
        max-value-indices (mapv #(.indexOf (vec %) (m/emax %)) (m/columns a3))]
    (m/transpose max-value-indices)))

(defn predict-nth-numeral
  "Predict what the nth numeral in number-mat should be"
  [number-mat n]
  (mod (+ (first (predict Theta1 Theta2 (m/array [(m/slice number-mat n)]))) 1)
       10))

(def X
  (m/join-along 1 (m/add (m/zero-matrix 20 1) 1)
                (m/reshape (m/array (map #(* (Math/exp 1) (Math/sin %))
                                         (range 1 21))) [20 1])
                (m/reshape (m/array (map #(* (Math/exp 0.5) (Math/cos %))
                                         (range 1 21))) [20 1])))
(def y
  (m/gt (m/reshape (m/emap #(Math/sin %) (op/+ (m/slice X 1 0) (m/slice X 1 1)))
                   [(m/row-count (m/slice X 1 0)) 1]) 0))
