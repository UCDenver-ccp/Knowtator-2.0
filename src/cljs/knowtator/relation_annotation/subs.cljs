(ns knowtator.relation-annotation.subs
  (:require
   [re-frame.core :refer [reg-sub]]
   [knowtator.model :as model]))

(reg-sub ::graph
  :graph)

(reg-sub ::realized-graph
  (fn [db _]
    (-> db
      :graph
      (update :nodes (partial map (partial model/realize-ann-node db))))))

(reg-sub ::graph-physics
  (fn [db _]
    (true? (get-in db [:graph :physics]))))
