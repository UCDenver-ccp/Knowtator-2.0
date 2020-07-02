(ns knowtator.model-test
  (:require [knowtator.model :as model]
            [clojure.test :as t]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))




(t/deftest ann-color-test
  (t/testing "Annotation color from profile"
    (t/is (= "blue"
            (model/ann-color
              {:id      :a1
               :profile :p1
               :concept :c1
               :doc     :d1}
              {:p1 {:c1 "blue"
                    :c2 "green"}})))))
