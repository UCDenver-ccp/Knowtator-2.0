(ns knowtator.core
  (:require
   [breaking-point.core :as bp]
   [knowtator.config    :as config]
   [knowtator.events    :as events]
   [knowtator.routes    :as routes]
   [knowtator.views     :as views]
   [re-frame.core       :as rf]
   [re-pressed.core     :as rp]
   [reagent.dom         :as rdom]))

(defn dev-setup [] (when config/debug? (println "dev mode")))

(defn ^:dev/after-load mount-root
  []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init
  []
  (routes/start!)
  (rf/dispatch-sync [::events/initialize-db])
  (rf/dispatch-sync [::rp/add-keyboard-event-listener "keydown"])
  (rf/dispatch-sync [::bp/set-breakpoints {:breakpoints [:mobile 768
                                                         :tablet
                                                         992  :small-monitor
                                                         1200 :large-monitor]
                                           :debounce-ms 166}])
  (dev-setup)
  (mount-root))
