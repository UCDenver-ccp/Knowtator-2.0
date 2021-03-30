(ns knowtator.handler-test
  (:require [knowtator.handler :as sut]
            [clojure.test :refer [testing deftest is]]
            [reitit.core :as r]
            [muuntaja.middleware :as muun-m]
            [muuntaja.core :as m]))

(deftest app-router-test
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
    (is (= {:anns   [{:id      :annotation-4
                      :doc     :document3
                      :profile :Default
                      :concept "http://www.co-ode.org/ontologies/pizza/pizza.owl#Food"}
                     {:id      :mention_0
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
            :docs   [{:file-name "document1.txt"
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
            :spans  [{:id :document3-11 :ann :annotation-7 :start 0 :end 1}
                     {:id :document1-29 :ann :annotation-4 :start 15 :end 24}
                     {:id :document3-14 :ann :annotation-6 :start 28 :end 36}
                     {:id :document1-28 :ann :mention_1 :start 10 :end 14}
                     {:id :document1-26 :ann :mention_0 :start 0 :end 4}
                     {:id :span-2 :ann :mention_3 :start 0 :end 3}
                     {:id :document3-17 :ann :mention_2 :start 28 :end 36}]
            :graphs [{:id    :graph_0
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
            (update-in [:docs] vec)
            (update-in [:docs 4] dissoc :content)
            (update-in [:anns] (partial sort-by (juxt :doc :id :concept))))))))
