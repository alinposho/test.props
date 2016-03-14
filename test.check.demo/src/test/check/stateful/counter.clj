(ns test.check.stateful.counter)

;; Define the interface and implementation for our counter

(defprotocol CounterInterface
  (increment [this])
  (decrement [this])
  (get-value [this])
  (reset-value [this]))

(def InitialValue 0)