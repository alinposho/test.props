(ns test.check.stateful.naive-parallel-stateful-testing
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [test.check.stateful.counter :refer :all]
            [test.check.stateful.stateful-testing-clojure-way :refer :all])
  (:import (java.util.concurrent.atomic AtomicInteger)))


(extend-protocol CounterInterface
  AtomicInteger
  (increment [this] (.incrementAndGet this))
  (decrement [this] (.decrementAndGet this))
  (get-value [this] (.get this))
  (reset-value [this] (.set this InitialValue)))

(defn new-thread-safe-counter [] (AtomicInteger. InitialValue))

;; This does not work as expected since it does not compute the possible interleavings.
(defn parallel-counter-property [create-counter]
  (prop/for-all [commands (gen/not-empty (gen/vector state-generator))]
                (let [counter (create-counter)
                      initial-state (get-value counter)
                      expected-final-state (compute-final-state initial-state commands)]
                  (doall (pmap #(run %1 counter) commands)) ;; If counter is not synchronized, we'll get an error
                  (= expected-final-state (get-value counter)))))


(comment
  ;; These do not work as epected because we are not verifying all possible interleavings
  (tc/quick-check 1000 (parallel-counter-property new-counter))
  (tc/quick-check 1000 (parallel-counter-property new-thread-safe-counter))
  )