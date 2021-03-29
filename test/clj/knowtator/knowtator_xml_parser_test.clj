(ns knowtator.knowtator-xml-parser-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer [deftest is testing]]
            [knowtator.knowtator-xml-parser :as sut]
            [knowtator.util :as util]))

(def project-file (io/resource "test_project_using_uris"))
(def project-xml (sut/read-project-xmls project-file))

(def articles (sut/read-articles project-file))

(deftest parse-profile-test
  (testing "Basic"
    (is (= [{:id     :p1
             :colors {"c1" "blue"}}]
          (sut/parse-profiles (atom 0)
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
          (sut/parse-profiles (atom 0)
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
  (testing "Something else between profiles"
    (is (= [{:id     :p1
             :colors {"c1" "blue"}}
            {:id     :p2
             :colors {"c1" "blue"}}]
          (sut/parse-profiles (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :profile
                        :attrs   {:id "p1"}
                        :content [{:tag   :highlighter
                                   :attrs {:color "blue"
                                           :class "c1"}}]}
                       [1 2 3]
                       {:tag     :profile
                        :attrs   {:id "p2"}
                        :content [{:tag   :highlighter
                                   :attrs {:color "blue"
                                           :class "c1"}}]}]}))))
  (testing "Missing ID"
    (is (= [{:id     :profile-1
             :colors {"c1" "blue"}}]
          (sut/parse-profiles (atom 0)
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
          (sut/parse-profiles (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :profile
                        :attrs   {:id "p1"}
                        :content [{:tag   :highlighter
                                   :attrs {:color "blue"
                                           :class "c1"}}
                                  {:tag   :highlighter
                                   :attrs {:color "red"
                                           :class "c2"}}]}]}))))

  (testing "Simple project"
    (is (= [{:id :Default
             :colors
             {"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza" "#ff0000"}}
            {:id :profile1
             :colors
             {"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza" "#ff3333"
              "http://www.co-ode.org/ontologies/pizza/pizza.owl#IceCream"
              "#00ffff"}}]
          (->> project-file
            sut/read-project-xmls
            (sut/parse-profiles (atom 0)))))))

(deftest parse-annotation-test
  (testing "Basic"
    (is (= [{:id      :a1
             :profile :p1
             :concept "c1"
             :doc     :d1}]
          (sut/parse-annotations (atom 0)
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
          (sut/parse-annotations (atom 0)
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
  #_(testing "Multiple concepts"
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
                                                        :id    "c1"}}
                                               {:tag   :class
                                                :attrs {:label "cl1"
                                                        :id    "c2"}}]}]}]}))))
  (testing "Missing ID"
    (is (= [{:id      :annotation-1
             :profile :p1
             :concept "c1"
             :doc     :d1}]
          (sut/parse-annotations (atom 0)
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
          (sut/parse-annotations (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id "d1"}
                        :content [{:tag     :annotation
                                   :attrs   {:id "a1"}
                                   :content [{:tag   :class
                                              :attrs {:label "cl1"
                                                      :id    "c1"}}]}]}]}))))

  (testing "Simple project"
    (is (= [{:id      :mention_0
             :doc     :document1
             :profile :Default
             :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"}
            {:id      :mention_1
             :doc     :document1
             :profile :profile1
             :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#IceCream"}
            {:id      :mention_3
             :doc     :document2
             :profile :Default
             :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"}
            {:id      :annotation-4
             :doc     :document3
             :profile :Default
             :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}
            {:id      :annotation-5
             :doc     :document3
             :profile :profile1
             :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}
            {:id      :annotation-6
             :doc     :document3
             :profile :Default
             :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}
            {:id      :annotation-7
             :doc     :document3
             :profile :profile1
             :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}
            {:id      :annotation-8
             :doc     :document3
             :profile :Default
             :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}
            {:id      :mention_2
             :doc     :document3
             :profile :Default
             :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}]
          (->> project-xml
            (sut/parse-annotations (atom 0))
            (sort-by (juxt :doc :id :concept)))))))

(deftest parse-graph-space-test
  (testing "Basic"
    (is (= [{:id    :g1
             :doc   :d1
             :nodes [{:id  :n1
                      :ann :a1}
                     {:id  :n2
                      :ann :a2}]
             :edges [{:id   :e1
                      :from :n1
                      :to   :n2}]}]
          (sut/parse-graph-spaces (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id "d1"}
                        :content [{:tag     :graph-space
                                   :attrs   {:id "g1"}
                                   :content [{:tag   :vertex
                                              :attrs {:annotation "a1"
                                                      :id         "n1"}}
                                             {:tag   :vertex
                                              :attrs {:annotation "a2"
                                                      :id         "n2"}}
                                             {:tag   :triple
                                              :attrs {:subject "n1"
                                                      :object  "n2"
                                                      :id      "e1"}}]}]}]}))))
  (testing "Multiple graph spaces"
    (is (= [{:id    :g1
             :doc   :d1
             :nodes [{:id  :n1
                      :ann :a1}
                     {:id  :n2
                      :ann :a2}]
             :edges [{:id   :e1
                      :to   :n2
                      :from :n1}]}
            {:id    :g2
             :doc   :d1
             :nodes [{:id  :n1
                      :ann :a1}
                     {:id  :n2
                      :ann :a2}]
             :edges [{:id   :e1
                      :from :n1
                      :to   :n2}]}]
          (sut/parse-graph-spaces (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id "d1"}
                        :content [{:tag     :graph-space
                                   :attrs   {:id "g1"}
                                   :content [{:tag   :vertex
                                              :attrs {:annotation "a1"
                                                      :id         "n1"}}
                                             {:tag   :vertex
                                              :attrs {:annotation "a2"
                                                      :id         "n2"}}
                                             {:tag   :triple
                                              :attrs {:subject "n1"
                                                      :object  "n2"
                                                      :id      "e1"}}]}
                                  {:tag     :graph-space
                                   :attrs   {:id "g2"}
                                   :content [{:tag   :vertex
                                              :attrs {:annotation "a1"
                                                      :id         "n1"}}
                                             {:tag   :vertex
                                              :attrs {:annotation "a2"
                                                      :id         "n2"}}
                                             {:tag   :triple
                                              :attrs {:subject "n1"
                                                      :object  "n2"
                                                      :id      "e1"}}]}]}]}))))

  (testing "Missing ID"
    (is (= [{:id    :graph-space-1
             :doc   :d1
             :nodes [{:id  :n1
                      :ann :a1}
                     {:id  :n2
                      :ann :a2}]
             :edges [{:id   :e1
                      :from :n1
                      :to   :n2}]}]
          (sut/parse-graph-spaces (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id "d1"}
                        :content [{:tag     :graph-space
                                   :attrs   {}
                                   :content [{:tag   :vertex
                                              :attrs {:annotation "a1"
                                                      :id         "n1"}}
                                             {:tag   :vertex
                                              :attrs {:annotation "a2"
                                                      :id         "n2"}}
                                             {:tag   :triple
                                              :attrs {:subject "n1"
                                                      :object  "n2"
                                                      :id      "e1"}}]}]}]}))))

  (testing "Missing nodes from edges"
    (is (= [{:id    :g1
             :doc   :d1
             :nodes [{:id  :n1
                      :ann :a1}]
             :edges []}]
          (sut/parse-graph-spaces (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id "d1"}
                        :content [{:tag     :graph-space
                                   :attrs   {:id "g1"}
                                   :content [{:tag   :vertex
                                              :attrs {:annotation "a1"
                                                      :id         "n1"}}
                                             {:tag   :triple
                                              :attrs {:subject "n1"
                                                      :object  "n2"
                                                      :id      "e1"}}]}]}]}))))
  #_(testing "Disconnected nodes"
      (is (= [{:id    :g1
               :doc   :d1
               :nodes [{:id  :n1
                        :ann :a1}
                       {:id  :n2
                        :ann :a1}]
               :edges [{:id   :e1
                        :from :n1
                        :to   :n2}]}]
            (sut/parse-graph-space (atom 0)
              {:tag     :knowtator-project
               :content [{:tag     :document
                          :attrs   {:id "d1"}
                          :content [{:tag     :graph-space
                                     :attrs   {:id "g1"}
                                     :content [{:tag   :vertex
                                                :attrs {:annotation "a1"
                                                        :id         "n1"}}
                                               {:tag   :vertex
                                                :attrs {:annotation "a1"
                                                        :id         "n2"}}
                                               {:tag   :vertex
                                                :attrs {:annotation "a1"
                                                        :id         "n3"}}
                                               {:tag   :triple
                                                :attrs {:subject "n1"
                                                        :object  "n2"
                                                        :id      "e1"}}]}]}]}))))
  (testing "Ordering of edges and nodes in xml"
    (is (= [{:id    :g1
             :doc   :d1
             :nodes [{:id  :n1
                      :ann :a1}
                     {:id  :n2
                      :ann :a1}]
             :edges [{:id   :e1
                      :from :n1
                      :to   :n2}]}]
          (sut/parse-graph-spaces (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id "d1"}
                        :content [{:tag     :graph-space
                                   :attrs   {:id "g1"}
                                   :content [{:tag   :vertex
                                              :attrs {:annotation "a1"
                                                      :id         "n1"}}
                                             {:tag   :triple
                                              :attrs {:subject "n1"
                                                      :object  "n2"
                                                      :id      "e1"}}
                                             {:tag   :vertex
                                              :attrs {:annotation "a1"
                                                      :id         "n2"}}]}]}]})))
    (is (= [{:id    :g1
             :doc   :d1
             :nodes [{:id  :n1
                      :ann :a1}
                     {:id  :n2
                      :ann :a1}]
             :edges [{:id   :e1
                      :from :n1
                      :to   :n2}]}]
          (sut/parse-graph-spaces (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id "d1"}
                        :content [{:tag     :graph-space
                                   :attrs   {:id "g1"}
                                   :content [{:tag   :triple
                                              :attrs {:subject "n1"
                                                      :object  "n2"
                                                      :id      "e1"}}
                                             {:tag   :vertex
                                              :attrs {:annotation "a1"
                                                      :id         "n1"}}
                                             {:tag   :vertex
                                              :attrs {:annotation "a1"
                                                      :id         "n2"}}]}]}]})))
    (is (= [{:id    :g1
             :doc   :d1
             :nodes [{:id  :n1
                      :ann :a1}
                     {:id  :n2
                      :ann :a1}]
             :edges [{:id   :e1
                      :from :n1
                      :to   :n2}
                     {:id   :e2
                      :from :n1
                      :to   :n2}]}]
          (sut/parse-graph-spaces (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id "d1"}
                        :content [{:tag     :graph-space
                                   :attrs   {:id "g1"}
                                   :content [{:tag   :triple
                                              :attrs {:subject "n1"
                                                      :object  "n2"
                                                      :id      "e1"}}
                                             {:tag   :triple
                                              :attrs {:subject "n1"
                                                      :object  "n2"
                                                      :id      "e2"}}
                                             {:tag   :vertex
                                              :attrs {:annotation "a1"
                                                      :id         "n1"}}
                                             {:tag   :vertex
                                              :attrs {:annotation "a1"
                                                      :id         "n2"}}]}]}]})))
    (is (= [{:id    :g1
             :doc   :d1
             :nodes [{:id  :n1
                      :ann :a1}
                     {:id  :n2
                      :ann :a1}]
             :edges [{:id   :e1
                      :from :n1
                      :to   :n2}
                     {:id   :e2
                      :from :n1
                      :to   :n2}]}]
          (sut/parse-graph-spaces (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id "d1"}
                        :content [{:tag     :graph-space
                                   :attrs   {:id "g1"}
                                   :content [{:tag   :triple
                                              :attrs {:subject "n1"
                                                      :object  "n2"
                                                      :id      "e1"}}
                                             {:tag   :vertex
                                              :attrs {:annotation "a1"
                                                      :id         "n1"}}
                                             {:tag   :vertex
                                              :attrs {:annotation "a1"
                                                      :id         "n2"}}
                                             {:tag   :triple
                                              :attrs {:subject "n1"
                                                      :object  "n2"
                                                      :id      "e2"}}]}]}]}))))

  (testing "Simple project"
    (is (= [{:id    :graph_0
             :doc   :document1
             :nodes [{:id  :node_0
                      :ann :mention_0}
                     {:id  :node_1
                      :ann :mention_1}]
             :edges [{:id :edge_0

                      :from :node_0
                      :to   :node_1}]}
            {:id    :graph_2
             :doc   :document2
             :nodes [{:id  :node_0
                      :ann :mention_3}
                     {:id  :node_1
                      :ann :mention_3}]
             :edges [{:id   :edge_0
                      :to   :node_1
                      :from :node_0}]}
            {:id    (keyword "Old Knowtator Relations")
             :doc   :document3
             :nodes [{:id  :document3-19
                      :ann :mention_0}
                     {:id  :document3-20
                      :ann :mention_1}
                     {:id  :document3-22
                      :ann :mention_2}]
             :edges [{:id   :document3-21
                      :to   :document3-20
                      :from :document3-19}
                     {:id   :document3-23
                      :from :document3-19
                      :to   :document3-22}]}]
          (sut/parse-graph-spaces (atom 0) project-xml)))))

