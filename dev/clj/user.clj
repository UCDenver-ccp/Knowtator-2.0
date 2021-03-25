(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require [clojure.pprint :refer [pprint]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [knowtator.core :as knowtator]
            [mount.core :as mount]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(add-tap (bound-fn* pprint))

(defn start
  "Starts application.
  You'll usually want to run this on startup."
  []
  (mount/start-without #'knowtator/repl-server))

(defn stop
  "Stops application."
  []
  (mount/stop-except #'knowtator/repl-server))

(defn restart
  "Restarts application."
  []
  (stop)
  (start))
