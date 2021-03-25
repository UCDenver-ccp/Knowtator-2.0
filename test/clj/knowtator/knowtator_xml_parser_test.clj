(ns knowtator.knowtator-xml-parser-test
  (:require [clojure.test :refer [deftest is testing]]
            [knowtator.knowtator-xml-parser :as sut]
            [clojure.java.io :as io]
            [knowtator.util :as util]))

(def project-file (io/resource "test_project_using_uris"))
(def annotation-xmls (sut/read-project-xmls "Annotations" project-file))

(deftest parse-profile-test
  (testing "Basic"
    (is (= [{:id     :p1
             :colors {"c1" "blue"}}]
          (sut/parse-profile (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :profile
                        :attrs   {:id "p1"}
                        :content [{:tag   :highlighter
                                   :attrs {:color "blue"
                                           :class "c1"}}]}]}))))
  (testing "Multiple profiles"
    (is (= [{:id     :p1
             :colors {"c1" "blue"}}
            {:id     :p2
             :colors {"c1" "blue"}}]
          (sut/parse-profile (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :profile
                        :attrs   {:id "p1"}
                        :content [{:tag   :highlighter
                                   :attrs {:color "blue"
                                           :class "c1"}}]}
                       {:tag     :profile
                        :attrs   {:id "p2"}
                        :content [{:tag   :highlighter
                                   :attrs {:color "blue"
                                           :class "c1"}}]}]}))))
  (testing "Missing ID"
    (is (= [{:id     :profile-1
             :colors {"c1" "blue"}}]
          (sut/parse-profile (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :profile
                        :attrs   {}
                        :content [{:tag   :highlighter
                                   :attrs {:color "blue"
                                           :class "c1"}}]}]}))))
  (testing "Multiple colors"
    (is (= [{:id     :p1
             :colors {"c1" "blue"
                      "c2" "red"}}]
          (sut/parse-profile (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :profile
                        :attrs   {:id "p1"}
                        :content [{:tag   :highlighter
                                   :attrs {:color "blue"
                                           :class "c1"}}
                                  {:tag   :highlighter
                                   :attrs {:color "red"
                                           :class "c2"}}]}]})))))
(deftest parse-annotation-test
  (testing "Basic"
    (is (= [{:id      :a1
             :profile :p1
             :concept "c1"
             :doc     :d1}]
          (sut/parse-annotation (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id "d1"}
                        :content [{:tag     :annotation
                                   :attrs   {:id        "a1"
                                             :annotator "p1"}
                                   :content [{:tag   :class
                                              :attrs {:label "cl1"
                                                      :id    "c1"}}]}]}]}))))
  (testing "Multiple annotations"
    (is (= [{:id      :a1
             :profile :p1
             :concept "c1"
             :doc     :d1}
            {:id      :a2
             :profile :p1
             :concept "c1"
             :doc     :d1}]
          (sut/parse-annotation (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id "d1"}
                        :content [{:tag     :annotation
                                   :attrs   {:id        "a1"
                                             :annotator "p1"}
                                   :content [{:tag   :class
                                              :attrs {:label "cl1"
                                                      :id    "c1"}}]}
                                  {:tag     :annotation
                                   :attrs   {:id        "a2"
                                             :annotator "p1"}
                                   :content [{:tag   :class
                                              :attrs {:label "cl1"
                                                      :id    "c1"}}]}]}]}))))
  (testing "Missing ID"
    (is (= [{:id      :annotation-1
             :profile :p1
             :concept "c1"
             :doc     :d1}]
          (sut/parse-annotation (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id "d1"}
                        :content [{:tag     :annotation
                                   :attrs   {:annotator "p1"}
                                   :content [{:tag   :class
                                              :attrs {:label "cl1"
                                                      :id    "c1"}}]}]}]}))))
  (testing "Missing annotator"
    (is (= [{:id      :a1
             :profile :Default
             :concept "c1"
             :doc     :d1}]
          (sut/parse-annotation (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id "d1"}
                        :content [{:tag     :annotation
                                   :attrs   {:id "a1"}
                                   :content [{:tag   :class
                                              :attrs {:label "cl1"
                                                      :id    "c1"}}]}]}]})))))