(deftest parse-span-test
  (testing "Basic"
    (is (= [{:id    :s1
             :ann   :a1
             :start 0
             :end   1}]
          (sut/parse-spans (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {}
                        :content [{:tag     :annotation
                                   :attrs   {:id "a1"}
                                   :content [{:tag   :span
                                              :attrs {:id    "s1"
                                                      :start "0"
                                                      :end   "1"}}]}]}]}))))
  (testing "Multiple spans"
    (is (= [{:id    :s1
             :ann   :a1
             :start 0
             :end   1}]
          (sut/parse-spans (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {}
                        :content [{:tag     :annotation
                                   :attrs   {:id "a1"}
                                   :content [{:tag   :span
                                              :attrs {:id    "s1"
                                                      :start "0"
                                                      :end   "1"}}
                                             {:tag   :span
                                              :attrs {:id    "s2"
                                                      :start "0"
                                                      :end   "1"}}]}]}]})))
    (is (= [{:id    :s1
             :ann   :a1
             :start 0
             :end   1}
            {:id    :s2
             :ann   :a1
             :start 0
             :end   2}]
          (sut/parse-spans (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {}
                        :content [{:tag     :annotation
                                   :attrs   {:id "a1"}
                                   :content [{:tag   :span
                                              :attrs {:id    "s1"
                                                      :start "0"
                                                      :end   "1"}}
                                             {:tag   :span
                                              :attrs {:id    "s2"
                                                      :start "0"
                                                      :end   "2"}}]}]}]}))))
  (testing "Multiple annotations"
    (is (= [{:id :document3-11 :ann :mention_0 :start 0 :end 1}
            {:id :document3-14 :ann :mention_1 :start 28 :end 36}
            {:id :document3-17 :ann :mention_2 :start 28 :end 36}]
          (->> {:tag     :knowtator-project
                :content [{:tag     :document
                           :attrs   {}
                           :content [{:tag     :annotation
                                      :attrs   {:annotator  "Default"
                                                :id         "mention_0"
                                                :motivation ""
                                                :type       "identity"}
                                      :content [{:tag   :span
                                                 :attrs {:end   "1"
                                                         :id    "document3-11"
                                                         :start "0"}}]}
                                     {:tag     :annotation
                                      :attrs   {:annotator  "profile1"
                                                :id         "mention_1"
                                                :motivation ""
                                                :type       "identity"}
                                      :content [{:tag   :span
                                                 :attrs {:end   "36"
                                                         :id    "document3-14"
                                                         :start "28"}}]}
                                     {:tag     :annotation
                                      :attrs   {:annotator  "Default"
                                                :id         "mention_2"
                                                :motivation ""
                                                :type       "identity"}
                                      :content [{:tag     :span
                                                 :attrs   {:end   "36"
                                                           :id    "document3-17"
                                                           :start "28"}
                                                 :content ["ppeared!"]}]}]}]}
            (sut/parse-spans (atom 0))))))

  (testing "Missing ID"
    (is (= [{:id    :span-1
             :ann   :a1
             :start 0
             :end   1}]
          (sut/parse-spans (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {}
                        :content [{:tag     :annotation
                                   :attrs   {:id "a1"}
                                   :content [{:tag   :span
                                              :attrs {:start "0"
                                                      :end   "1"}}]}]}]}))))
  (testing "Start greater than end"
    (is (= [{:id    :s1
             :ann   :a1
             :start 0
             :end   1}]
          (sut/parse-spans (atom 0)
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {}
                        :content [{:tag     :annotation
                                   :attrs   {:id "a1"}
                                   :content [{:tag   :span
                                              :attrs {:id    "s1"
                                                      :end   "0"
                                                      :start "1"}}]}]}]}))))

  (testing "Simple project"
    (is (= [{:id :document1-26 :ann :mention_0 :start 0 :end 4}
            {:id :document1-28 :ann :mention_1 :start 10 :end 14}
            {:id :document1-29 :ann :mention_1 :start 15 :end 24}
            {:id :document3-11 :ann :mention_0 :start 0 :end 1}
            {:id :document3-14 :ann :mention_1 :start 28 :end 36}
            {:id :document3-17 :ann :mention_2 :start 28 :end 36}
            {:id :span-1 :ann :mention_3 :start 0 :end 3}]
          (->> project-xml
            (sut/parse-spans (atom 0))
            (sort-by :id))))))

