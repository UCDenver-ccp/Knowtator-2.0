(ns knowtator.relation-annotation.subs
  (:require [knowtator.model :as model]
            [knowtator.owl.subs :as owl]
            [knowtator.util :as util]
            [re-frame.core :refer [reg-sub]]
            [com.rpl.specter :as sp]))

(reg-sub ::graph-spaces
  (comp #(or % []) (partial sort-by (comp name :id) util/compare-alpha-num) :graphs :text-annotation))

(reg-sub ::selected-graph-space-id
  (fn [db _]
    (get-in db [:selection :graphs])))

(reg-sub ::selected-graph-space
  :<- [::selected-graph-space-id]
  :<- [::graph-spaces]
  (fn [[graph-id graphs] _]
    (sp/select-one
      [(sp/filterer #(= (:id %) graph-id))
       sp/FIRST]
      graphs)))

(reg-sub ::db
  identity)

(reg-sub ::selected-realized-graph
  :<- [::selected-graph-space]
  :<- [::db]
  :<- [::owl/classes-uri->label]
  :<- [::display-ann-node-owl-class?]

  :<- [::owl/obj-prop-uri->label]
  (fn [[graph db class-map display-owl-class? property-map] _]
    (when graph
      (-> graph
        (model/realize-ann-nodes db class-map display-owl-class?)
        (model/realize-relation-anns property-map)))))

(reg-sub ::graph-physics
  :<- [::selected-graph-space]
  (fn [graph _]
    (get graph :physics false)))

(reg-sub ::display-ann-node-owl-class?
  :<- [::selected-graph-space]
  (fn [graph _]
    (get graph :display-owl-class?)))

(reg-sub ::edge-length
  (fn [db _]
    (get-in db [:selection :edge-length] 95)))
