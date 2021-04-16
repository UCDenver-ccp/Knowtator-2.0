(ns knowtator.model-test
  (:require [clojure.test :refer [testing is deftest]]
            [knowtator.model :as sut]))

(def test-db {:text-annotation {:profiles [{:id     :p1
                                            :colors {:c1 "green"
                                                     :c2 "red"}}
                                           {:id     :p2
                                            :colors {:c1 "blue"}}]
                                :spans    [{:id    :s1
                                            :ann   :a1
                                            :start 0
                                            :end   15}
                                           {:id      :s2
                                            :ann     :a2
                                            :start   2
                                            :end     3
                                            :content "WRONG"}
                                           {:id    :s3
                                            :ann   :a1
                                            :start 16
                                            :end   45}]
                                :docs     [{:id      :d1
                                            :content "The quick brown fox jumped over the lazy dog."}
                                           {:id      :d2
                                            :content "WRONG"}]
                                :anns     [{:id      :a1
                                            :doc     :d1
                                            :profile :p1
                                            :concept :c1}
                                           {:id      :a2
                                            :doc     :d2
                                            :profile :p2
                                            :concept :c2}]}})

(deftest realize-ann-node-test
  (testing "Realize simple ann-node"
    (is (= {:id      :n1
            :ann     :a1
            :doc     :d1
            :concept :c1
            :profile :p1
            :color   "green"
            :label   "The quick brown fox jumped over the lazy dog.\nC1"
            :content "The quick brown fox jumped over the lazy dog."}
          (sut/realize-ann-node
            test-db
            {:c1 "C1"
             :c2 "C2"}
            true
            {:id  :n1
             :ann :a1})))))

(deftest realize-span-test
  (testing "Realize simple span"
    (is (= {:id      :s1
            :ann     :a1
            :start   1
            :end     5
            :doc     :d1
            :concept :c1
            :profile :p1
            :content "he q"}
          (sut/realize-span
            {:text-annotation {:docs [{:id      :d1
                                       :content "The quick brown fox jumped over the lazy dog."}
                                      {:id :content
                                       :d2 "WRONG"}]
                               :anns [{:id      :a1
                                       :doc     :d1
                                       :profile :p1
                                       :concept :c1}
                                      {:id      :a2
                                       :doc     :d2
                                       :profile :p2
                                       :concept :c2}]}}
            {:id    :s1
             :ann   :a1
             :start 1
             :end   5})))))

(deftest realize-ann-test
  (testing "Realize simple ann with disjoint spans."
    (is (= {:id      :a1
            :profile :p1
            :concept :c1
            :doc     :d1
            :color   "green"
            :content "The quick brown fox jumped over the lazy dog."}
          (sut/realize-ann
            (sut/realize-spans test-db)
            {:id      :a1
             :profile :p1
             :concept :c1
             :doc     :d1})))))

(deftest make-overlapping-spans-test
  (testing "Empty input"
    (is (empty? (sut/make-overlapping-spans []))))
  (testing "Exact overlap"
    (is (= 1 (-> '({:id :Z+O/+!e+_, :ann :H!3_?cS?/-, :start 79, :end 92}
                   {:id #{:Z+O/+!e+_ :Ff/aa?W}, :ann #{:H!3_?cS?/- :Q+d/K*+?EY}, :start 79, :end 92})
               sut/make-overlapping-spans
               count)))
    (is (= 1 (-> '({:id    #{:LB0/_**Y956. :F?i!XJ/Ff :.-5!+o_/l-.E.},
                    :ann   #{:BBL:E9/muM :jz.zzF/DIDi :La9b_/R},
                    :start 30,
                    :end   36}
                   {:id    #{:_XE/x :LB0/_**Y956. :F?i!XJ/Ff :.-5!+o_/l-.E. :Wr./R?GQ-},
                    :ann   #{:BBL:E9/muM :jz.zzF/DIDi :++oLJj0/TC! :La9b_/R :y?/Ss8d4.},
                    :start 30,
                    :end   36})
               sut/make-overlapping-spans
               count)))))

