(ns knowtator-clj.core
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]
    [knowtator-clj.subs-evts :as se]
    [knowtator-clj.views :as views]
    [knowtator-clj.config :as config]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn init []
  (re-frame/dispatch-sync [::se/initialize-db])
  (dev-setup)
  (mount-root))
