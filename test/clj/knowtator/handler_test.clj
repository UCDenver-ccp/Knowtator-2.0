(ns knowtator.handler-test
  (:require [knowtator.handler :as sut]
            [clojure.test :refer [testing deftest is]]
            [reitit.core :as r]
            [muuntaja.core :as m]
            [knowtator.util :as util]))

(deftest app-router-test
  (testing "Enumerated routes"
    (is (= ["/" "/annotation" "/graph" "/about" "/review"
            "/project/project/:file-name" "/project/doc/:id"
            "/project/ontology/:file-name"]
          (map first (r/routes (sut/app-router))))))

  (testing "Project routes"
    (is (= {:id "123"}
          (:path-params (r/match-by-path (sut/app-router) "/project/doc/123"))))))

(deftest app-routes-test
  (testing "Project routes"
    (is (= {:status 200
            :body   {:content "hello"
                     :id      123}}
          ((sut/start-app-routes) {:request-method :get
                                   :uri            "/project/doc/123"})))
    (is (= {:anns     [{:id      :mention_0,
                        :profile :Default,
                        :doc     :document1,
                        :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza",}
                       {:id      :mention_1,
                        :profile :profile1,
                        :doc     :document1,
                        :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#IceCream",}
                       {:id      :mention_3,
                        :profile :Default,
                        :doc     :document2,
                        :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza",}
                       {:id      :annotation-4,
                        :profile :Default,
                        :doc     :document3,
                        :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food",}
                       {:id      :annotation-5,
                        :profile :profile1,
                        :doc     :document3,
                        :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food",}
                       {:id      :annotation-6,
                        :profile :Default,
                        :doc     :document3,
                        :concept "http://www.co-ode.org/ontologies/pizza.owl#Food",}
                       {:id      :annotation-7,
                        :profile :profile1,
                        :doc     :document3,
                        :concept "http://www.co-ode.org/ontologies/pizza.owl#Food",}
                       {:id      :annotation-8,
                        :profile :Default,
                        :doc     :document3,
                        :concept "http://www.co-ode.org/ontologies/pizza.owl#Food",}
                       {:id      :mention_2,
                        :profile :Default,
                        :doc     :document3,
                        :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food",}]
            :docs     [{:file-name "document1.txt"
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
                        :id        :long_article}]
            :spans    [{:id :document1-26, :start 0, :end 4, :ann :mention_0}
                       {:id :document1-28, :start 10, :end 14, :ann :mention_1}
                       {:id :document1-29, :start 15, :end 24, :ann :mention_1}
                       {:id :document3-11, :start 0, :end 1, :ann :annotation-4}
                       {:id :document3-11, :start 0, :end 1, :ann :annotation-6}
                       {:id :document3-14, :start 28, :end 36, :ann :annotation-5}
                       {:id :document3-17, :start 28, :end 36, :ann :mention_2}
                       {:id :span-1, :start 0, :end 3, :ann :mention_3}
                       {:id :span-2, :start 28, :end 36, :ann :annotation-7}
                       {:id :span-3, :start 28, :end 36, :ann :annotation-8}]
            :graphs   [{:id    :graph_0
                        :doc   :document1
                        :nodes [{:id  :node_0
                                 :ann :mention_0}
                                {:id  :node_1
                                 :ann :mention_1}]
                        :edges [{:id        :edge_0
                                 :from      :node_0
                                 :to        :node_1
                                 :predicate {:polarity   :positive
                                             :property   "http://www.co-ode.org/ontologies/pizza/pizza.owl#hasBase"
                                             :quantifier {:type  :some
                                                          :value nil}}}]}
                       {:id    :graph_2
                        :doc   :document2
                        :nodes [{:id  :node_0
                                 :ann :mention_3}
                                {:id  :node_1
                                 :ann :mention_3}]
                        :edges [{:id        :edge_0
                                 :to        :node_1
                                 :from      :node_0
                                 :predicate {:polarity   nil
                                             :property   "http://www.co-ode.org/ontologies/pizza/pizza.owl#hasBase"
                                             :quantifier {:type  :some
                                                          :value nil}}}]}
                       {:id    (keyword "Old Knowtator Relations")
                        :doc   :document3
                        :nodes [{:id  :document3-19
                                 :ann :mention_0}
                                {:id  :document3-20
                                 :ann :mention_1}
                                {:id  :document3-22
                                 :ann :mention_2}]
                        :edges [{:id        :document3-21
                                 :to        :document3-20
                                 :from      :document3-19
                                 :predicate {:polarity   :positive
                                             :property   "http://www.co-ode.org/ontologies/pizza/pizza.owl#hasIngredient"
                                             :quantifier {:type  :some
                                                          :value nil}}}
                                {:id        :document3-23
                                 :from      :document3-19
                                 :to        :document3-22
                                 :predicate {:polarity   :positive
                                             :property   "http://www.co-ode.org/ontologies/pizza/pizza.owl#hasIngredient"
                                             :quantifier {:type  :some
                                                          :value nil}}}]}]
            :profiles [{:id :Default
                        :colors
                        {"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza" "#ff0000"}}
                       {:id :profile1
                        :colors
                        {"http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza" "#ff3333"
                         "http://www.co-ode.org/ontologies/pizza/pizza.owl#IceCream"
                         "#00ffff"}}]}
          (-> {:request-method :get
               :headers        {"content-type" "application/edn"
                                "accept"       "application/transit+json"}
               :uri            "/project/project/test_project_using_uris"}
            ((sut/start-app-routes))
            m/decode-response-body
            (update-in [:docs 4] dissoc :content)
            (update :spans (partial sort-by (juxt :id :ann)))
            (update-in [:anns] (partial sort-by (juxt :doc :id :concept))))))
    (is (= {:ontology    5
            :obj-props   8
            :classes     100
            :ann-props   12
            :data-props  0
            :individuals 5}
          (->> {:request-method :get
                :headers        {"content-type" "application/edn"
                                 "accept"       "application/transit+json"}
                :uri            "/project/ontology/test_project_using_uris"}
            ((sut/start-app-routes))
            m/decode-response-body
            (util/map-vals count))))))
