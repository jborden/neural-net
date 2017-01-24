(ns neural-net.test.percepton
  (:require [clojure.core.matrix :as m]
            [clojure.core.matrix.operators :as op]
            [clojure.test :refer [is deftest testing use-fixtures]]
            [neural-net.percepton :as percepton]))

(def X
  (m/join-along 1 (m/add (m/zero-matrix 20 1) 1)
                (m/reshape (m/array (map #(* (Math/exp 1) (Math/sin %))
                                         (range 1 21))) [20 1])
                (m/reshape (m/array (map #(* (Math/exp 0.5) (Math/cos %))
                                         (range 1 21))) [20 1])))

(def y
  (m/gt (m/reshape (m/emap #(Math/sin %) (op/+ (m/slice X 1 0) (m/slice X 1 1)))
                   [(m/row-count (m/slice X 1 0)) 1]) 0))

(def Xm
  ;; matlab
  ;;
  ;; [ -1 -1 ; -1 -2 ; -2 -1 ; -2 -2 ; ...
  ;;   1 1 ;  1 2 ;  2 1 ; 2 2 ; ...
  ;;   -1 1 ;  -1 2 ;  -2 1 ; -2 2 ; ...
  ;;   1 -1 ; 1 -2 ;  -2 -1 ; -2 -2 ];
  (m/array [[-1 -1] [-1 -2] [-2 -1] [-2 -2]
            [1 1] [1 2] [2 1] [2 2]
            [-1 1] [-1 2] [-2 1] [-2 2]
            [1 -1] [1 -2] [-2 -1] [-2 -2]]))

(def ym
  ;; matlab
  ;; [ 1 1 1 1 2 2 2 2 3 3 3 3 4 4 4 4 ]';
  (m/transpose (m/array [[1 1 1 1 2 2 2 2 3 3 3 3 4 4 4 4]])))

(def t1
  ;; matlab
  ;; sin(reshape(1:2:24, 4, 3));
  (m/emap #(Math/sin %)
          ;; reshape(1:2:24, 4, 3) in matlab
          ;; np.transpose(np.reshape(list(range(1,24,2)),[3,4])) in python
          ;; see:https://groups.google.com/forum/#!msg/numerical-clojure/zebBCa68eTw/lMv-GTfXwikJ
          ;; for some discussion related to this issue
          (m/transpose (m/reshape (m/array [(range 1 24 2)]) [3 4]))))

(def t2
  ;; matlab
  ;; cos(reshape(1:2:40, 4, 5));
  (m/emap #(Math/cos %)
          (m/transpose (m/reshape (m/array [(range 1 40 2)]) [5 4]))))

(deftest simple-data-tests
  []
  (is (= (percepton/lr-cost-function (m/transpose (m/array [[0.25 0.5 -0.5]]))
                                     X y 0.1))
      {:J 0.7670870967747265
       :grad  (m/array [[0.15835361724505678],
                        [0.22405002743117702],
                        [-0.10740598766374065]])}))
