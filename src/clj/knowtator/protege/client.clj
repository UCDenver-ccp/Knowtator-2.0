(ns knowtator.protege.client
  (:require [aleph.http :as http]
            [byte-streams :as bs]
            [cognitect.transit :as t]
            [manifold.stream :as s]))

(def host "localhost")
(def port 10003)
(def url (format "ws://%s:%d" host port))

(def protege-conn @(http/websocket-client (str url "/protege")))

(+ 1 1)
(defn read-message [m]
  (-> m
    bs/to-input-stream
    (t/reader :json)
    t/read))

(read-message @(s/take! protege-conn))

@(s/take! ( protege-conn))
(s/consume #(println "message: " %) protege-conn)
