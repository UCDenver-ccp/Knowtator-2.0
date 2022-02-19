(ns knowtator.routes.home
  (:require
   [knowtator.layout     :as layout]
   [knowtator.middleware :as middleware]))

(defn home-page [request] (layout/render request "index.html"))

(defn home-routes
  []
  [""
   {:middleware [middleware/wrap-csrf middleware/wrap-formats]}
   ["/"
    {:get home-page}
    ["annotation" {:get home-page}]
    ["graph" {:get home-page}]]])
