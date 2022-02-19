(ns knowtator.env
  (:require
   [clojure.tools.logging :as log]
   [knowtator.dev-middleware :refer [wrap-dev]]
   [selmer.parser         :as parser]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info
      "\n-=[knowtator started successfully using the development profile]=-"))
   :stop (fn [] (log/info "\n-=[knowtator has shut down successfully]=-"))
   :middleware wrap-dev})
