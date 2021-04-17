(ns knowtator.protege.client
  (:require [aleph.http :as http]
            [manifold.stream :as s]
            [ring.middleware.transit :refer [decode]]))

(def host "localhost")
(def port 10003)
(def url (format "ws://%s:%d" host port))

(comment (let [protege-conn (->> @(http/websocket-client (str url "/protege"))
                                 (s/map decode))]
           (println "new: " @(s/take! protege-conn))
           #_@(s/consume #(println "message: " %) protege-conn)))
