(ns knowtator.relation-annotation.subs
  (:require
   [re-frame.core :refer [reg-sub]]
   [knowtator.model :as model]
   [knowtator.util :as util]))

(reg-sub ::graph-spaces
  #(sort-by :id (get-in % [:text-annotation :graphs] [])))

(reg-sub ::selected-graph-space
  (fn [db _]
    (get-in db [:selection :graphs])))

(reg-sub ::db
  identity)

(reg-sub ::selected-realized-graph
  :<- [::selected-graph-space]
  :<- [::graph-spaces]
  :<- [::db]
  (fn [[id graphs db] _]
    (when id
      (-> graphs
        (->> (util/map-with-key :id))
        id
        (update :nodes (partial map (partial model/realize-ann-node db)))))))

(reg-sub ::graph-physics
  (fn [db _]
    (true? (get-in db [:graph :physics]))))
