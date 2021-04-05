(ns util-test
  (:require  #?(:clj [clojure.test :refer [deftest testing is]]
                :cljs [cljs.test :refer [deftest testing is] :include-macros true])
             [knowtator.util :as sut]))

(deftest compare-alpha-num-test
  (is (= ["1" "2"
          "A1" "A3" "A3A" "A3B" "A4" "A10" "A11" "A12"
          "B2" "B10"
          "F1" "F3"
          "GS2" "GS116"]
        (sort sut/compare-alpha-num ["GS2" "GS116"
                                     "A1", "A10", "A11", "A12", "A3A", "A3B", "A3", "A4",
                                     "B10", "B2",
                                     "F1", "1", "2", "F3"]))))
