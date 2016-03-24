(ns test.check.stateful.stateful-testing
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [test.check.stateful.counter :refer :all]))

;; Define the interface and implementation for our counter
(defprotocol Command
  (run [this sut])
  (next-state [this state]))

(deftype Inc []
  Command
  (run [_ sut] (increment sut))
  (next-state [_ state] (inc state))
  (toString [_] "Inc"))

(deftype Dec []
  Command
  (run [_ sut] (decrement sut))
  (next-state [_ state] (dec state))
  (toString [_] "Dec"))

(deftype Reset []
  Command
  (run [_ sut] (reset-value sut))
  (next-state [_ _] 0)
  (toString [_] "Reset"))

(deftype Get []
  Command
  (run [_ sut] (get-value sut))
  (next-state [_ state] state)
  (toString [_] "Get"))


(def state-generator (gen/elements [(Inc.) (Dec.) (Get.) (Reset.)]))

(defn compute-final-state [initial-state commands]
  (reduce (fn [prev-state command]
            (next-state command prev-state))
          initial-state
          commands))

(defn counter-property [create-counter]
  (prop/for-all [commands (gen/not-empty (gen/vector state-generator))]
                (let [counter (create-counter)
                      initial-state (get-value counter)
                      expected-final-state (compute-final-state initial-state commands)]
                  (doseq [c commands] (run c counter))
                  (= expected-final-state (get-value counter)))))

(deftype Counter [^:unsynchronized-mutable value]
  CounterInterface
  (increment [_] (set! value (+ value 1)))
  (decrement [_] (set! value (- value 1)))
  (get-value [_] value)
  (reset-value [_] (set! value InitialValue)))

(defn new-counter [] (Counter. InitialValue))

(deftype ErroneousCounter [^:unsynchronized-mutable value]
  CounterInterface
  (increment [_] (set! value (+ value 1)))
  (decrement [_] (set! value (- value (if (> value 3) 2 1))))
  (get-value [_] value)
  (reset-value [_] (set! value InitialValue)))

(defn new-erroneous-counter [] (ErroneousCounter. InitialValue))

(comment

  ;; Sample use of the Counter type
  (def c (Counter. 0))
  (get-value c)
  (increment c)
  (get-value c)
  (reset-value c)

  (def i (Inc.))
  (next-state i 3)

  (gen/sample (gen/elements [increment decrement get-value reset-value]) 1)


  (tc/quick-check 100 (counter-property new-counter))
  ;; This will fail the test and we will notice the shrinking
  (tc/quick-check 100 (counter-property new-erroneous-counter))
  (tc/quick-check 100 (counter-property new-erroneous-counter) :seed 1458766296670)
  (tc/quick-check 1000 (counter-property new-erroneous-counter) :seed 1458766296670)
  )
