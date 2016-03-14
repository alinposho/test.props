(ns test.check.stateful-testing-test
  (:require [clojure.test :refer :all]
            [test.check.stateful.stateful-testing :refer :all]
            [clojure.test.check.clojure-test :refer :all])
  (:import (test.check.stateful.stateful_testing Inc Counter)))


(deftest Inc-test
  (is (= 2 (next-state (Inc.) 1)))
  (is (= 3 (run (Inc.) (Counter. 2)))))