(ns knowtator.relation-annotation.subs
  (:require
   [re-frame.core :refer [reg-sub]]
   [knowtator.model :as model]
   [knowtator.util :as util]
   [knowtator.subs :as subs]))

(reg-sub ::graph-spaces
  #(sort-by :id (get-in % [:text-annotation :graphs] [])))

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

(reg-sub ::ontology
  :ontology)

(reg-sub ::obj-prop-uri->label
  :<- [::ontology]
  (fn [ontology _]
    (->> ontology
      :obj-props
      (map (fn [{:keys                        [annotation]
                {:keys [namespace fragment]} :iri}]
             [(str namespace fragment) fragment]))
      (into {}))))

(reg-sub ::selected-realized-graph
  :<- [::selected-graph-space]
  :<- [::db]
  :<- [::subs/profile-map]
  :<- [::subs/doc-map]
  :<- [::subs/ann-map]
  :<- [::obj-prop-uri->label]
  (fn [[graph db profile-map doc-map ann-map property-map] _]
    (when graph
      (-> graph
        (model/realize-ann-nodes db profile-map doc-map ann-map)
        (model/realize-relation-anns property-map)))))

(reg-sub ::graph-physics
  :<- [::selected-graph-space]
  (fn [graph _]
    (get graph :physics false)))
