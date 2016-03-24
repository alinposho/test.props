(ns test.check.simple-failing-properties
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))


(defn decrement [x]
  (if (> x 5)
    (- x 2)
    (- x 1)))

(def decrement-prop
  (prop/for-all [x gen/int]
                (= (- x 1) (decrement x))))

(comment
  (tc/quick-check 100 decrement-prop)
  )

;; Test shrinking in action
;; Totally contrieved property example
(def prop-no-42
  (prop/for-all [v (gen/vector gen/int)]
                (not (some #{42} v))))

(comment
  (tc/quick-check 100 prop-no-42)
  )