(deftest parse-document-test
  (testing "Basic"
    (is (= [{:id        :d1
             :file-name "fn1"}]
          (sut/parse-document (atom 0)
            {:tag     :knowtator-project
             :content [{:tag   :document
                        :attrs {:id        "d1"
                                :text-file "fn1"}}]}))))
  (testing "Multiple documents"
    (is (= [{:id        :d1
             :file-name "fn1"}
            {:id        :d2
             :file-name "fn1"}]
          (sut/parse-document (atom 0)
            {:tag     :knowtator-project
             :content [{:tag   :document
                        :attrs {:id        "d1"
                                :text-file "fn1"}}
                       {:tag   :document
                        :attrs {:id        "d2"
                                :text-file "fn1"}}]}))))
  (testing "Missing ID"
    (is (= [{:id        :document-1
             :file-name "fn1"}]
          (sut/parse-document (atom 0)
            {:tag     :knowtator-project
             :content [{:tag   :document
                        :attrs {:text-file "fn1"}}]}))))

  (testing "Missing file name"
    (is (= [{:id        :d1
             :file-name "d1.txt"}]
          (sut/parse-document (atom 0)
            {:tag     :knowtator-project
             :content [{:tag   :document
                        :attrs {:id "d1"}}]})))))

(deftest parse-documents-test
  (testing "Basic parse documents from annotation files"
    (is (= [5 [{:file-name "document1.txt",
                :id        :document1,
                :content   "This is a test document."}
               {:file-name "document2.txt",
                :id        :document2,
                :content   "And another one!"}
               {:file-name "document3.txt",
                :id        :document3,
                :content   "A second test document has appeared!"}
               {:file-name "document4.txt",
                :id        :document4,
                :content   "Look at me."}
               {:file-name "long_article.txt"
                :id        :long_article}]]
          (->> annotation-xmls
            (sut/parse-documents project-file)
            (sort-by :id)
            vec
            (#(update % 4 dissoc :content))
            ((juxt count identity)))))))

(deftest parse-annotations-test
  (testing "Basic parse annotations from annotation files"
    (is (= [{:id      :mention_0,
             :doc     :document1,
             :profile :Default,
             :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"}
            {:id      :mention_1,
             :doc     :document1,
             :profile :profile1,
             :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#IceCream"}
            {:id      :mention_3,
             :doc     :document2,
             :profile :Default,
             :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"}
            {:id      :mention_0,
             :doc     :document3,
             :profile :Default,
             :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}
            {:id      :mention_0,
             :doc     :document3,
             :profile :Default,
             :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}
            {:id      :mention_1,
             :doc     :document3,
             :profile :profile1,
             :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}
            {:id      :mention_1,
             :doc     :document3,
             :profile :profile1,
             :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}
            {:id      :mention_2,
             :doc     :document3,
             :profile :Default,
             :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}
            {:id      :mention_2,
             :doc     :document3,
             :profile :Default,
             :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}]
          (->> annotation-xmls
            sut/parse-annotations
            (sort-by (juxt :doc :id)))))))

(deftest parse-profiles-test
  (testing "Basic project profile parsing"
    (is (= [{:id :Default,
             :colors
             {"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza" "#ff0000"}}
            {:id :profile1,
             :colors
             {"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza" "#ff3333",
              "http://www.co-ode.org/ontologies/pizza/pizza.owl#IceCream"
              "#00ffff"}}]
          (->> project-file
            (sut/read-project-xmls "Profiles")
            sut/parse-profiles)))))

(deftest parse-spans-test
  (testing "Parse basic project for spans"
    (is (= [{:id :document1-26, :ann :mention_0, :start 0, :end 4}
            {:id :document1-28, :ann :mention_1, :start 10, :end 14}
            {:id :document1-29, :ann :mention_1, :start 15, :end 24}
            {:id :span-1 :ann :mention_3, :start 0, :end 3}
            {:id :document3-11, :ann :mention_0, :start 0, :end 1}
            {:id :document3-14, :ann :mention_1, :start 28, :end 36}
            {:id :document3-17, :ann :mention_2, :start 28, :end 36}
            {:id :document3-11, :ann :mention_0, :start 0, :end 1}
            {:id :span-2 :ann :mention_1, :start 28, :end 36}
            {:id :span-3 :ann :mention_2, :start 28, :end 36}]
          (->> annotation-xmls
            sut/parse-spans)))))

(deftest parse-project-test
  (testing "Basic project"
    (is (= {:anns     [6 #{{:id      :mention_0,
                            :doc     :document1,
                            :profile :Default,
                            :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"}
                           {:id      :mention_1,
                            :doc     :document1,
                            :profile :profile1,
                            :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#IceCream"}
                           {:id      :mention_3,
                            :doc     :document2,
                            :profile :Default,
                            :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"}
                           {:id      :mention_0,
                            :doc     :document3,
                            :profile :Default,
                            :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}
                           {:id      :mention_0,
                            :doc     :document3,
                            :profile :Default,
                            :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}
                           {:id      :mention_1,
                            :doc     :document3,
                            :profile :profile1,
                            :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}
                           {:id      :mention_1,
                            :doc     :document3,
                            :profile :profile1,
                            :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}
                           {:id      :mention_2,
                            :doc     :document3,
                            :profile :Default,
                            :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}
                           {:id      :mention_2,
                            :doc     :document3,
                            :profile :Default,
                            :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}}]
            :docs     [5 [{:file-name "document1.txt",
                           :id        :document1,
                           :content   "This is a test document."}
                          {:file-name "document2.txt",
                           :id        :document2,
                           :content   "And another one!"}
                          {:file-name "document3.txt",
                           :id        :document3,
                           :content   "A second test document has appeared!"}
                          {:file-name "document4.txt",
                           :id        :document4,
                           :content   "Look at me."}
                          {:file-name "long_article.txt"
                           :id        :long_article}]]
            :spans    [7 [{:id :document1-26, :ann :mention_0, :start 0, :end 4}
                          {:id :document1-28, :ann :mention_1, :start 10, :end 14}
                          {:id :document1-29, :ann :mention_1, :start 15, :end 24}
                          {:id :span-1 :ann :mention_3, :start 0, :end 3}
                          {:id :document3-11, :ann :mention_0, :start 0, :end 1}
                          {:id :document3-14, :ann :mention_1, :start 28, :end 36}
                          {:id :document3-17, :ann :mention_2, :start 28, :end 36}
                          {:id :document3-11, :ann :mention_0, :start 0, :end 1}
                          {:id :span-2 :ann :mention_1, :start 28, :end 36}
                          {:id :span-3 :ann :mention_2, :start 28, :end 36}]]
            :graphs   [3]
            :profiles [2 [{:id :Default,
                           :colors
                           {"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza" "#ff0000"}}
                          {:id :profile1,
                           :colors
                           {"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza" "#ff3333",
                            "http://www.co-ode.org/ontologies/pizza/pizza.owl#IceCream"
                            "#00ffff"}}]]}
          (-> project-file
            sut/parse-project
            (update :docs vec)
            (update-in [:docs 4] dissoc :content)
            (->> (util/map-vals (juxt count identity))))))))

;; public static final ProjectCounts defaultCounts = new ProjectCounts(5, 6, 7, 3, 2, 3, 7, 4, 0);
;; defaultExpectedStructureAnnotations = 0;
;; defaultExpectedHighlighters = 3;
;; defaultExpectedAnnotationNodes = 7;
;; defaultExpectedTriples = 4;
;; defaultAnnotationLayers = 1;
