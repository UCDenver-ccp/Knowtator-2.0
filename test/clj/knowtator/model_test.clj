(ns knowtator.model-test
  (:require [knowtator.model.util :as model]
            [clojure.test :as t]
            [knowtator.util :as util]))

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

(t/deftest make-overlapping-spans-test
  (t/testing "Empty input"
    (t/is (empty? (model/make-overlapping-spans []))))
  (t/testing "Exact overlap"
    (t/is (= 1 (-> '({:id :Z+O/+!e+_, :ann :H!3_?cS?/-, :start 79, :end 92}
                     {:id #{:Z+O/+!e+_ :Ff/aa?W}, :ann #{:H!3_?cS?/- :Q+d/K*+?EY}, :start 79, :end 92})
                 model/make-overlapping-spans
                 count)))
    (t/is (= 1 (-> '({:id    #{:LB0/_**Y956. :F?i!XJ/Ff :.-5!+o_/l-.E.},
                      :ann   #{:BBL:E9/muM :jz.zzF/DIDi :La9b_/R},
                      :start 30,
                      :end   36}
                     {:id    #{:_XE/x :LB0/_**Y956. :F?i!XJ/Ff :.-5!+o_/l-.E. :Wr./R?GQ-},
                      :ann   #{:BBL:E9/muM :jz.zzF/DIDi :++oLJj0/TC! :La9b_/R :y?/Ss8d4.},
                      :start 30,
                      :end   36})
                 model/make-overlapping-spans
                 count)))))
