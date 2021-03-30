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
            :label   "The quick brown fox jumped over the lazy dog."
            :content "The quick brown fox jumped over the lazy dog."}
          (sut/realize-ann-node
            test-db
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
            {:d1 {:id      :d1
                  :content "The quick brown fox jumped over the lazy dog."}
             :d2 {:id :content
                  :d2 "WRONG"}}
            {:a1 {:id      :a1
                  :doc     :d1
                  :profile :p1
                  :concept :c1}
             :a2 {:id      :a2
                  :doc     :d2
                  :profile :p2
                  :concept :c2}}
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
            {:p1 {:id     :p1
                  :colors {:c1 "green"
                           :c2 "red"}}
             :p2 {:id     :p1
                  :colors {:c1 "blue"}}}
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
