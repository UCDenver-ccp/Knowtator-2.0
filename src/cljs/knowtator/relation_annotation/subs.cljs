(ns knowtator.relation-annotation.subs
  (:require
   [re-frame.core :refer [reg-sub]]
   [knowtator.model :as model]
   [knowtator.util :as util]))

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

(reg-sub ::selected-realized-graph
  :<- [::selected-graph-space]
  :<- [::db]
  (fn [[graph db] _]
    (when graph
      (update graph :nodes (partial map (partial model/realize-ann-node db))))))

(reg-sub ::graph-physics
  :<- [::selected-graph-space]
  (fn [graph _]
    (get graph :physics false)))
