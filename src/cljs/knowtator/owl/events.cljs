(ns knowtator.owl.events
  (:require [re-frame.core :refer [reg-event-db]]
            [knowtator.util :as util]))

(reg-event-db ::select-owl-class
  (fn [db [_ iri]]
    (assoc-in db [:selection :concepts] iri)))

(reg-event-db ::select-ann-prop
  (fn [db [_ id]]
    (assoc-in db [:selection :ann-props] id)))

(reg-event-db ::toggle-collapse-owl-class
  (fn [db [_ iri]]
    (update-in db [:ontology :classes]
      (fn [classes]
        (-> classes
          (->> (util/map-with-key (comp (partial apply str) (juxt :namespace :fragment) :iri)))
          (update-in [iri :collapsed?] not)
          vals)))))