(deftest remove-selected-item-test
  (testing "No sub-items"
    (is (= {:text-annotation {:docs           [{:id :d1}]
                              :anns           nil
                              :spans          nil
                              :ann-nodes      nil
                              :assertion-anns nil
                              :graphs         nil}
            :selection       {:docs           nil
                              :anns           nil
                              :spans          nil
                              :ann-nodes      nil
                              :assertion-anns nil
                              :graphs         nil}}
          (sut/remove-matching-sub-items {:text-annotation {:docs [{:id :d1}
                                                                   {:id :d2}]}
                                          :selection       {:docs :d2}}
            :id :docs :d2)
          #_(sut/remove-selected-item {:text-annotation {:docs [{:id :d1}
                                                                {:id :d2}]}
                                       :selection       {:docs :d2}}
              :doc :docs)))

    (is (= {:text-annotation {:docs  [{:id :d1}
                                      {:id :d2}]
                              :anns  [{:id  :a2
                                       :doc :d2}]
                              :spans nil}
            :selection       {:docs  :d2
                              :anns  nil
                              :spans nil}}
          (sut/remove-selected-item {:text-annotation {:docs [{:id :d1}
                                                              {:id :d2}]

                                                       :anns [{:id  :a1
                                                               :doc :d1}
                                                              {:id  :a2
                                                               :doc :d2}]}
                                     :selection {:docs :d2
                                                 :anns :a1}}
            :anns))))

  (testing "With sub-items"
    (is (= {:text-annotation {:docs           [{:id :d1}]
                              :anns           []
                              :spans          []
                              :ann-nodes      nil
                              :assertion-anns nil
                              :graphs         nil}
            :selection       {:docs           nil
                              :anns           nil
                              :spans          nil
                              :ann-nodes      nil
                              :assertion-anns nil
                              :graphs         nil}}
          (sut/remove-selected-item {:text-annotation {:docs  [{:id :d1}
                                                               {:id :d2}]
                                                       :anns  [{:id  :a2
                                                                :doc :d2}]
                                                       :spans [{:id  :s1
                                                                :ann :a2}]}
                                     :selection       {:docs :d2
                                                       :anns :a2}}
            :docs)))

    (is (= {:text-annotation {:docs  [{:id :d1}
                                      {:id :d2}]
                              :anns  [{:id  :a2
                                       :doc :d2}]
                              :spans [{:id  :s2
                                       :ann :a2}]}
            :selection       {:docs  :d2
                              :anns  nil
                              :spans nil}}
          (sut/remove-selected-item {:text-annotation {:docs [{:id :d1}
                                                              {:id :d2}]

                                                       :anns  [{:id  :a1
                                                                :doc :d1}
                                                               {:id  :a2
                                                                :doc :d2}]
                                                       :spans [{:id  :s1
                                                                :ann :a1}
                                                               {:id  :s2
                                                                :ann :a2}]}
                                     :selection {:docs  :d2
                                                 :anns  :a1
                                                 :spans :s1}}
            :anns)))

    (is (= {:text-annotation {:spans          [{:id  :s6
                                                :ann :a3}]
                              :ann-nodes      nil
                              :assertion-anns nil
                              :graphs         nil
                              :anns           [{:id      :a3
                                                :profile :p1
                                                :concept :c1
                                                :doc     :d2}]
                              :profiles       nil
                              :docs           [{:id      :d2
                                                :content "hey"}]}
            :selection       {:project        "default"
                              :docs           nil
                              :graphs         nil
                              :ann-nodes      nil
                              :assertion-anns nil
                              :anns           nil
                              :profiles       :p1
                              :concepts       :c1
                              :spans          nil
                              :review-type    :anns
                              :ann-props      "http://www.w3.org/2004/02/skos/core#prefLabel"}}
          (sut/remove-selected-item
            {:text-annotation {:spans    [{:id  :s1
                                           :ann :a1}
                                          {:id  :s2
                                           :ann :a1}
                                          {:id  :s3
                                           :ann :a2}
                                          {:id  :s4
                                           :ann :a2}
                                          {:id  :s5
                                           :ann :a1}
                                          {:id  :s6
                                           :ann :a3}]
                               :anns     [{:id      :a1
                                           :profile :p1
                                           :concept :c1
                                           :doc     :d1}
                                          {:id      :a2
                                           :profile :p2
                                           :concept :c2
                                           :doc     :d1}
                                          {:id      :a3
                                           :profile :p1
                                           :concept :c1
                                           :doc     :d2}]
                               :profiles nil
                               :docs     [{:id      :d2
                                           :content "hey"}
                                          {:id      :d1
                                           :content "there"}]}
             :selection       {:project     "default"
                               :docs        :d1
                               :anns        nil
                               :profiles    :p1
                               :concepts    :c1
                               :spans       nil
                               :review-type :anns
                               :ann-props   "http://www.w3.org/2004/02/skos/core#prefLabel"}}
            :docs)))))

(deftest add-node-test
  (is (= {:text-annotation {:graphs [{:id    :g0
                                      :nodes [{:id      :n1
                                               :ann     :a1
                                               :x       -122
                                               :y       -189
                                               :label   "test"
                                               :physics false}]}]}
          :selection       {:anns :a1}}
        (sut/add-node {:text-annotation {:graphs [{:id :g0}]}
                       :selection       {:anns :a1}}
          :g0
          {:id :disregarded
           :x  -122
           :y  -189}))))

(deftest add-edge-test
  (is (= {:text-annotation {:graphs [{:id    :g0
                                      :nodes [{:id :n1}
                                              {:id :n2}]
                                      :edges [{:id   :e1
                                               :from :n1
                                               :to   :n2}]}]}
          :selection       {:anns :a1}}
        (sut/add-edge {:text-annotation {:graphs [{:id    :g0
                                                   :nodes [{:id :n1}
                                                           {:id :n2}]}]}
                       :selection       {:anns :a1}}
          :g0
          {:from :n1
           :to   :n2}))))
