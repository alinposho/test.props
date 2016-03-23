(defproject test.check.demo "0.1.0-SNAPSHOT"
  :description "A demo test.check project"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/test.check "0.9.0"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
