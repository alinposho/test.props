(ns test.check.stateful.stateful-testing-clojure-way
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [test.check.stateful.counter :refer :all]))

(defmulti run
          "The function that will run the Counter command"
          (fn [command counter] command))

(defmethod run :inc [_ counter]
  (increment counter))

(defmethod run :dec [_ counter]
  (decrement counter))

(defmethod run :get [_ counter]
  (get-value counter))

(defmethod run :reset [_ counter]
  (reset-value counter))

(defmulti next-state
          "The function that will compute the next state for each of the commands"
          (fn [command value] command))

(defmethod next-state :inc [_ value]
  (inc value))

(defmethod next-state :dec [_ value]
  (dec value))

(defmethod next-state :get [_ value]
  value)

(defmethod next-state :reset [_ _]
  InitialValue)


(def state-generator (gen/elements [:inc :dec :get :reset]))

(defn compute-final-state [initial-state commands]
  (reduce (fn [prev-state command] (next-state command prev-state))
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

  (tc/quick-check 100 (counter-property new-counter))
  ;; This will fail the test and we will notice the shrinking
  (tc/quick-check 100 (counter-property new-erroneous-counter))


  )
