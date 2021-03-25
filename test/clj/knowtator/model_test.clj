(ns knowtator.model-test
  (:require [clojure.test :refer [testing is deftest]]
            [knowtator.model :as sut]))

(deftest realize-span-test
  (testing "Realize simple span"
    (is (= {:id      :s1
            :ann     :a1
            :start   1
            :end     5
            :doc     :d1
            :concept :c1
            :profile :p1
            :content "he b"}
          (sut/realize-span
            {:d1 {:id      :d1
                  :content "The brown fox jumped over the dog."}
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
            :content "The brown fox jumped over the lazy dog."}
          (sut/realize-ann
            {:p1 {:c1 "green"
                  :c2 "red"}
             :p2 {:c1 "blue"}}
            [{:id      :s1
              :ann     :a1
              :start   1
              :end     10
              :content "The brown fox"}
             {:id      :s2
              :ann     :a2
              :start   2
              :end     11
              :content "WRONG"}
             {:id      :s3
              :ann     :a1
              :start   20
              :end     24
              :content "jumped over the lazy dog."}]
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
