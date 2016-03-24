(ns test.check.simple-examples
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))


(def min-is-smaller-than-all-elements
  (prop/for-all [v (gen/not-empty (gen/vector gen/int))]
                (let [min-elem (apply min v)]
                  (every? (fn [elem] (<= min-elem elem)) v))))

(comment
  (tc/quick-check 1000 min-is-smaller-than-all-elements)
  )


(defn ascending?
  "clojure.core/sorted? doesn't do what we might expect, so we write our
  own function"
  [coll]
  (every? (fn [[a b]] (<= a b)) (partition 2 1 coll)))


(def ascending-sorted-vector-prop
  (prop/for-all [v (gen/vector gen/int)]
                (let [s (sort v)]
                  (ascending? s))))

(def descending-sorted-vector-prop
  (prop/for-all [v (gen/vector-distinct gen/int {:min-elements 2})]
                (let [s (sort > v)]
                  (not (ascending? s)))))

(comment
  (tc/quick-check 100 ascending-sorted-vector-prop)
  (tc/quick-check 100 descending-sorted-vector-prop)
  (tc/quick-check 100 min-is-smaller-than-all-elements :seed 1457950593723)

  )
