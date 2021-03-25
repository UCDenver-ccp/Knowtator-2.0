(ns knowtator.handler-test
  (:require [knowtator.handler :as sut]
            [clojure.test :refer [testing deftest is]]
            [reitit.core :as r]))

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
                                   :uri            "/project/doc/123"})))))
