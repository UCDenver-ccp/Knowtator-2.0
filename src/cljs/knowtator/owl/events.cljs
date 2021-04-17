(ns knowtator.owl.events
  (:require [re-frame.core :refer [reg-event-db]]
            [knowtator.util :as util]
            [clojure.set :as set]
            [knowtator.owl.util :refer [map->iri]]))

(reg-event-db ::set-ontology
  (fn [db [_ result]]
    (-> db
        (assoc :ontology result)
        (assoc-in [:selection :ann-props]
                  (->> result
                       :ann-props
                       first
                       map->iri))
        (assoc-in [:selection :concepts]
                  (->> result
                       :classes
                       first
                       map->iri))
        (assoc-in [:selection :obj-props]
                  (->> result
                       :obj-props
                       first
                       map->iri)))))

(defn select-hierarchy-node
  [db k h iri]
  (-> db
      (update-in [:ontology k]
                 (fn [classes]
                   (let [h       (get-in db [:ontology h])
                         to-show (->> iri
                                      (ancestors h))
                         to-hide (-> to-show
                                     (->> (mapcat (partial descendants h)))
                                     set
                                     (set/difference to-show))]
                     (as-> classes m
                       (util/map-with-key map->iri classes)
                       (reduce (fn [m id]
                                 (assoc-in m
                                   [id :visible?]
                                   true))
                         m
                         to-show)
                       (reduce (fn [m id]
                                 (assoc-in m
                                   [id :visible?]
                                   false))
                         m
                         to-hide)
                       (vals m)))))))

(reg-event-db ::select-owl-class
  (fn [db [_ iri]]
    (-> db
        (assoc-in [:selection :concepts] iri)
        (select-hierarchy-node :classes :class-hierarchy iri))))

(reg-event-db ::select-owl-obj-prop
  (fn [db [_ iri]]
    (-> db
        (assoc-in [:selection :obj-props] iri)
        (select-hierarchy-node :obj-props :obj-prop-hierarchy iri))))

(reg-event-db ::select-ann-prop
  (fn [db [_ id]] (assoc-in db [:selection :ann-props] id)))

(defn toggle-collapse
  [db k iri]
  (update-in db
             [:ontology k]
             (fn [ms]
               (-> ms
                   (->> (util/map-with-key map->iri))
                   (update-in [iri :visible?]
                              not)
                   vals))))

(reg-event-db ::toggle-collapse-owl-class
  (fn [db [_ iri]] (toggle-collapse db :classes iri)))

(reg-event-db ::toggle-collapse-owl-obj-prop
  (fn [db [_ iri]] (toggle-collapse db :obj-props iri)))

(reg-event-db ::select-owl-hierarchy
  (fn [db [_ id]] (assoc-in db [:selection :owl-hierarchy] id)))
