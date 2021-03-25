(ns knowtator.knowtator-xml-parser-test
  (:require [clojure.test :refer [deftest is testing]]
            [knowtator.knowtator-xml-parser :as sut]
            [clojure.java.io :as io]
            [knowtator.util :as util]))

(def project-file (io/resource "test_project_using_uris"))
(def annotation-xmls (sut/read-project-xmls "Annotations" project-file))

(deftest parse-documents-test
  (testing "Basic parse documents from annotation files"
    (is (= [5 [{:file-name "document1.txt",
                :id        "document1",
                :content   "This is a test document."}
               {:file-name "document2.txt",
                :id        "document2",
                :content   "And another one!"}
               {:file-name "document3.txt",
                :id        "document3",
                :content   "A second test document has appeared!"}
               {:file-name "document4.txt", :id "document4", :content "Look at me."}
               {:file-name "long_article.txt"
                :id        "long_article"}]]
          (->> annotation-xmls
            (sut/parse-documents project-file)
            (sort-by :id)
            vec
            (#(update % 4 dissoc :content))
            ((juxt count identity)))))))

(deftest parse-annotations-test
  (testing "Basic parse annotations from annotation files"
    (is (= [{:id      "mention_0",
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
             :concept "Food"}]
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

(deftest parse-project-test
  (testing "Basic project"
    (is (= {:anns     [6 #{{:id      "mention_0",
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
                            :concept "Food"}}]
            :docs     [5 [{:file-name "document1.txt",
                           :id        "document1",
                           :content   "This is a test document."}
                          {:file-name "document2.txt",
                           :id        "document2",
                           :content   "And another one!"}
                          {:file-name "document3.txt",
                           :id        "document3",
                           :content   "A second test document has appeared!"}
                          {:file-name "document4.txt", :id "document4", :content "Look at me."}
                          {:file-name "long_article.txt"
                           :id        "long_article"}]]
            :spans    [7]
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