(deftest parse-document-test
  (testing "Basic"
    (is (= [{:id        :d1
             :file-name "fn1"}]
          (sut/parse-documents (atom 0) nil
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id        "d1"
                                  :text-file "fn1"}
                        :content []}]}))))
  (testing "Multiple documents"
    (is (= [{:id        :d1
             :file-name "fn1"}
            {:id        :d2
             :file-name "fn1"}]
          (sut/parse-documents (atom 0) nil
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id        "d1"
                                  :text-file "fn1"}
                        :content []}
                       {:tag     :document
                        :attrs   {:id        "d2"
                                  :text-file "fn1"}
                        :content []}]}))))
  (testing "Missing ID"
    (is (= [{:id        :document-1
             :file-name "fn1"}]
          (sut/parse-documents (atom 0) nil
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:text-file "fn1"}
                        :content []}]}))))

  (testing "Missing file name"
    (is (= [{:id        :d1
             :file-name "d1.txt"}]
          (sut/parse-documents (atom 0) nil
            {:tag     :knowtator-project
             :content [{:tag     :document
                        :attrs   {:id "d1"}
                        :content []}]}))))

  (testing "Simple project"
    (is (= [5 [{:file-name "document1.txt"
                :id        :document1
                :content   "This is a test document."}
               {:file-name "document2.txt"
                :id        :document2
                :content   "And another one!"}
               {:file-name "document3.txt"
                :id        :document3
                :content   "A second test document has appeared!"}
               {:file-name "document4.txt"
                :id        :document4
                :content   "Look at me."}
               {:file-name "long_article.txt"
                :id        :long_article}]]
          (->> project-xml
            (sut/parse-documents (atom 0) articles)
            (sort-by :id)
            vec
            (#(update % 4 dissoc :content))
            ((juxt count identity)))))))

