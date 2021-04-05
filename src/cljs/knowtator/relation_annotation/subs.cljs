(ns knowtator.relation-annotation.subs
  (:require
   [re-frame.core :refer [reg-sub]]
   [knowtator.model :as model]
   [knowtator.util :as util]
   [knowtator.subs :as subs]))

(reg-sub ::graph-spaces
  (comp #(or % []) (partial sort-by (comp name :id) util/compare-alpha-num) :graphs :text-annotation))

(reg-sub ::selected-graph-space-id
  (fn [db _]
    (get-in db [:selection :graphs])))

(reg-sub ::selected-graph-space
  :<- [::selected-graph-space-id]
  :<- [::graph-spaces]
  (fn [[graph-id graphs] _]
    (when graph-id
      (-> graphs
        (->> (util/map-with-key :id))
        graph-id))))

(reg-sub ::db
  identity)

(reg-sub ::obj-props
  (comp :obj-props :ontology))

(reg-sub ::ann-props
  (comp :ann-props :ontology))

(reg-sub ::classes
  (comp :classes :ontology))

(defn make-uri->label-map [owl-entities annotation-iri]
  (->> owl-entities
    (map (fn [{:keys                        [annotation]
              {:keys [namespace fragment]} :iri}]
           [(str namespace fragment)
            (or (->> annotation
                  (filter (fn [{:keys [iri]}] (and iri (= iri annotation-iri))))
                  first
                  :literal
                  :value)
              fragment)]))
    (into {})))

(reg-sub ::obj-prop-uri->label
  :<- [::obj-props]
  :<- [::selected-ann-prop]
  (fn [[obj-props ann-prop] _]
    (make-uri->label-map obj-props ann-prop)))

(reg-sub ::classes-uri->label
  :<- [::classes]
  :<- [::selected-ann-prop]
  (fn [[classes ann-prop] _]
    (make-uri->label-map classes ann-prop)))

(reg-sub ::selected-realized-graph
  :<- [::selected-graph-space]
  :<- [::db]
  :<- [::subs/profile-map]
  :<- [::subs/doc-map]
  :<- [::subs/ann-map]
  :<- [::classes-uri->label]
  :<- [::display-ann-node-owl-class?]

  :<- [::obj-prop-uri->label]
  (fn [[graph db profile-map doc-map ann-map class-map display-owl-class? property-map] _]
    (when graph
      (-> graph
        (model/realize-ann-nodes db profile-map doc-map ann-map class-map display-owl-class?)
        (model/realize-relation-anns property-map)))))

(reg-sub ::graph-physics
  :<- [::selected-graph-space]
  (fn [graph _]
    (get graph :physics false)))

(reg-sub ::display-ann-node-owl-class?
  :<- [::selected-graph-space]
  (fn [graph _]
    (get graph :display-owl-class?)))

(reg-sub ::selected-ann-prop
  (fn [db _]
    (get-in db [:selection :ann-props])))

(reg-sub ::edge-length
  (fn [db _]
    (get-in db [:selection :edge-length] 95)))
