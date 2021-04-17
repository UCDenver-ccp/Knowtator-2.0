(ns knowtator.routes.home
  (:require [clojure.java.io :as io]
            [knowtator.layout :as layout]
            [knowtator.middleware :as middleware]
            [ring.util.http-response :as response]
            [ring.util.response :refer [resource-response]]))

(defn home-page [request] (layout/render request "index.html"))

(defn home-routes
  []
  ["" {:middleware [middleware/wrap-csrf middleware/wrap-formats]}
   ["/" {:get home-page} ["annotation" {:get home-page}]
    ["graph" {:get home-page}]]])
