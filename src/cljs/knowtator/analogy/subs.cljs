(ns knowtator.analogy.subs
  (:require [re-frame.core :refer [reg-sub]]
            [knowtator.model :as model]))

(reg-sub ::analogy-graphs (fn [_ _] {:default (model/get-analogy-map)}))

(reg-sub ::selected-analogy-graph-id (fn [_ _] :default))

(reg-sub ::selected-node-label (fn [_ _] :id))

(reg-sub ::selected-analogy-graph
  :<- [::analogy-graphs]
  :<- [::selected-analogy-graph-id]
  :<- [::selected-node-label]
  (fn [[graphs id node-label] _]
    (-> graphs
        (get id)
        (update :nodes (partial map #(assoc % :label (get % node-label)))))))
