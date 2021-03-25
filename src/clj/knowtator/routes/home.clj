(ns knowtator.routes.home
  (:require [clojure.java.io :as io]
            [knowtator.layout :as layout]
            [knowtator.middleware :as middleware]
            [ring.util.http-response :as response]
            [ring.util.response :refer [resource-response]]))

(defn home-page [request]
  (layout/render request "index.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/ontology"]
   ["/" {:get home-page}]
   ["/annotation" {:get home-page}]
   ["/test" {:get (fn [_]
                    (resource-response "html/test.html"))}]
   ["/docs" {:get (fn [_]
                    (-> "docs/docs.md"
                      io/resource
                      slurp
                      response/ok
                      (response/header "Content-Type" "text/plain; charset=utf-8")))}]])
