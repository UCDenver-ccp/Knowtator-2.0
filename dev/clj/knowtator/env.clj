(ns knowtator.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [knowtator.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init       (fn []
                 (parser/cache-off!)
                 (log/info "\n-=[knowtator started successfully using the development profile]=-"))
   :stop       (fn []
                 (log/info "\n-=[knowtator has shut down successfully]=-"))
   :middleware wrap-dev})
