(ns test.check.sort-properties
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))


(def ascending-sorted-vector-prop
  (prop/for-all [v (gen/vector gen/int)]
                (let [s (sort v)]
                  (apply < s))))

(comment

  (apply < [1 2 3])                                         ;; 1 < 2 < 3 ?
  (tc/quick-check 100 ascending-sorted-vector-prop)
  )


(def first-element-is-min-after-sorting
  (prop/for-all [v (gen/not-empty (gen/vector gen/int))]
                (= (apply min v)
                   (first (sort v)))))

(comment
  (tc/quick-check 100 first-element-is-min-after-sorting)
  )


