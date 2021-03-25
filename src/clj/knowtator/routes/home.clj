(ns knowtator.routes.home
  (:require [knowtator.layout :as layout]
            [clojure.java.io :as io]
            [knowtator.middleware :as middleware]
            [ring.util.response :refer [resource-response]]
            [ring.util.http-response :as response]))

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
                    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                      (response/header "Content-Type" "text/plain; charset=utf-8")))}]])
