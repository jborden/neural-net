(ns neural-net.percepton)

(defn sigmoid [z]
  (/ 1 (+ 1 (Math/exp (* -1 z)))))