(deftest read-articles-test
  (is (= [{:file-name "document1.txt"
           :id        :document1
           :content   "This is a test document."}
          {:file-name "document2.txt"
           :id        :document2
           :content   "And another one!"}
          {:file-name "document3.txt"
           :id        :document3
           :content   "A second test document has appeared!"}
          {:file-name "document4.txt" :id :document4 :content "Look at me."}
          {:file-name "long_article.txt"
           :id        :long_article}]
        (-> project-file
          sut/read-articles
          vec
          (update 4 dissoc :content)))))

(deftest parse-project-test
  (testing "Basic"
    (is (= {:anns     [{:id      :a1
                        :doc     :d1
                        :profile :p1
                        :concept "c1"}
                       {:id      :a2
                        :doc     :d1
                        :profile :Default
                        :concept "c2"}]
            :docs     [{:id        :d1
                        :file-name "tf.txt"
                        :content   "Hi"}]
            :spans    [{:id    :s1
                        :ann   :a1
                        :start 0
                        :end   1}
                       {:id    :s2
                        :ann   :a2
                        :start 0
                        :end   1}
                       {:id    :span-7
                        :ann   :a2
                        :start 1
                        :end   2}]
            :profiles [{:id     :p1
                        :colors {:c1 :default}}
                       {:id     :Default
                        :colors {:c1 "blue"}}]
            :graphs   [{:id    :g1
                        :doc   :d1
                        :nodes [{:id  :n1
                                 :ann :a1}
                                {:id  :node-10
                                 :ann :a2}]
                        :edges [{:id        :e1
                                 :from      :n1
                                 :to        :node-10
                                 #_#_:label :ep1}]}]}
          (sut/parse-project [{:id      :d1
                               :content "Hi"}]
            {:tag     :knowtator-project
             :content [{:tag     :profile
                        :attrs   {:id "p1"}
                        :content [{:tag   :highlighter
                                   :attrs {:class "c1"
                                           :color "blue"}}]}

                       {:tag     :document
                        :attrs   {:id "d1" :text-file "tf.txt"}
                        :content [{:tag     :annotation
                                   :attrs   {:id         "a1"
                                             :annotator  "p1"
                                             :motivation ""
                                             :type       "identity"}
                                   :content [{:tag   :class
                                              :attrs {:id    "c1"
                                                      :label "cl1"}}
                                             {:tag     :span
                                              :attrs   {:id    "s1"
                                                        :start "0"
                                                        :end   "1"}
                                              :content ["H"]}]}
                                  {:tag     :annotation
                                   :attrs   {:id         "a2"
                                             :motivation ""
                                             :type       "identity"}
                                   :content [{:tag   :class
                                              :attrs {:id    "c2"
                                                      :label "cl2"}}
                                             {:tag     :span
                                              :attrs   {:id    "s2"
                                                        :start "0"
                                                        :end   "1"}
                                              :content ["H"]}
                                             {:tag     :span
                                              :attrs   {:start "1"
                                                        :end   "2"}
                                              :content ["i"]}]}
                                  {:tag     :graph-space
                                   :attrs   {:id "g1"}
                                   :content [{:tag   :vertex
                                              :attrs {:annotation "a1"
                                                      :id         "n1"
                                                      :x          "20.0"
                                                      :y          "20.0"}}
                                             {:tag   :vertex
                                              :attrs {:annotation "a2"
                                                      :x          "20.0"
                                                      :y          "20.0"}}
                                             {:tag   :triple
                                              :attrs {:annotator  "Default"
                                                      :id         "e1"
                                                      :motivation ""
                                                      :object     "node-10"
                                                      :polarity   "positive"
                                                      :property   "ep1"
                                                      :quantifier "some"
                                                      :subject    "n1"
                                                      :value      ""}}]}]}]}))))

  (testing "Simple project"
    (is (= {:anns   [9 [{:id      :mention_0
                         :doc     :document1
                         :profile :Default
                         :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"}
                        {:id      :mention_1
                         :doc     :document1
                         :profile :profile1
                         :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#IceCream"}
                        {:id      :mention_3
                         :doc     :document2
                         :profile :Default
                         :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"}
                        {:id      :annotation-4
                         :doc     :document3
                         :profile :Default
                         :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}
                        {:id      :annotation-5
                         :doc     :document3
                         :profile :profile1
                         :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}
                        {:id      :annotation-6
                         :doc     :document3
                         :profile :Default
                         :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}
                        {:id      :annotation-7
                         :doc     :document3
                         :profile :profile1
                         :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}
                        {:id      :annotation-8
                         :doc     :document3
                         :profile :Default
                         :concept "http://www.co-ode.org/ontologies/pizza.owl#Food"}
                        {:id      :mention_2
                         :doc     :document3
                         :profile :Default
                         :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}]]
            :docs   [5 [{:file-name "document1.txt"
                         :id        :document1
                         :content   "This is a test document."}
                        {:file-name "document2.txt"
                         :id        :document2
                         :content   "And another one!"}
                        {:file-name "document3.txt"
                         :id        :document3
                         :content   "A second test document has appeared!"}
                        {:file-name "document4.txt"
                         :id        :document4
                         :content   "Look at me."}
                        {:file-name "long_article.txt"
                         :id        :long_article}]]
            :spans  [7 [{:id :document1-26 :ann :mention_0 :start 0 :end 4}
                        {:id :document1-28 :ann :mention_1 :start 10 :end 14}
                        {:id :document1-29 :ann :mention_1 :start 15 :end 24}
                        {:id :span-15 :ann :mention_3 :start 0 :end 3}
                        {:id :document3-11 :ann :mention_0 :start 0 :end 1}
                        {:id :document3-14 :ann :mention_1 :start 28 :end 36}
                        {:id :document3-17 :ann :mention_2 :start 28 :end 36}]]
            :graphs [3 [{:id    :graph_0
                         :doc   :document1
                         :nodes [{:id  :node_0
                                  :ann :mention_0}
                                 {:id  :node_1
                                  :ann :mention_1}]
                         :edges [{:id :edge_0

                                  :from :node_0
                                  :to   :node_1}]}
                        {:id    :graph_2
                         :doc   :document2
                         :nodes [{:id  :node_0
                                  :ann :mention_3}
                                 {:id  :node_1
                                  :ann :mention_3}]
                         :edges [{:id   :edge_0
                                  :to   :node_1
                                  :from :node_0}]}
                        {:id    (keyword "Old Knowtator Relations")
                         :doc   :document3
                         :nodes [{:id  :document3-19
                                  :ann :mention_0}
                                 {:id  :document3-20
                                  :ann :mention_1}
                                 {:id  :document3-22
                                  :ann :mention_2}]
                         :edges [{:id   :document3-21
                                  :to   :document3-20
                                  :from :document3-19}
                                 {:id   :document3-23
                                  :from :document3-19
                                  :to   :document3-22}]}]]
            :profiles [2 [{:id :Default
                           :colors
                           {"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza" "#ff0000"}}
                          {:id :profile1
                           :colors
                           {"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza" "#ff3333"
                            "http://www.co-ode.org/ontologies/pizza/pizza.owl#IceCream"
                            "#00ffff"}}]]}
          (let [articles (sut/read-articles project-file)
                xml      (sut/read-project-xmls project-file)]
            (-> articles
              (sut/parse-project xml)
              (update :docs vec)
              (update-in [:docs 4] dissoc :content)
              (update :anns (partial sort-by (juxt :doc :id :concept)))
              (->> (util/map-vals (juxt count identity)))))))))

;; public static final ProjectCounts defaultCounts = new ProjectCounts(5 6 7 3 2 3 7 4 0);
;; defaultExpectedStructureAnnotations = 0;
;; defaultExpectedHighlighters = 3;
;; defaultExpectedAnnotationNodes = 7;
;; defaultExpectedTriples = 4;
;; defaultAnnotationLayers = 1;
