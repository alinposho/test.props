(ns test.check.demo-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer :all]))

(defspec first-element-is-min-after-sorting                 ;; the name of the test
         100                                                ;; the number of iterations for test.check to test
         (prop/for-all [v (gen/not-empty (gen/vector gen/int))]
                       (= (apply min v)
                          (first (sort v)))))

(defspec sort-is-idempotent
         100
         (prop/for-all [v (gen/vector gen/int)]
                       (= (sort v) (sort (sort v)))))

(comment
  (run-tests *ns*)
  (ns-map *ns*)


  (sort-is-idempotent)
  )