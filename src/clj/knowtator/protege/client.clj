(ns knowtator.protege.client
  (:require #_[compojure.core :as compojure :refer [GET]]
            [ring.middleware.params :as params]
            #_[compojure.route :as route]
            [aleph.http :as http]
            [byte-streams :as bs]
            [manifold.stream :as s]
            [manifold.deferred :as d]
            [manifold.bus :as bus]
            [clojure.core.async :as a]
            [clojure.java.io :as io])
  (:import [java.net Socket]))

(let [conn @(http/websocket-client "ws://localhost:10002/echo")]

  (s/put-all! conn
    (->> 10 range (map str)))

  (->> conn
    (s/transform (take 10))
    s/stream->seq
    doall))    ;=> ("0" "1" "2" "3" "4" "5" "6" "7" "8" "9")


(let [conn1 @(http/websocket-client "ws://localhost:10002/chat")
      conn2 @(http/websocket-client "ws://localhost:10002/chat")
      ]

  ;; sign our two users in
  (s/put-all! conn1 ["shoes and ships" "Alice"])
  (s/put-all! conn2 ["shoes and ships" "Bob"])

  (s/put! conn1 "hello")

  @(s/take! conn1)   ;=> "Alice: hello"
  @(s/take! conn2)   ;=> "Alice: hello"

  (s/put! conn2 "hi!")

  @(s/take! conn1)   ;=> "Bob: hi!"
  @(s/take! conn2)   ;=> "Bob: hi!"
  )

(defn sign-off [msg]
  (println msg)
  msg)

(let [conn (Socket. "localhost" 8000)]
  (loop [last-line nil]
    (if (:closed (bean conn))
      (sign-off "Connection closed")
      (let [new-line (read-line)]
        (println "New line:" new-line)
        (if (= new-line last-line)
          (sign-off "Nothing new")
          (recur new-line))))

    (read-line))
  #_(binding [*in* (io/reader (.getInputStream conn))]
      (read-line)))
