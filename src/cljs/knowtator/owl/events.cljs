(ns knowtator.owl.events
  (:require [re-frame.core :refer [reg-event-db]]
            [knowtator.util :as util]
            [clojure.set :as set]))

(reg-event-db ::select-owl-class
  (fn [db [_ iri]]
    (-> db
      (assoc-in [:selection :concepts] iri)
      (update-in [:ontology :classes]
        (fn [classes]
          (let [h       (get-in db [:ontology :hierarchy])
                to-show (->> iri
                          (ancestors h))
                to-hide (-> to-show
                          (->> (mapcat (partial descendants h)))
                          set
                          (set/difference to-show))]
            (as-> classes m
              (util/map-with-key (comp (partial apply str) (juxt :namespace :fragment) :iri) classes)
              (reduce
                (fn [m id]
                  (assoc-in m [id :collapsed?] false))
                m to-show)
              (reduce
                (fn [m id]
                  (assoc-in m [id :collapsed?] true))
                m to-hide)
              (vals m))))))))

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
