(ns knowtator.knowtator-xml-parser-test
  (:require [clojure.test :refer [deftest is testing]]
            [knowtator.knowtator-xml-parser :as sut]))

(deftest parse-annotations-test
  (testing "Basic parse annotation file"
    (is (= #{{:id      "mention_0",
              :doc     "document1",
              :profile "Default",
              :concept "Pizza"}
             {:id      "mention_1",
              :doc     "document1",
              :profile "profile1",
              :concept "IceCream"}
             {:id      "mention_3",
              :doc     "document2",
              :profile "Default",
              :concept nil}
             {:id      "mention_0",
              :doc     "document3",
              :profile "Default",
              :concept "Food"}
             {:id      "mention_1",
              :doc     "document3",
              :profile "profile1",
              :concept "Food"}
             {:id      "mention_2",
              :doc     "document3",
              :profile "Default",
              :concept "Food"}}
          (-> "test_project_using_uris"
            sut/parse-annotations
            set)))))

;; public static final ProjectCounts defaultCounts = new ProjectCounts(5, 6, 7, 3, 2, 3, 7, 4, 0);

;; /** The constant defaultExpectedTextSources. */
;; public static final int defaultExpectedTextSources = 5;

;; /** The constant defaultExpectedConceptAnnotations. */
;; public static final int defaultExpectedConceptAnnotations = 6;

;; public static final int defaultExpectedStructureAnnotations = 0;

;; /** The constant defaultExpectedSpans. */
;; public static final int defaultExpectedSpans = 7;

;; /** The constant defaultExpectedGraphSpaces. */
;; public static final int defaultExpectedGraphSpaces = 3;

;; /** The constant defaultExpectedProfiles. */
;; public static final int defaultExpectedProfiles = 2;

;; /** The constant defaultExpectedHighlighters. */
;; public static final int defaultExpectedHighlighters = 3;

;; /** The constant defaultExpectedAnnotationNodes. */
;; public static final int defaultExpectedAnnotationNodes = 7;

;; /** The constant defaultExpectedTriples. */
;; public static final int defaultExpectedTriples = 4;

;; public static int defaultAnnotationLayers = 1;
