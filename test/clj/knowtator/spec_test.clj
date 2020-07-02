(ns spec-test
  (:require [knowtator.model.specs :refer :all]
            [clojure.test :refer [is deftest]]
            [knowtator.model.util]
            [clojure.spec.test.alpha :as stest]))

(alias 'stc 'clojure.spec.test.check)

(defn is-results
  [results]
  (if (some :failure results)
    (do
      (println "\nFailed specs:")
      (doseq [result results
              :when  (:failure result)]
        (println (:sym result))
        #_(pprint (or (ex-data (:failure result))
                    (:failure result)))))
    (do
      (println "\nSuccess:")
      (doseq [result results
              :when  (:failure result)]
        (println (:sym result)))
      true)))

(deftest specced-fns
  (->
    (stest/enumerate-namespace 'knowtator.model.util)
    (stest/check {::stc/opts {:num-tests 10
                              :seed      22894}})
    (is-results)
    (is)))
