(ns knowtator.relation-annotation.subs
  (:require
   [re-frame.core :refer [reg-sub]]
   [knowtator.model :as model]
   [knowtator.util :as util]))

(reg-sub ::graph
  :graph)

(defn realize-ann-node
  [db {:keys [ann] :as node}]
  (let [ann (->> db
              :anns
              (util/map-with-key :id)
              ann)]
    (assoc node :color (model/ann-color ann (:profiles db)))))

(reg-sub ::realized-graph
  (fn [db _]
    (-> db
      :graph
      (update :nodes (partial map (partial realize-ann-node db))))))

(reg-sub ::graph-physics
  (fn [db _]
    (true? (get-in db [:graph :physics]))))
