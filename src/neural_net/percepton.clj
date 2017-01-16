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
;;
;; below would be a MUCH nicer syntax
;; (> (sin (+ (X : 1) (X : 2)) 0))
;; : = row-count in 1st position
;;   = column-count in 2nd position
;;
;; (m/array (map #(Math/sin %) (range 1 21))
;; note: below is for a small matrix, see the comment about performance
;; in core.matrix. Basically, because the operation is O(N), it suggests
;; using a mutable array with mset! instead of mset
;; temp = [0.25 0.5 -0.5]' = (m/array (mapv vector [0.25 0.5 -0.5]))
;; temp(1) = 0; =  (m/mset (m/array (mapv vector [0.25 0.5 -0.5])) 0 0 0)
;; J_reg = (temp' * temp) * (lambda / (2 * m)); =
;; (* (m/select (m/mmul (m/transpose temp) temp) 0 0) (/ lambda (* 2 m)))
;; note: above result should be a scalar, but core.matrix returns
;; a single row matrix. m/select is used to get at this scalar


;; what? vectors have a row-count but not a column count? seems like bad aesthetics
;; (m/column-count the-vector) =>
;; RuntimeException Vector does not have dimension: 1  mikera.vectorz.matrix-api/eval20433/fn--20442 (matrix_api.clj:506)
;;
;; (m/row-count the-vector) => 20

;; another problem: You have to know when a calculation will return a scalar,
;; this isn't handed automatically. so when you get a #vectorz/matrix [[0.7670870967747265]],
;; you have to do a (m/select <matrix> 0 0) on it to get the scalar out

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

(defn lr-cost-function
  [theta X y lambda]
  (let [m (m/row-count y)
        ;; note: if very large theta arrays are being used, this might
        ;; need to be converted to using mset!
        ;;
        ;; The corresponding octave/matlab code is given in comments
        temp (m/mset theta 0 0 0) ;; temp = theta; temp(1) = 0;
        J_reg (* (m/select (m/mmul (m/transpose temp) temp) 0 0)
                 (/ lambda (* 2 m)))
        ;; matlab = (temp' * temp) * (lambda / (2 * m));
        J_factor (- (/ 1 m)) ;; -(1 / m)
        J_term1 (m/select (m/mmul (m/emap #(Math/log %)
                                          (m/emap sigmoid
                                                  (m/mmul (m/transpose theta)
                                                          (m/transpose X))))
                                  y) 0 0)
        ;; matlab = (log(sigmoid(theta' * X')) * y)
        J_term2 (m/emap #(Math/log %)
                        (m/emap #(- 1 %)
                                (m/emap sigmoid
                                        (m/mmul (m/transpose theta)
                                                (m/transpose X)))))
        ;; matlab = log(1 - sigmoid(theta' * X'))
        J_term3 (m/emap #(- 1 %) y) ; (1 - y)
        J (+ (* J_factor
                (+ J_term1 (m/select (m/mmul J_term2 J_term3) 0 0)))
             J_reg)
        ;; matlab = (-(1 / m) * ((log(sigmoid(theta' * X')) * y) + log(1 - sigmoid(theta' * X'))  * (1 - y))) + J_reg;
        ;; gradient
        grad (op/+
              (op/* (/ 1 m) (m/mmul (m/transpose X)
                                    (m/sub (m/emap sigmoid (m/mmul X theta)) y)))
              (op/* temp (/ lambda m)))
        ;; matlab
        ;; grad = (1 / m) * (X' * (sigmoid(X * theta) - y));
        ;; temp = temp .* (lambda/m);
        ;; grad = grad + temp;
        ]
    {:J J
     :grad grad}))
