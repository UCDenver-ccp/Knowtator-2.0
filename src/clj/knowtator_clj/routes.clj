(ns knowtator-clj.routes
  (:require [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [response]]
            [ring.middleware.transit :as t]
            [clojure.string :as str]))

(defn load-doc
  [id]
  (let [r (-> (str "test/test_project/Articles/" id)
              (str ".txt")
              (io/resource)
              (slurp))]
    {:content r
     :id      id}))

(defn summarize-docs
  []
  (->> "test/test_project/Articles/"
       (io/resource)
       (io/file)
       (file-seq)
       (rest)
       (map str)
       (map #(str/split % #"/"))
       (map last)
       (map #(str/split % #"\."))
       (map first)
       (map #(hash-map :id %))))

(defn home-routes [endpoint]
  (routes
    (GET "/" _
      (-> "public/index.html"
          io/resource
          io/input-stream
          response
          (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))
    (resources "/")
    (-> "/document-summary/"
        (GET _
          (-> (summarize-docs)
              (response)))
        (t/wrap-transit-response))
    (-> "/document/"
        (GET [id]
          (-> id (load-doc) (response)))
        (t/wrap-transit-response))))

