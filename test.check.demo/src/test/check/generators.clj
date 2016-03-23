(ns test.check.generators
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]))

(defrecord User [user-name user-id email active?])

(->User "reiddraper" 15 "reid@example.com" true)

(def domain (gen/elements ["gmail.com" "hotmail.com" "computer.org"]))
(def email-gen
  (gen/fmap (fn [[name domain-name]] (str name "@" domain-name))
            (gen/tuple (gen/not-empty gen/string-alphanumeric) domain)))

(last (gen/sample email-gen))

(def user-gen
  (gen/fmap (partial apply ->User)
            (gen/tuple (gen/not-empty gen/string-alphanumeric)
                       gen/nat
                       email-gen
                       gen/boolean)))

(last (gen/sample user-gen))
(clojure.pprint/pprint (gen/sample user-gen))


(comment

  (gen/sample gen/int)
  (gen/sample gen/int 5)
  (gen/sample (gen/vector gen/nat))

  (gen/sample (gen/tuple gen/nat gen/boolean gen/ratio))

  (gen/sample domain)

  )